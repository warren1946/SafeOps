/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.core.application.ports.ShaftPort
import com.zama.safeops.modules.core.domain.model.Shaft
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities.ShaftJpaEntity
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataShaftRepository
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataSiteRepository
import org.springframework.stereotype.Component

@Component
class ShaftJpaAdapter(
    private val repo: SpringDataShaftRepository,
    private val siteRepo: SpringDataSiteRepository
) : ShaftPort {

    override fun save(shaft: Shaft): Shaft =
        repo.save(ShaftJpaEntity.fromDomain(shaft)).toDomain()

    override fun findAll(): List<Shaft> =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: ShaftId): Shaft? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun exists(id: ShaftId): Boolean =
        repo.existsById(id.value)

    override fun existsSite(siteId: SiteId): Boolean =
        siteRepo.existsById(siteId.value)
}