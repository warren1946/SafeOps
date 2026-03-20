/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.audit.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.audit.application.ports.AuditAction
import com.zama.safeops.modules.audit.infrastructure.persistence.jpa.entities.AuditLogJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface AuditLogJpaRepository : JpaRepository<AuditLogJpaEntity, UUID> {

    /**
     * Find audit logs by tenant ID, ordered by timestamp descending.
     */
    fun findByTenantIdOrderByTimestampDesc(tenantId: Long, pageable: Pageable): List<AuditLogJpaEntity>

    /**
     * Find audit logs by entity type and ID.
     */
    fun findByEntityTypeAndEntityIdOrderByTimestampDesc(
        entityType: String,
        entityId: String,
        pageable: Pageable
    ): List<AuditLogJpaEntity>

    /**
     * Find audit logs by user ID.
     */
    fun findByUserIdOrderByTimestampDesc(userId: Long, pageable: Pageable): List<AuditLogJpaEntity>

    /**
     * Find audit logs within a time range.
     */
    @Query(
        """
        SELECT a FROM AuditLogJpaEntity a 
        WHERE a.timestamp >= :from AND a.timestamp <= :to 
        ORDER BY a.timestamp DESC
    """
    )
    fun findByTimeRange(
        @Param("from") from: Instant,
        @Param("to") to: Instant,
        pageable: Pageable
    ): List<AuditLogJpaEntity>

    /**
     * Find audit logs by action type.
     */
    fun findByActionOrderByTimestampDesc(action: AuditAction, pageable: Pageable): List<AuditLogJpaEntity>

    /**
     * Find recent audit logs for a tenant.
     */
    @Query(
        """
        SELECT a FROM AuditLogJpaEntity a 
        WHERE a.tenantId = :tenantId 
        ORDER BY a.timestamp DESC
    """
    )
    fun findRecentByTenant(
        @Param("tenantId") tenantId: Long,
        pageable: Pageable
    ): List<AuditLogJpaEntity>

    /**
     * Search audit logs with multiple criteria.
     */
    @Query(
        """
        SELECT a FROM AuditLogJpaEntity a 
        WHERE (:#{#tenantId} IS NULL OR a.tenantId = :tenantId)
        AND (:#{#userId} IS NULL OR a.userId = :userId)
        AND (:#{#entityType} IS NULL OR a.entityType = :entityType)
        AND (:#{#entityId} IS NULL OR a.entityId = :entityId)
        AND (:#{#action} IS NULL OR a.action = :action)
        AND (:#{#fromDate} IS NULL OR a.timestamp >= :fromDate)
        AND (:#{#toDate} IS NULL OR a.timestamp <= :toDate)
        AND (:#{#searchText} IS NULL OR 
            LOWER(a.description) LIKE LOWER(CONCAT('%', :searchText, '%'))
            OR LOWER(a.userEmail) LIKE LOWER(CONCAT('%', :searchText, '%')))
        ORDER BY a.timestamp DESC
    """
    )
    fun search(
        @Param("tenantId") tenantId: Long?,
        @Param("userId") userId: Long?,
        @Param("entityType") entityType: String?,
        @Param("entityId") entityId: String?,
        @Param("action") action: AuditAction?,
        @Param("fromDate") fromDate: Instant?,
        @Param("toDate") toDate: Instant?,
        @Param("searchText") searchText: String?,
        pageable: Pageable
    ): List<AuditLogJpaEntity>

    /**
     * Delete old audit logs.
     */
    @Modifying
    @Query("DELETE FROM AuditLogJpaEntity a WHERE a.timestamp < :cutoff")
    fun deleteBefore(@Param("cutoff") cutoff: Instant): Int

    /**
     * Count audit logs by tenant.
     */
    fun countByTenantId(tenantId: Long): Long

    /**
     * Count audit logs by action.
     */
    fun countByAction(action: AuditAction): Long
}
