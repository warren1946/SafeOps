/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.sync.application.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.zama.safeops.modules.sync.application.ports.ConflictResolutionPort
import com.zama.safeops.modules.sync.application.ports.EntitySyncHandler
import com.zama.safeops.modules.sync.application.ports.SyncPort
import com.zama.safeops.modules.sync.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing offline-online synchronization.
 * Handles queuing, conflict resolution, and batch uploads.
 */
@Service
class SyncService(
    private val syncPort: SyncPort,
    private val conflictPort: ConflictResolutionPort,
    private val objectMapper: ObjectMapper,
    private val handlers: List<EntitySyncHandler<*>>
) {
    private val activeSyncs = ConcurrentHashMap<String, Boolean>()
    private val handlerRegistry = handlers.associateBy { it.entityType }

    /**
     * Queue an entity for synchronization.
     */
    fun <T> queueEntity(
        tenantId: TenantId,
        userId: Long,
        entity: T,
        entityType: EntityType,
        operation: SyncOperation,
        serverId: Long? = null,
        priority: SyncPriority = SyncPriority.NORMAL
    ): SyncRecord {
        val handler = getHandler<T>(entityType)
        val jsonData = handler.serialize(entity)

        val record = SyncRecord(
            tenantId = tenantId,
            userId = userId,
            entityType = entityType,
            operation = operation,
            localData = jsonData,
            checksum = calculateChecksum(jsonData),
            serverId = serverId
        )

        return syncPort.save(record)
    }

    /**
     * Get sync statistics for a user.
     */
    fun getSyncStatus(tenantId: TenantId, userId: Long): SyncStatistics {
        return syncPort.getStatistics(tenantId, userId)
    }

    /**
     * Perform manual sync for a user.
     */
    @Transactional
    fun performSync(tenantId: TenantId, userId: Long): SyncResult {
        val syncKey = "$tenantId-$userId"

        // Prevent concurrent syncs
        if (activeSyncs.putIfAbsent(syncKey, true) != null) {
            return SyncResult.AlreadySyncing
        }

        return try {
            // Get pending records
            val pending = syncPort.findPending(tenantId, userId, limit = 100)

            if (pending.isEmpty()) {
                return SyncResult.NothingToSync
            }

            // Check for conflicts first
            val conflicts = conflictPort.detectConflicts(pending)
            if (conflicts.isNotEmpty()) {
                return SyncResult.HasConflicts(conflicts)
            }

            // Sync records
            val results = pending.map { record ->
                syncRecord(record)
            }

            val successCount = results.count { it.isSuccess }
            val failureCount = results.count { !it.isSuccess }

            SyncResult.Success(
                processedCount = pending.size,
                successCount = successCount,
                failureCount = failureCount
            )

        } finally {
            activeSyncs.remove(syncKey)
        }
    }

    /**
     * Scheduled background sync for all pending records.
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    @Async
    fun backgroundSync() {
        // This would iterate through all tenants/users with pending data
        // and trigger sync for each. In production, use a job queue.
    }

    /**
     * Resolve a sync conflict.
     */
    fun resolveConflict(
        syncId: String,
        strategy: ResolutionStrategy,
        userId: Long
    ): SyncRecord {
        return conflictPort.resolveConflict(syncId, strategy, userId)
    }

    /**
     * Get all conflicts for a user.
     */
    fun getConflicts(tenantId: TenantId, userId: Long): List<SyncConflict> {
        val conflictRecords = syncPort.findConflicts(tenantId, userId)
        return conflictPort.detectConflicts(conflictRecords)
    }

    /**
     * Cancel a pending sync record.
     */
    fun cancelSync(localId: String): Boolean {
        val record = syncPort.findByLocalId(localId) ?: return false
        if (record.status == SyncStatus.PENDING) {
            syncPort.save(record.copy(status = SyncStatus.CANCELLED))
            return true
        }
        return false
    }

    /**
     * Retry failed syncs.
     */
    fun retryFailed(tenantId: TenantId, userId: Long): SyncResult {
        val failed = syncPort.findFailed(tenantId, userId)

        // Reset status to pending
        val reset = failed.map { it.copy(status = SyncStatus.PENDING, retryCount = 0) }
        syncPort.saveBatch(reset)

        // Trigger sync
        return performSync(tenantId, userId)
    }

    /**
     * Cleanup old synced records.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    fun cleanupOldRecords() {
        val thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60)
        // This would need tenant iteration in production
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getHandler(entityType: EntityType): EntitySyncHandler<T> {
        return handlerRegistry[entityType] as? EntitySyncHandler<T>
            ?: throw IllegalStateException("No handler registered for $entityType")
    }

    private fun syncRecord(record: SyncRecord): SyncAttempt {
        val updated = syncPort.save(record.markAsSyncing())

        return try {
            @Suppress("UNCHECKED_CAST")
            val handler = getHandler<Any>(record.entityType)
            val entity = handler.deserialize(record.localData)

            when (record.operation) {
                SyncOperation.CREATE -> {
                    val serverId = handler.createOnServer(record.tenantId, entity)
                    syncPort.save(updated.markAsSynced(serverId))
                }

                SyncOperation.UPDATE -> {
                    record.serverId?.let { handler.updateOnServer(it, entity) }
                    syncPort.save(updated.markAsSynced(record.serverId!!))
                }

                SyncOperation.DELETE -> {
                    record.serverId?.let { handler.deleteOnServer(it) }
                    syncPort.save(updated.markAsSynced(record.serverId!!))
                }
            }

            SyncAttempt.Success(record.localId)
        } catch (e: Exception) {
            syncPort.save(updated.markAsFailed(e.message ?: "Unknown error"))
            SyncAttempt.Failure(record.localId, e.message ?: "Unknown error")
        }
    }

    private fun calculateChecksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}

/**
 * Result of a sync operation.
 */
sealed class SyncResult {
    data class Success(
        val processedCount: Int,
        val successCount: Int,
        val failureCount: Int
    ) : SyncResult()

    data class HasConflicts(val conflicts: List<SyncConflict>) : SyncResult()
    object NothingToSync : SyncResult()
    object AlreadySyncing : SyncResult()
}

/**
 * Result of a single sync attempt.
 */
sealed class SyncAttempt(val isSuccess: Boolean) {
    data class Success(val localId: String) : SyncAttempt(true)
    data class Failure(val localId: String, val error: String) : SyncAttempt(false)
}
