/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.core.application.ports.SitePort
import com.zama.safeops.modules.core.domain.model.Site
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities.SiteJpaEntity
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataMineRepository
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataSiteRepository
import org.springframework.stereotype.Component

@Component
class SiteJpaAdapter(
    private val repo: SpringDataSiteRepository,
    private val mineRepo: SpringDataMineRepository
) : SitePort {

    override fun save(site: Site): Site =
        repo.save(SiteJpaEntity.fromDomain(site)).toDomain()

    override fun findAll(): List<Site> =
        repo.findAll().map { it.toDomain() }

    override fun exists(id: SiteId): Boolean =
        repo.existsById(id.value)

    override fun existsMine(mineId: MineId): Boolean =
        mineRepo.existsById(mineId.value)
}