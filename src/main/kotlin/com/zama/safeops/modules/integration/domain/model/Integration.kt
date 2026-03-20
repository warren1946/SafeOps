/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.integration.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * External system integration configuration.
 */
data class Integration(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val type: IntegrationType,
    val provider: String,
    val status: IntegrationStatus,
    val config: IntegrationConfig,
    val syncSettings: SyncSettings,
    val lastSyncAt: Instant?,
    val lastError: String?,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class IntegrationType {
    ERP,              // SAP, Oracle, Dynamics
    SCADA,            // Industrial control systems
    COMMUNICATION,    // Slack, Teams, WhatsApp Business
    BI,               // Power BI, Tableau
    HR,               // Workday, SAP SuccessFactors
    FINANCE,          // Accounting systems
    CRM,              // Salesforce, HubSpot
    GIS,              // ArcGIS, Google Maps
    WEATHER,          // Weather APIs
    DRONE,            // DroneDeploy, Pix4D
    CUSTOM            // Webhooks, custom APIs
}

enum class IntegrationStatus {
    PENDING,      // Configuration pending
    ACTIVE,       // Connected and syncing
    ERROR,        // Connection error
    PAUSED,       // Temporarily disabled
    DISCONNECTED  // Permanently disabled
}

data class IntegrationConfig(
    val baseUrl: String?,
    val apiKey: String?,
    val apiSecret: String?,
    val username: String?,
    val password: String?,
    val oauthToken: String?,
    val webhookSecret: String?,
    val customHeaders: Map<String, String>,
    val timeoutMs: Int = 30000,
    val retryAttempts: Int = 3,
    val additionalProperties: Map<String, String>
)

data class SyncSettings(
    val syncDirection: SyncDirection,
    val syncFrequency: SyncFrequency,
    val customCronExpression: String?,  // For custom frequency
    val dataMappings: List<DataMapping>,
    val filters: SyncFilters
)

enum class SyncDirection {
    ONE_WAY_INBOUND,   // External → SafeOps
    ONE_WAY_OUTBOUND,  // SafeOps → External
    BIDIRECTIONAL      // Both directions
}

enum class SyncFrequency {
    REAL_TIME,     // Webhook-based
    MINUTE_5,
    MINUTE_15,
    MINUTE_30,
    HOURLY,
    DAILY,
    CUSTOM
}

data class DataMapping(
    val sourceField: String,
    val targetField: String,
    val transformation: TransformationType,
    val defaultValue: String?
)

enum class TransformationType {
    DIRECT,           // No transformation
    UPPERCASE,
    LOWERCASE,
    DATE_FORMAT,      // Requires format specification
    LOOKUP,           // Lookup table
    CONCATENATE,      // Join multiple fields
    SPLIT,            // Split field
    MATH,             // Mathematical operation
    CUSTOM            // Custom code transformation
}

data class SyncFilters(
    val includeConditions: List<SyncCondition>,
    val excludeConditions: List<SyncCondition>
)

data class SyncCondition(
    val field: String,
    val operator: Operator,
    val value: String
)

enum class Operator {
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    GREATER_THAN,
    LESS_THAN,
    IS_EMPTY,
    IS_NOT_EMPTY
}

/**
 * Sync job execution record.
 */
data class SyncJob(
    val id: String,
    val integrationId: String,
    val tenantId: TenantId,
    val status: JobStatus,
    val startedAt: Instant,
    var completedAt: Instant?,
    val recordsProcessed: Int,
    val recordsCreated: Int,
    val recordsUpdated: Int,
    val recordsFailed: Int,
    val errors: List<SyncError>,
    val executionTimeMs: Long?
)

enum class JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class SyncError(
    val recordId: String?,
    val errorCode: String,
    val errorMessage: String,
    val rawData: String?,
    val timestamp: Instant = Instant.now()
)

/**
 * Webhook event for real-time integrations.
 */
data class WebhookEvent(
    val id: String,
    val integrationId: String,
    val tenantId: TenantId,
    val eventType: String,
    val payload: String,
    val signature: String?,
    val receivedAt: Instant = Instant.now(),
    val processedAt: Instant?,
    val status: WebhookStatus
)

enum class WebhookStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    FAILED,
    RETRYING
}

/**
 * Pre-built integration templates.
 */
object IntegrationTemplates {

    val SAP_ERP = IntegrationTemplate(
        id = "sap_erp",
        name = "SAP ERP",
        type = IntegrationType.ERP,
        provider = "SAP",
        description = "Sync personnel, assets, and cost centers with SAP",
        requiredConfig = listOf("baseUrl", "username", "password"),
        defaultMappings = listOf(
            DataMapping("PERNR", "employeeId", TransformationType.DIRECT, null),
            DataMapping("VORNA", "firstName", TransformationType.DIRECT, null),
            DataMapping("NACHN", "lastName", TransformationType.DIRECT, null),
            DataMapping("ORGTX", "department", TransformationType.DIRECT, null)
        )
    )

    val SLACK = IntegrationTemplate(
        id = "slack",
        name = "Slack",
        type = IntegrationType.COMMUNICATION,
        provider = "Slack",
        description = "Send safety alerts and notifications to Slack channels",
        requiredConfig = listOf("apiKey", "channelId"),
        defaultMappings = emptyList()
    )

    val MICROSOFT_TEAMS = IntegrationTemplate(
        id = "microsoft_teams",
        name = "Microsoft Teams",
        type = IntegrationType.COMMUNICATION,
        provider = "Microsoft",
        description = "Send safety alerts and notifications to Teams channels",
        requiredConfig = listOf("webhookUrl"),
        defaultMappings = emptyList()
    )

    val DRONE_DEPLOY = IntegrationTemplate(
        id = "drone_deploy",
        name = "DroneDeploy",
        type = IntegrationType.DRONE,
        provider = "DroneDeploy",
        description = "Import drone imagery for inspections and mapping",
        requiredConfig = listOf("apiKey"),
        defaultMappings = emptyList()
    )

    val ARC_GIS = IntegrationTemplate(
        id = "arcgis",
        name = "ArcGIS",
        type = IntegrationType.GIS,
        provider = "Esri",
        description = "Sync mine maps and spatial data with ArcGIS",
        requiredConfig = listOf("baseUrl", "apiKey"),
        defaultMappings = emptyList()
    )

    val SALESFORCE = IntegrationTemplate(
        id = "salesforce",
        name = "Salesforce",
        type = IntegrationType.CRM,
        provider = "Salesforce",
        description = "Sync contractor and client information",
        requiredConfig = listOf("baseUrl", "oauthToken"),
        defaultMappings = emptyList()
    )

    val ALL_TEMPLATES = listOf(SAP_ERP, SLACK, MICROSOFT_TEAMS, DRONE_DEPLOY, ARC_GIS, SALESFORCE)
}

data class IntegrationTemplate(
    val id: String,
    val name: String,
    val type: IntegrationType,
    val provider: String,
    val description: String,
    val requiredConfig: List<String>,
    val defaultMappings: List<DataMapping>
)

/**
 * API credential store (encrypted).
 */
data class StoredCredential(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val credentialType: CredentialType,
    val encryptedData: String,
    val expiresAt: Instant?,
    val lastUsedAt: Instant?,
    val createdAt: Instant = Instant.now()
)

enum class CredentialType {
    API_KEY,
    OAUTH_TOKEN,
    BASIC_AUTH,
    CERTIFICATE,
    WEBHOOK_SECRET
}
