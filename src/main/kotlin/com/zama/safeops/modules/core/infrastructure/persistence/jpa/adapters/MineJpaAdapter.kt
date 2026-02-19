/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.core.application.ports.MinePort
import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities.MineJpaEntity
import com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository.SpringDataMineRepository
import org.springframework.stereotype.Component

@Component
class MineJpaAdapter(
    private val repo: SpringDataMineRepository
) : MinePort {

    override fun save(mine: Mine): Mine =
        repo.save(MineJpaEntity.fromDomain(mine)).toDomain()

    override fun findAll(): List<Mine> =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: MineId): Mine? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun exists(id: MineId): Boolean =
        repo.existsById(id.value)
}