/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.sync.infrastructure.adapters

import com.zama.safeops.modules.sync.application.ports.ConflictResolutionPort
import com.zama.safeops.modules.sync.application.ports.SyncPort
import com.zama.safeops.modules.sync.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Stub implementation of SyncPort and ConflictResolutionPort.
 */
@Component
class StubSyncAdapter : SyncPort, ConflictResolutionPort {

    override fun save(record: SyncRecord): SyncRecord = record

    override fun saveBatch(records: List<SyncRecord>): List<SyncRecord> = records

    override fun findByLocalId(localId: String): SyncRecord? = null

    override fun findByServerId(entityType: EntityType, serverId: Long): SyncRecord? = null

    override fun findPending(tenantId: TenantId, userId: Long, limit: Int): List<SyncRecord> = emptyList()

    override fun findFailed(tenantId: TenantId, userId: Long, maxRetries: Int): List<SyncRecord> = emptyList()

    override fun findConflicts(tenantId: TenantId, userId: Long): List<SyncRecord> = emptyList()

    override fun findByUser(tenantId: TenantId, userId: Long, status: SyncStatus?): List<SyncRecord> = emptyList()

    override fun getStatistics(tenantId: TenantId, userId: Long): SyncStatistics =
        SyncStatistics(0, 0, 0, 0, 0, null, 0, null)

    override fun delete(localId: String) {}

    override fun deleteSyncedBefore(tenantId: TenantId, before: Instant): Int = 0

    override fun detectConflicts(records: List<SyncRecord>): List<SyncConflict> = emptyList()

    override fun resolveConflict(syncId: String, strategy: ResolutionStrategy, userId: Long): SyncRecord {
        throw NotImplementedError("Stub implementation")
    }

    override fun autoResolve(conflicts: List<SyncConflict>): List<SyncConflict> = emptyList()
}
