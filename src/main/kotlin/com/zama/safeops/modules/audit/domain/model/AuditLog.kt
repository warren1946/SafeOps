/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.domain.model

import com.zama.safeops.modules.audit.application.ports.AuditAction
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant
import java.util.*

/**
 * Value object for Audit Log ID.
 */
@JvmInline
value class AuditLogId(val value: UUID) {
    companion object {
        fun generate(): AuditLogId = AuditLogId(UUID.randomUUID())
    }
}

/**
 * Domain entity representing an audit log entry.
 * Immutable by design - audit logs should never be modified.
 */
data class AuditLog(
    val id: AuditLogId = AuditLogId.generate(),
    val tenantId: TenantId,
    val userId: Long?,
    val userEmail: String?,
    val action: AuditAction,
    val entityType: String,
    val entityId: String?,
    val description: String,
    val oldValues: Map<String, Any?>? = null,
    val newValues: Map<String, Any?>? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val sessionId: String? = null,
    val success: Boolean = true,
    val errorMessage: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Instant = Instant.now()
) {

    /**
     * Check if this audit log represents a data modification.
     */
    fun isModification(): Boolean = action in setOf(
        AuditAction.CREATE,
        AuditAction.UPDATE,
        AuditAction.DELETE
    )

    /**
     * Get changes as a formatted string for display.
     */
    fun getChangesDescription(): String {
        if (oldValues == null && newValues == null) return "No data changes"

        val changes = mutableListOf<String>()
        val allKeys = (oldValues?.keys ?: emptySet()) + (newValues?.keys ?: emptySet())

        allKeys.forEach { key ->
            val old = oldValues?.get(key)
            val new = newValues?.get(key)
            if (old != new) {
                changes.add("$key: '$old' → '$new'")
            }
        }

        return changes.joinToString(", ")
    }
}

/**
 * Builder for creating audit log entries.
 */
class AuditLogBuilder {
    private var tenantId: TenantId? = null
    private var userId: Long? = null
    private var userEmail: String? = null
    private var action: AuditAction? = null
    private var entityType: String = ""
    private var entityId: String? = null
    private var description: String = ""
    private var oldValues: Map<String, Any?>? = null
    private var newValues: Map<String, Any?>? = null
    private var ipAddress: String? = null
    private var userAgent: String? = null
    private var sessionId: String? = null
    private var success: Boolean = true
    private var errorMessage: String? = null
    private var metadata: Map<String, String> = emptyMap()

    fun tenantId(tenantId: TenantId) = apply { this.tenantId = tenantId }
    fun userId(userId: Long?) = apply { this.userId = userId }
    fun userEmail(userEmail: String?) = apply { this.userEmail = userEmail }
    fun action(action: AuditAction) = apply { this.action = action }
    fun entityType(entityType: String) = apply { this.entityType = entityType }
    fun entityId(entityId: String?) = apply { this.entityId = entityId }
    fun description(description: String) = apply { this.description = description }
    fun oldValues(oldValues: Map<String, Any?>?) = apply { this.oldValues = oldValues }
    fun newValues(newValues: Map<String, Any?>?) = apply { this.newValues = newValues }
    fun ipAddress(ipAddress: String?) = apply { this.ipAddress = ipAddress }
    fun userAgent(userAgent: String?) = apply { this.userAgent = userAgent }
    fun sessionId(sessionId: String?) = apply { this.sessionId = sessionId }
    fun success(success: Boolean) = apply { this.success = success }
    fun errorMessage(errorMessage: String?) = apply { this.errorMessage = errorMessage }
    fun metadata(metadata: Map<String, String>) = apply { this.metadata = metadata }

    fun build(): AuditLog {
        require(tenantId != null) { "Tenant ID is required" }
        require(action != null) { "Action is required" }

        return AuditLog(
            tenantId = tenantId!!,
            userId = userId,
            userEmail = userEmail,
            action = action!!,
            entityType = entityType,
            entityId = entityId,
            description = description,
            oldValues = oldValues,
            newValues = newValues,
            ipAddress = ipAddress,
            userAgent = userAgent,
            sessionId = sessionId,
            success = success,
            errorMessage = errorMessage,
            metadata = metadata
        )
    }
}

/**
 * Audit log entry for entity lifecycle events.
 */
data class EntityAuditLog(
    val auditLog: AuditLog,
    val entityType: String,
    val entityId: String,
    val operation: EntityOperation
) {
    enum class EntityOperation {
        CREATED,
        MODIFIED,
        DELETED,
        VIEWED
    }
}
