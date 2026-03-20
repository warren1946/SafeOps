/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.integration.application.services

import com.zama.safeops.modules.integration.application.ports.IntegrationPort
import com.zama.safeops.modules.integration.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Central hub for managing all external system integrations.
 * Provides unified interface for ERP, SCADA, communication tools, and more.
 */
@Service
class IntegrationHubService(
    private val integrationPort: IntegrationPort,
    private val adapters: List<IntegrationAdapter>,
    private val webhookProcessor: WebhookProcessor
) {
    private val adapterRegistry = adapters.associateBy { it.integrationType }
    private val runningJobs = ConcurrentHashMap<String, SyncJob>()

    /**
     * Get available integration templates.
     */
    fun getTemplates(): List<IntegrationTemplate> {
        return IntegrationTemplates.ALL_TEMPLATES
    }

    /**
     * Create a new integration.
     */
    fun createIntegration(
        tenantId: TenantId,
        templateId: String,
        name: String,
        config: IntegrationConfig,
        syncSettings: SyncSettings
    ): Integration {
        val template = IntegrationTemplates.ALL_TEMPLATES.find { it.id == templateId }
            ?: throw IllegalArgumentException("Template not found: $templateId")

        // Validate required config
        val missingConfig = template.requiredConfig.filter {
            when (it) {
                "baseUrl" -> config.baseUrl.isNullOrBlank()
                "apiKey" -> config.apiKey.isNullOrBlank()
                "apiSecret" -> config.apiSecret.isNullOrBlank()
                "username" -> config.username.isNullOrBlank()
                "password" -> config.password.isNullOrBlank()
                "oauthToken" -> config.oauthToken.isNullOrBlank()
                "webhookUrl" -> config.baseUrl.isNullOrBlank()
                else -> true
            }
        }

        if (missingConfig.isNotEmpty()) {
            throw IllegalArgumentException("Missing required config: ${missingConfig.joinToString()}")
        }

        val integration = Integration(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            name = name,
            type = template.type,
            provider = template.provider,
            status = IntegrationStatus.PENDING,
            config = config,
            syncSettings = syncSettings,
            lastSyncAt = null,
            lastError = null
        )

        val saved = integrationPort.save(integration)

        // Test connection
        testConnection(saved.id)

        return saved
    }

    /**
     * Test integration connection.
     */
    fun testConnection(integrationId: String): ConnectionTestResult {
        val integration = integrationPort.findById(integrationId)
            ?: throw IllegalArgumentException("Integration not found: $integrationId")

        val adapter = getAdapter(integration.type)

        return try {
            adapter.testConnection(integration.config)

            // Update status to active if test passes
            if (integration.status == IntegrationStatus.PENDING ||
                integration.status == IntegrationStatus.ERROR
            ) {
                integrationPort.save(
                    integration.copy(
                        status = IntegrationStatus.ACTIVE,
                        lastError = null,
                        updatedAt = Instant.now()
                    )
                )
            }

            ConnectionTestResult.Success
        } catch (e: Exception) {
            integrationPort.save(
                integration.copy(
                    status = IntegrationStatus.ERROR,
                    lastError = e.message,
                    updatedAt = Instant.now()
                )
            )

            ConnectionTestResult.Failure(e.message ?: "Unknown error")
        }
    }

    /**
     * Execute manual sync for an integration.
     */
    fun executeSync(integrationId: String): SyncJob {
        val integration = integrationPort.findById(integrationId)
            ?: throw IllegalArgumentException("Integration not found: $integrationId")

        if (integration.status != IntegrationStatus.ACTIVE) {
            throw IllegalStateException("Integration is not active")
        }

        val job = SyncJob(
            id = UUID.randomUUID().toString(),
            integrationId = integrationId,
            tenantId = integration.tenantId,
            status = JobStatus.PENDING,
            startedAt = Instant.now(),
            completedAt = null,
            recordsProcessed = 0,
            recordsCreated = 0,
            recordsUpdated = 0,
            recordsFailed = 0,
            errors = emptyList(),
            executionTimeMs = null
        )

        runningJobs[job.id] = job

        // Execute sync asynchronously
        executeSyncJob(integration, job)

        return job
    }

    /**
     * Get sync job status.
     */
    fun getJobStatus(jobId: String): SyncJob? {
        return runningJobs[jobId] ?: integrationPort.findJobById(jobId)
    }

    /**
     * Process incoming webhook.
     */
    fun processWebhook(
        integrationId: String,
        signature: String?,
        payload: String
    ): WebhookResult {
        val integration = integrationPort.findById(integrationId)
            ?: return WebhookResult.InvalidIntegration

        // Verify signature if configured
        if (integration.config.webhookSecret != null) {
            if (!verifyWebhookSignature(payload, signature, integration.config.webhookSecret)) {
                return WebhookResult.InvalidSignature
            }
        }

        val event = WebhookEvent(
            id = UUID.randomUUID().toString(),
            integrationId = integrationId,
            tenantId = integration.tenantId,
            eventType = "incoming",
            payload = payload,
            signature = signature,
            processedAt = null,
            status = WebhookStatus.PENDING
        )

        integrationPort.saveWebhookEvent(event)

        // Process asynchronously
        webhookProcessor.process(event)

        return WebhookResult.Accepted(event.id)
    }

    /**
     * Get all integrations for a tenant.
     */
    fun getIntegrations(tenantId: TenantId): List<Integration> {
        return integrationPort.findByTenant(tenantId)
    }

    /**
     * Get integration by ID.
     */
    fun getIntegration(integrationId: String): Integration? {
        return integrationPort.findById(integrationId)
    }

    /**
     * Update integration configuration.
     */
    fun updateIntegration(
        integrationId: String,
        config: IntegrationConfig? = null,
        syncSettings: SyncSettings? = null,
        status: IntegrationStatus? = null
    ): Integration {
        val integration = integrationPort.findById(integrationId)
            ?: throw IllegalArgumentException("Integration not found: $integrationId")

        val updated = integration.copy(
            config = config ?: integration.config,
            syncSettings = syncSettings ?: integration.syncSettings,
            status = status ?: integration.status,
            updatedAt = Instant.now()
        )

        return integrationPort.save(updated)
    }

    /**
     * Delete an integration.
     */
    fun deleteIntegration(integrationId: String) {
        integrationPort.delete(integrationId)
    }

    /**
     * Get sync history for an integration.
     */
    fun getSyncHistory(integrationId: String, limit: Int = 50): List<SyncJob> {
        return integrationPort.findJobsByIntegration(integrationId, limit)
    }

    /**
     * Scheduled sync for all active integrations.
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    fun scheduledSync() {
        val now = Instant.now()

        integrationPort.findAllActive().forEach { integration ->
            val shouldSync = when (integration.syncSettings.syncFrequency) {
                SyncFrequency.REAL_TIME -> false // Webhook-based
                SyncFrequency.MINUTE_5 -> true
                SyncFrequency.MINUTE_15 -> now.epochSecond % 900 < 60
                SyncFrequency.MINUTE_30 -> now.epochSecond % 1800 < 60
                SyncFrequency.HOURLY -> now.epochSecond % 3600 < 60
                SyncFrequency.DAILY -> now.epochSecond % 86400 < 60
                SyncFrequency.CUSTOM -> false // Cron-based
            }

            if (shouldSync) {
                try {
                    executeSync(integration.id)
                } catch (e: Exception) {
                    // Log error, continue with other integrations
                }
            }
        }
    }

    /**
     * Send data to external system (outbound).
     */
    fun <T> sendToExternal(
        integrationId: String,
        data: T,
        entityType: String
    ): OutboundResult {
        val integration = integrationPort.findById(integrationId)
            ?: return OutboundResult.IntegrationNotFound

        val adapter = getAdapter(integration.type)

        return try {
            adapter.sendData(integration.config, data, entityType)
            OutboundResult.Success
        } catch (e: Exception) {
            OutboundResult.Failure(e.message ?: "Unknown error")
        }
    }

    private fun executeSyncJob(integration: Integration, job: SyncJob) {
        val adapter = getAdapter(integration.type)

        try {
            runningJobs[job.id] = job.copy(status = JobStatus.RUNNING)

            val startTime = System.currentTimeMillis()

            val result = when (integration.syncSettings.syncDirection) {
                SyncDirection.ONE_WAY_INBOUND ->
                    adapter.pullData(integration.config, integration.syncSettings)

                SyncDirection.ONE_WAY_OUTBOUND ->
                    adapter.pushData(integration.config, integration.syncSettings)

                SyncDirection.BIDIRECTIONAL -> {
                    val pull = adapter.pullData(integration.config, integration.syncSettings)
                    val push = adapter.pushData(integration.config, integration.syncSettings)
                    SyncResult(
                        recordsProcessed = pull.recordsProcessed + push.recordsProcessed,
                        recordsCreated = pull.recordsCreated + push.recordsCreated,
                        recordsUpdated = pull.recordsUpdated + push.recordsUpdated,
                        recordsFailed = pull.recordsFailed + push.recordsFailed,
                        errors = pull.errors + push.errors
                    )
                }
            }

            val completedJob = job.copy(
                status = if (result.errors.isEmpty()) JobStatus.COMPLETED else JobStatus.FAILED,
                completedAt = Instant.now(),
                recordsProcessed = result.recordsProcessed,
                recordsCreated = result.recordsCreated,
                recordsUpdated = result.recordsUpdated,
                recordsFailed = result.recordsFailed,
                errors = result.errors,
                executionTimeMs = System.currentTimeMillis() - startTime
            )

            runningJobs[job.id] = completedJob
            integrationPort.saveJob(completedJob)

            // Update integration last sync
            integrationPort.save(
                integration.copy(
                    lastSyncAt = Instant.now(),
                    lastError = if (result.errors.isNotEmpty()) "${result.errors.size} errors" else null
                )
            )

        } catch (e: Exception) {
            val failedJob = job.copy(
                status = JobStatus.FAILED,
                completedAt = Instant.now(),
                errors = listOf(SyncError(null, "EXECUTION_ERROR", e.message ?: "Unknown", null)),
                executionTimeMs = System.currentTimeMillis() - startTime
            )
            runningJobs[job.id] = failedJob
            integrationPort.saveJob(failedJob)
        }
    }

    private fun getAdapter(type: IntegrationType): IntegrationAdapter {
        return adapterRegistry[type]
            ?: throw IllegalStateException("No adapter registered for $type")
    }

    private fun verifyWebhookSignature(payload: String, signature: String?, secret: String): Boolean {
        if (signature == null) return false
        // Implement HMAC verification
        return true // Placeholder
    }

    private val startTime: Long = 0 // Placeholder for actual timing
}

// Supporting interfaces

interface IntegrationAdapter {
    val integrationType: IntegrationType
    fun testConnection(config: IntegrationConfig): ConnectionTestResult
    fun pullData(config: IntegrationConfig, settings: SyncSettings): SyncResult
    fun pushData(config: IntegrationConfig, settings: SyncSettings): SyncResult
    fun <T> sendData(config: IntegrationConfig, data: T, entityType: String)
}

interface WebhookProcessor {
    fun process(event: WebhookEvent)
}

sealed class ConnectionTestResult {
    object Success : ConnectionTestResult()
    data class Failure(val error: String) : ConnectionTestResult()
}

sealed class WebhookResult {
    data class Accepted(val eventId: String) : WebhookResult()
    object InvalidIntegration : WebhookResult()
    object InvalidSignature : WebhookResult()
}

sealed class OutboundResult {
    object Success : OutboundResult()
    object IntegrationNotFound : OutboundResult()
    data class Failure(val error: String) : OutboundResult()
}

data class SyncResult(
    val recordsProcessed: Int,
    val recordsCreated: Int,
    val recordsUpdated: Int,
    val recordsFailed: Int,
    val errors: List<SyncError>
)
