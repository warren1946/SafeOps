/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.shared.kernel.domain.events

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant
import java.util.UUID

/**
 * Base interface for all domain events in the system.
 * Domain events represent significant occurrences within the domain
 * that other parts of the system may need to react to.
 */
interface DomainEvent {
    val eventId: UUID
    val occurredAt: Instant
    val tenantId: TenantId?
    val eventType: String
    val aggregateId: String
    val version: Int
}

/**
 * Abstract base class for domain events providing common functionality.
 */
abstract class BaseDomainEvent(
    override val tenantId: TenantId?,
    override val aggregateId: String,
    override val version: Int = 1
) : DomainEvent {
    override val eventId: UUID = UUID.randomUUID()
    override val occurredAt: Instant = Instant.now()
    override val eventType: String = this::class.simpleName ?: "UnknownEvent"
}

// ==================== Core Domain Events ====================

/**
 * Event fired when a new tenant is provisioned.
 */
data class TenantProvisionedEvent(
    val tenantSlug: String,
    val tenantName: String,
    val adminEmail: String,
    val subscriptionPlan: String
) : BaseDomainEvent(
    tenantId = null, // System-level event
    aggregateId = tenantSlug,
    version = 1
)

/**
 * Event fired when tenant configuration is updated.
 */
data class TenantConfigurationUpdatedEvent(
    override val tenantId: TenantId,
    val changedSettings: Map<String, Pair<String?, String?>> // setting -> (oldValue, newValue)
) : BaseDomainEvent(
    tenantId = tenantId,
    aggregateId = tenantId.value.toString(),
    version = 1
)

/**
 * Event fired when a user logs in successfully.
 */
data class UserLoggedInEvent(
    override val tenantId: TenantId,
    val userId: Long,
    val email: String,
    val ipAddress: String?,
    val userAgent: String?
) : BaseDomainEvent(
    tenantId = tenantId,
    aggregateId = userId.toString(),
    version = 1
)

/**
 * Event fired when a user's role changes.
 */
data class UserRoleChangedEvent(
    override val tenantId: TenantId,
    val userId: Long,
    val oldRoles: Set<String>,
    val newRoles: Set<String>,
    val changedBy: Long
) : BaseDomainEvent(
    tenantId = tenantId,
    aggregateId = userId.toString(),
    version = 1
)
