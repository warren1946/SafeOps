/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.kernel.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Marker interface for entities that belong to a specific tenant.
 * All tenant-aware entities must implement this interface for proper
 * data isolation in a multi-tenant environment.
 */
interface TenantAware {
    val tenantId: TenantId
}

/**
 * Abstract base class for tenant-aware aggregate roots.
 * Provides common functionality for all tenant-scoped entities.
 */
abstract class TenantAggregateRoot(
    override val tenantId: TenantId
) : TenantAware {

    /**
     * Validates that the given tenantId matches this entity's tenant.
     * Used to ensure cross-tenant data access is prevented.
     */
    fun validateTenantAccess(requestingTenantId: TenantId) {
        require(tenantId == requestingTenantId) {
            "Cross-tenant access denied. Entity belongs to tenant $tenantId, " +
                    "but access was requested for tenant $requestingTenantId"
        }
    }
}
