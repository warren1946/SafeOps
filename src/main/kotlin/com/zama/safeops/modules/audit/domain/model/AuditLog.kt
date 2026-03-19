/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Audit log entry for tracking all significant system events.
 * Immutable record of who did what, when, and from where.
 */
data class AuditLog(
    val id: AuditLogId? = null,
    val tenantId: TenantId,
    val userId: Long?,
    val action: AuditAction,
    val entityType: String,
    val entityId: String?,
    val oldValue: String?,
    val newValue: String?,
    val metadata: Map<String, String>,
    val ipAddress: String?,
    val userAgent: String?,
    val timestamp: Instant = Instant.now()
)

@JvmInline
value class AuditLogId(val value: Long)

/**
 * Types of auditable actions.
 */
enum class AuditAction {
    // CRUD operations
    CREATE,
    READ,
    UPDATE,
    DELETE,

    // Auth operations
    LOGIN,
    LOGOUT,
    LOGIN_FAILED,
    PASSWORD_CHANGED,
    TOKEN_REFRESHED,

    // Business operations
    INSPECTION_STARTED,
    INSPECTION_COMPLETED,
    INSPECTION_SUBMITTED,
    INSPECTION_REVIEWED,
    HAZARD_REPORTED,
    HAZARD_RESOLVED,
    SAFETY_EVENT_CREATED,
    ALERT_SENT,

    // System operations
    EXPORT_GENERATED,
    REPORT_CREATED,
    CONFIGURATION_CHANGED,
    USER_INVITED,
    USER_ROLE_CHANGED
}

/**
 * Annotation to mark methods for automatic auditing.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Auditable(
    val action: AuditAction,
    val entityType: String,
    val captureParams: Array<String> = [],
    val captureResult: Boolean = false
)
