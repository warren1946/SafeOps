/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.application.ports

import com.zama.safeops.modules.audit.domain.model.AuditLog
import com.zama.safeops.modules.audit.domain.model.AuditAction
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Port for audit log persistence.
 */
interface AuditPort {
    fun save(log: AuditLog): AuditLog
    fun findById(id: Long): AuditLog?
    fun findByTenant(tenantId: TenantId, page: Int, size: Int): List<AuditLog>
    fun findByUser(userId: Long, page: Int, size: Int): List<AuditLog>
    fun findByAction(tenantId: TenantId, action: AuditAction, page: Int, size: Int): List<AuditLog>
    fun findByEntity(tenantId: TenantId, entityType: String, entityId: String): List<AuditLog>
    fun findByTimeRange(tenantId: TenantId, from: Instant, to: Instant): List<AuditLog>
    fun search(tenantId: TenantId, query: String, page: Int, size: Int): List<AuditLog>
}
