/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.core.application.ports.AreaPort
import com.zama.safeops.modules.core.domain.model.Area
import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities.AreaJpaEntity
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataAreaRepository
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataShaftRepository
import org.springframework.stereotype.Component

@Component
class AreaJpaAdapter(
    private val repo: SpringDataAreaRepository,
    private val shaftRepo: SpringDataShaftRepository
) : AreaPort {

    override fun save(area: Area): Area =
        repo.save(AreaJpaEntity.fromDomain(area)).toDomain()

    override fun findAll(): List<Area> =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: AreaId): Area? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun exists(id: AreaId): Boolean =
        repo.existsById(id.value)

    override fun existsShaft(shaftId: ShaftId): Boolean =
        shaftRepo.existsById(shaftId.value)
}