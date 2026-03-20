/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.application.services

import com.zama.safeops.modules.audit.application.ports.AuditAction
import com.zama.safeops.modules.audit.application.ports.AuditPort
import com.zama.safeops.modules.audit.application.ports.AuditSearchCriteria
import com.zama.safeops.modules.audit.domain.model.AuditLog
import com.zama.safeops.modules.audit.domain.model.AuditLogBuilder
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Service for managing audit logging.
 *
 * Features:
 * - Asynchronous logging for performance
 * - Batch processing for high throughput
 * - Automatic buffering and flushing
 * - GDPR-compliant data handling
 */
@Service
class AuditService(
    private val auditPort: AuditPort
) {
    private val buffer = ConcurrentLinkedQueue<AuditLog>()

    companion object {
        const val BATCH_SIZE = 100
        const val BUFFER_SIZE_THRESHOLD = 50
    }

    /**
     * Log an audit entry asynchronously.
     * This method returns immediately and logs in the background.
     */
    @Async
    fun logAsync(auditLog: AuditLog) {
        buffer.offer(auditLog)

        if (buffer.size >= BUFFER_SIZE_THRESHOLD) {
            flushBuffer()
        }
    }

    /**
     * Log an audit entry synchronously.
     * Use this for critical operations that must be logged before returning.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun logSync(auditLog: AuditLog): AuditLog {
        return auditPort.save(auditLog)
    }

    /**
     * Log an operation with builder pattern.
     */
    fun log(block: AuditLogBuilder.() -> Unit) {
        val builder = AuditLogBuilder()
        block(builder)
        logAsync(builder.build())
    }

    /**
     * Log entity creation.
     */
    fun logCreate(
        tenantId: TenantId,
        userId: Long?,
        entityType: String,
        entityId: String,
        newValues: Map<String, Any?>,
        ipAddress: String? = null,
        userAgent: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            action(AuditAction.CREATE)
            entityType(entityType)
            entityId(entityId)
            description("Created $entityType with ID $entityId")
            newValues(newValues)
            ipAddress(ipAddress)
            userAgent(userAgent)
        }
    }

    /**
     * Log entity update.
     */
    fun logUpdate(
        tenantId: TenantId,
        userId: Long?,
        entityType: String,
        entityId: String,
        oldValues: Map<String, Any?>,
        newValues: Map<String, Any?>,
        ipAddress: String? = null,
        userAgent: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            action(AuditAction.UPDATE)
            entityType(entityType)
            entityId(entityId)
            description("Updated $entityType with ID $entityId")
            oldValues(oldValues)
            newValues(newValues)
            ipAddress(ipAddress)
            userAgent(userAgent)
        }
    }

    /**
     * Log entity deletion.
     */
    fun logDelete(
        tenantId: TenantId,
        userId: Long?,
        entityType: String,
        entityId: String,
        oldValues: Map<String, Any?>? = null,
        ipAddress: String? = null,
        userAgent: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            action(AuditAction.DELETE)
            entityType(entityType)
            entityId(entityId)
            description("Deleted $entityType with ID $entityId")
            oldValues(oldValues)
            ipAddress(ipAddress)
            userAgent(userAgent)
        }
    }

    /**
     * Log user login.
     */
    fun logLogin(
        tenantId: TenantId,
        userId: Long,
        email: String,
        success: Boolean,
        ipAddress: String? = null,
        userAgent: String? = null,
        errorMessage: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            userEmail(email)
            action(AuditAction.LOGIN)
            entityType("USER")
            entityId(userId.toString())
            description(if (success) "User logged in successfully" else "Login failed")
            success(success)
            errorMessage(errorMessage)
            ipAddress(ipAddress)
            userAgent(userAgent)
        }
    }

    /**
     * Log user logout.
     */
    fun logLogout(
        tenantId: TenantId,
        userId: Long,
        email: String,
        ipAddress: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            userEmail(email)
            action(AuditAction.LOGOUT)
            entityType("USER")
            entityId(userId.toString())
            description("User logged out")
            ipAddress(ipAddress)
        }
    }

    /**
     * Log data export operation.
     */
    fun logExport(
        tenantId: TenantId,
        userId: Long,
        entityType: String,
        format: String,
        recordCount: Int,
        ipAddress: String? = null
    ) {
        log {
            tenantId(tenantId)
            userId(userId)
            action(AuditAction.EXPORT)
            entityType(entityType)
            description("Exported $recordCount $entityType records to $format")
            metadata(mapOf("format" to format, "recordCount" to recordCount.toString()))
            ipAddress(ipAddress)
        }
    }

    /**
     * Flush buffered audit logs to database.
     */
    fun flushBuffer() {
        val batch = mutableListOf<AuditLog>()

        while (batch.size < BATCH_SIZE && buffer.isNotEmpty()) {
            buffer.poll()?.let { batch.add(it) }
        }

        if (batch.isNotEmpty()) {
            try {
                auditPort.saveAll(batch)
            } catch (e: Exception) {
                // Log to fallback (e.g., file or external service)
                System.err.println("Failed to save audit logs: ${e.message}")
                // Re-queue for retry
                batch.forEach { buffer.offer(it) }
            }
        }
    }

    /**
     * Query methods for audit log retrieval.
     */
    @Transactional(readOnly = true)
    fun getRecentLogs(tenantId: TenantId, limit: Int = 50): List<AuditLog> {
        return auditPort.findRecent(tenantId, limit)
    }

    @Transactional(readOnly = true)
    fun getEntityHistory(entityType: String, entityId: String): List<AuditLog> {
        return auditPort.findByEntity(entityType, entityId)
    }

    @Transactional(readOnly = true)
    fun getUserActivity(userId: Long): List<AuditLog> {
        return auditPort.findByUser(userId)
    }

    @Transactional(readOnly = true)
    fun search(criteria: AuditSearchCriteria): List<AuditLog> {
        return auditPort.search(criteria)
    }

    @Transactional(readOnly = true)
    fun getActivityReport(tenantId: TenantId, from: Instant, to: Instant): ActivityReport {
        val logs = auditPort.findByTimeRange(from, to)
            .filter { it.tenantId == tenantId }

        return ActivityReport(
            totalOperations = logs.size,
            creates = logs.count { it.action == AuditAction.CREATE },
            updates = logs.count { it.action == AuditAction.UPDATE },
            deletes = logs.count { it.action == AuditAction.DELETE },
            logins = logs.count { it.action == AuditAction.LOGIN },
            failedOperations = logs.count { !it.success },
            topUsers = logs.groupBy { it.userId }
                .map { (userId, userLogs) -> UserActivity(userId, userLogs.size) }
                .sortedByDescending { it.operationCount }
                .take(10),
            periodStart = from,
            periodEnd = to
        )
    }

    /**
     * Cleanup old audit logs based on retention policy.
     */
    fun cleanupOldLogs(retentionDays: Long): Int {
        val cutoff = Instant.now().minusSeconds(retentionDays * 24 * 60 * 60)
        return auditPort.deleteBefore(cutoff)
    }
}

/**
 * Activity report data class.
 */
data class ActivityReport(
    val totalOperations: Int,
    val creates: Int,
    val updates: Int,
    val deletes: Int,
    val logins: Int,
    val failedOperations: Int,
    val topUsers: List<UserActivity>,
    val periodStart: Instant,
    val periodEnd: Instant
) {
    val successRate: Double
        get() = if (totalOperations > 0) {
            ((totalOperations - failedOperations).toDouble() / totalOperations) * 100
        } else 100.0
}

data class UserActivity(
    val userId: Long?,
    val operationCount: Int
)
