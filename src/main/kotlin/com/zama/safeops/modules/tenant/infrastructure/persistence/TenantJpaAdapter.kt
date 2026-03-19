/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.infrastructure.persistence

import com.zama.safeops.modules.tenant.application.ports.TenantPort
import com.zama.safeops.modules.tenant.domain.model.Tenant
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantSlug
import org.springframework.stereotype.Component

@Component
class TenantJpaAdapter(
    private val repository: TenantJpaRepository
) : TenantPort {

    override fun save(tenant: Tenant): Tenant {
        val entity = TenantJpaEntity.fromDomain(tenant)
        return repository.save(entity).toDomain()
    }

    override fun findById(id: TenantId): Tenant? {
        return repository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findBySlug(slug: TenantSlug): Tenant? {
        return repository.findBySlug(slug.value)?.toDomain()
    }

    override fun existsBySlug(slug: TenantSlug): Boolean {
        return repository.existsBySlug(slug.value)
    }

    override fun findAll(): List<Tenant> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun findActive(): List<Tenant> {
        return repository.findAllByStatus("ACTIVE").map { it.toDomain() }
    }

    override fun delete(id: TenantId) {
        repository.deleteById(id.value)
    }
}
