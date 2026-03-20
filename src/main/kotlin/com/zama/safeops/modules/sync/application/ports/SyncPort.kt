/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.sync.application.ports

import com.zama.safeops.modules.sync.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Port for sync record persistence.
 */
interface SyncPort {
    fun save(record: SyncRecord): SyncRecord
    fun saveBatch(records: List<SyncRecord>): List<SyncRecord>
    fun findByLocalId(localId: String): SyncRecord?
    fun findByServerId(entityType: EntityType, serverId: Long): SyncRecord?
    fun findPending(tenantId: TenantId, userId: Long, limit: Int = 100): List<SyncRecord>
    fun findFailed(tenantId: TenantId, userId: Long, maxRetries: Int = 5): List<SyncRecord>
    fun findConflicts(tenantId: TenantId, userId: Long): List<SyncRecord>
    fun findByUser(tenantId: TenantId, userId: Long, status: SyncStatus? = null): List<SyncRecord>
    fun getStatistics(tenantId: TenantId, userId: Long): SyncStatistics
    fun delete(localId: String)
    fun deleteSyncedBefore(tenantId: TenantId, before: Instant): Int
}

/**
 * Port for conflict detection and resolution.
 */
interface ConflictResolutionPort {
    fun detectConflicts(records: List<SyncRecord>): List<SyncConflict>
    fun resolveConflict(syncId: String, strategy: ResolutionStrategy, userId: Long): SyncRecord
    fun autoResolve(conflicts: List<SyncConflict>): List<SyncConflict>
}

/**
 * Port for entity-specific sync handlers.
 */
interface EntitySyncHandler<T> {
    val entityType: EntityType
    fun createOnServer(tenantId: TenantId, data: T): Long
    fun updateOnServer(serverId: Long, data: T)
    fun deleteOnServer(serverId: Long)
    fun serialize(entity: T): String
    fun deserialize(json: String): T
}
