/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.shared.kernel.domain.valueobjects

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Thread-local storage for the current tenant context.
 * This enables tenant isolation throughout the request lifecycle.
 */
object TenantContext {
    private val currentTenant: ThreadLocal<TenantId?> = ThreadLocal()
    private val currentTenantSlug: ThreadLocal<String?> = ThreadLocal()

    fun setCurrentTenant(tenantId: TenantId, slug: String) {
        currentTenant.set(tenantId)
        currentTenantSlug.set(slug)
    }

    fun getCurrentTenant(): TenantId? = currentTenant.get()

    fun getCurrentTenantSlug(): String? = currentTenantSlug.get()

    fun clear() {
        currentTenant.remove()
        currentTenantSlug.remove()
    }

    fun requireCurrentTenant(): TenantId {
        return currentTenant.get()
            ?: throw IllegalStateException("No tenant context available. Ensure TenantContextFilter is configured.")
    }
}

/**
 * Executes the given block with the specified tenant context.
 * Automatically clears the context after execution.
 */
inline fun <T> withTenant(tenantId: TenantId, slug: String, block: () -> T): T {
    TenantContext.setCurrentTenant(tenantId, slug)
    return try {
        block()
    } finally {
        TenantContext.clear()
    }
}
