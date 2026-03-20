/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.audit.application.ports.AuditPort
import com.zama.safeops.modules.audit.application.ports.AuditSearchCriteria
import com.zama.safeops.modules.audit.domain.model.AuditLog
import com.zama.safeops.modules.audit.domain.model.AuditLogId
import com.zama.safeops.modules.audit.infrastructure.persistence.jpa.entities.AuditLogJpaEntity
import com.zama.safeops.modules.audit.infrastructure.persistence.jpa.repositories.AuditLogJpaRepository
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AuditLogJpaAdapter(
    private val repository: AuditLogJpaRepository
) : AuditPort {

    override fun save(auditLog: AuditLog): AuditLog {
        return repository.save(auditLog.toEntity()).toDomain()
    }

    override fun saveAll(auditLogs: List<AuditLog>): List<AuditLog> {
        return repository.saveAll(auditLogs.map { it.toEntity() }).map { it.toDomain() }
    }

    override fun findByTenant(tenantId: TenantId, limit: Int): List<AuditLog> {
        return repository.findByTenantIdOrderByTimestampDesc(
            tenantId.value,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun findByEntity(entityType: String, entityId: String, limit: Int): List<AuditLog> {
        return repository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
            entityType,
            entityId,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun findByUser(userId: Long, limit: Int): List<AuditLog> {
        return repository.findByUserIdOrderByTimestampDesc(
            userId,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun findByTimeRange(from: Instant, to: Instant, limit: Int): List<AuditLog> {
        return repository.findByTimeRange(
            from,
            to,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun findByAction(action: com.zama.safeops.modules.audit.application.ports.AuditAction, limit: Int): List<AuditLog> {
        return repository.findByActionOrderByTimestampDesc(
            action,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun search(criteria: AuditSearchCriteria): List<AuditLog> {
        return repository.search(
            tenantId = criteria.tenantId?.value,
            userId = criteria.userId,
            entityType = criteria.entityType,
            entityId = criteria.entityId,
            action = criteria.action,
            fromDate = criteria.fromDate,
            toDate = criteria.toDate,
            searchText = criteria.searchText,
            pageable = PageRequest.of(0, criteria.limit)
        ).map { it.toDomain() }
    }

    override fun findRecent(tenantId: TenantId, limit: Int): List<AuditLog> {
        return repository.findRecentByTenant(
            tenantId.value,
            PageRequest.of(0, limit)
        ).map { it.toDomain() }
    }

    override fun deleteBefore(cutoffDate: Instant): Int {
        return repository.deleteBefore(cutoffDate)
    }

    private fun AuditLog.toEntity(): AuditLogJpaEntity {
        return AuditLogJpaEntity(
            id = this.id.value,
            tenantId = this.tenantId.value,
            userId = this.userId,
            userEmail = this.userEmail,
            action = this.action,
            entityType = this.entityType,
            entityId = this.entityId,
            description = this.description,
            oldValues = this.oldValues,
            newValues = this.newValues,
            ipAddress = this.ipAddress,
            userAgent = this.userAgent,
            sessionId = this.sessionId,
            success = this.success,
            errorMessage = this.errorMessage,
            metadata = this.metadata,
            timestamp = this.timestamp
        )
    }

    private fun AuditLogJpaEntity.toDomain(): AuditLog {
        return AuditLog(
            id = AuditLogId(this.id),
            tenantId = TenantId(this.tenantId),
            userId = this.userId,
            userEmail = this.userEmail,
            action = this.action,
            entityType = this.entityType,
            entityId = this.entityId,
            description = this.description,
            oldValues = this.oldValues,
            newValues = this.newValues,
            ipAddress = this.ipAddress,
            userAgent = this.userAgent,
            sessionId = this.sessionId,
            success = this.success,
            errorMessage = this.errorMessage,
            metadata = this.metadata,
            timestamp = this.timestamp
        )
    }
}
