/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.application.ports

import com.zama.safeops.modules.audit.domain.model.AuditLog
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Port for audit log persistence operations.
 */
interface AuditPort {

    /**
     * Save an audit log entry.
     */
    fun save(auditLog: AuditLog): AuditLog

    /**
     * Save multiple audit log entries (batch insert).
     */
    fun saveAll(auditLogs: List<AuditLog>): List<AuditLog>

    /**
     * Find audit logs for a specific tenant.
     */
    fun findByTenant(tenantId: TenantId, limit: Int = 100): List<AuditLog>

    /**
     * Find audit logs for a specific entity.
     */
    fun findByEntity(entityType: String, entityId: String, limit: Int = 100): List<AuditLog>

    /**
     * Find audit logs by user.
     */
    fun findByUser(userId: Long, limit: Int = 100): List<AuditLog>

    /**
     * Find audit logs within a time range.
     */
    fun findByTimeRange(from: Instant, to: Instant, limit: Int = 1000): List<AuditLog>

    /**
     * Find audit logs by action type.
     */
    fun findByAction(action: AuditAction, limit: Int = 100): List<AuditLog>

    /**
     * Search audit logs with criteria.
     */
    fun search(criteria: AuditSearchCriteria): List<AuditLog>

    /**
     * Get recent audit logs for a tenant.
     */
    fun findRecent(tenantId: TenantId, limit: Int = 50): List<AuditLog>

    /**
     * Delete old audit logs (for data retention policies).
     */
    fun deleteBefore(cutoffDate: Instant): Int
}

/**
 * Audit action types for categorizing operations.
 */
enum class AuditAction {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    LOGIN,
    LOGOUT,
    EXPORT,
    IMPORT,
    APPROVE,
    REJECT,
    SUBMIT,
    ASSIGN,
    CONFIGURE,
    EMERGENCY_ACTIVATE,
    EMERGENCY_RESOLVE
}

/**
 * Search criteria for audit log queries.
 */
data class AuditSearchCriteria(
    val tenantId: TenantId? = null,
    val userId: Long? = null,
    val entityType: String? = null,
    val entityId: String? = null,
    val action: AuditAction? = null,
    val fromDate: Instant? = null,
    val toDate: Instant? = null,
    val searchText: String? = null,
    val limit: Int = 100
)
