/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.application.ports

import com.zama.safeops.modules.tenant.domain.model.Tenant
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantSlug

/**
 * Port for tenant persistence operations.
 */
interface TenantPort {

    fun save(tenant: Tenant): Tenant

    fun findById(id: TenantId): Tenant?

    fun findBySlug(slug: TenantSlug): Tenant?

    fun existsBySlug(slug: TenantSlug): Boolean

    fun findAll(): List<Tenant>

    fun findActive(): List<Tenant>

    fun delete(id: TenantId)
}

/**
 * Port for tenant configuration operations.
 */
interface TenantConfigurationPort {

    fun updateConfiguration(tenantId: TenantId, updates: Map<String, Any>): Tenant

    fun updateBranding(tenantId: TenantId, brandingUpdates: Map<String, Any>): Tenant

    fun getConfiguration(tenantId: TenantId): Map<String, Any>
}
