/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.sync.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant
import java.util.*

/**
 * Represents an entity that can be synchronized between offline and online states.
 * Used for inspections, hazards, photos, and other data created offline.
 */
data class SyncRecord(
    val localId: String = UUID.randomUUID().toString(),
    val serverId: Long? = null,
    val tenantId: TenantId,
    val userId: Long,
    val entityType: EntityType,
    val operation: SyncOperation,
    val status: SyncStatus = SyncStatus.PENDING,
    val localData: String, // JSON payload
    val checksum: String, // Data integrity check
    val retryCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now(),
    val syncedAt: Instant? = null
) {
    fun markAsSyncing(): SyncRecord = copy(
        status = SyncStatus.SYNCING,
        modifiedAt = Instant.now()
    )

    fun markAsSynced(serverId: Long): SyncRecord = copy(
        serverId = serverId,
        status = SyncStatus.SYNCED,
        syncedAt = Instant.now(),
        modifiedAt = Instant.now(),
        lastError = null
    )

    fun markAsFailed(error: String): SyncRecord = copy(
        status = SyncStatus.FAILED,
        lastError = error,
        retryCount = retryCount + 1,
        modifiedAt = Instant.now()
    )

    fun markAsConflict(): SyncRecord = copy(
        status = SyncStatus.CONFLICT,
        modifiedAt = Instant.now()
    )

    fun canRetry(maxRetries: Int = 5): Boolean =
        status in listOf(SyncStatus.FAILED, SyncStatus.PENDING) && retryCount < maxRetries

    fun isStale(staleThresholdMinutes: Long = 30): Boolean {
        val threshold = Instant.now().minusSeconds(staleThresholdMinutes * 60)
        return status == SyncStatus.SYNCING && modifiedAt.isBefore(threshold)
    }
}

enum class EntityType {
    INSPECTION,
    INSPECTION_ITEM,
    INSPECTION_PHOTO,
    HAZARD,
    HAZARD_PHOTO,
    SAFETY_EVENT,
    OFFICER_CHECKIN,
    OFFLINE_NOTE
}

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}

enum class SyncStatus {
    PENDING,      // Waiting to sync
    SYNCING,      // Currently uploading
    SYNCED,       // Successfully synced
    FAILED,       // Failed, will retry
    CONFLICT,     // Needs manual resolution
    CANCELLED     // User cancelled
}

/**
 * Batch of sync records for efficient bulk operations.
 */
data class SyncBatch(
    val batchId: String = UUID.randomUUID().toString(),
    val tenantId: TenantId,
    val userId: Long,
    val records: List<SyncRecord>,
    val priority: SyncPriority = SyncPriority.NORMAL,
    val createdAt: Instant = Instant.now()
)

enum class SyncPriority {
    LOW,      // Background sync
    NORMAL,   // Standard priority
    HIGH,     // User-initiated
    CRITICAL  // Emergency data
}

/**
 * Sync statistics for a user or device.
 */
data class SyncStatistics(
    val pendingCount: Int,
    val syncingCount: Int,
    val syncedCount: Int,
    val failedCount: Int,
    val conflictCount: Int,
    val lastSyncAt: Instant?,
    val estimatedBytesToSync: Long,
    val oldestPendingRecord: Instant?
) {
    fun hasPendingItems(): Boolean = pendingCount > 0 || syncingCount > 0
    fun hasFailures(): Boolean = failedCount > 0
    fun hasConflicts(): Boolean = conflictCount > 0
}

/**
 * Represents a conflict between local and server data.
 */
data class SyncConflict(
    val syncRecord: SyncRecord,
    val serverData: String?, // JSON from server
    val serverModifiedAt: Instant,
    val conflictType: ConflictType,
    val resolution: ConflictResolution? = null
)

enum class ConflictType {
    BOTH_MODIFIED,    // Local and server both changed
    SERVER_DELETED,   // Server deleted, local modified
    VERSION_MISMATCH  // Data version mismatch
}

data class ConflictResolution(
    val strategy: ResolutionStrategy,
    val resolvedAt: Instant = Instant.now(),
    val resolvedBy: Long
)

enum class ResolutionStrategy {
    USE_LOCAL,        // Keep local version
    USE_SERVER,       // Accept server version
    MERGE,            // Attempt automatic merge
    MANUAL            // User must decide
}
