/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.adapters

import com.zama.safeops.modules.hazards.domain.model.Hazard
import com.zama.safeops.modules.hazards.domain.model.HazardDescription
import com.zama.safeops.modules.hazards.domain.model.HazardId
import com.zama.safeops.modules.hazards.domain.model.HazardTitle
import com.zama.safeops.modules.hazards.domain.ports.HazardPort
import com.zama.safeops.modules.hazards.infrastructure.persistence.entities.HazardJpaEntity
import com.zama.safeops.modules.hazards.infrastructure.persistence.repositories.HazardRepository
import org.springframework.stereotype.Component

@Component
class HazardPersistenceAdapter(
    private val repo: HazardRepository
) : HazardPort {

    override fun create(hazard: Hazard): Hazard =
        repo.save(hazard.toEntity()).toDomain()

    override fun findAll(): List<Hazard> =
        repo.findAll().map { it.toDomain() }

    override fun findById(id: Long): Hazard? =
        repo.findById(id).orElse(null)?.toDomain()

    override fun update(hazard: Hazard): Hazard =
        repo.save(hazard.toEntity()).toDomain()
}

private fun Hazard.toEntity() =
    HazardJpaEntity(
        id = this.id?.value,
        title = this.title.value,
        description = this.description.value,
        status = this.status,
        assignedTo = this.assignedTo,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

private fun HazardJpaEntity.toDomain() =
    Hazard(
        id = HazardId(this.id!!),
        title = HazardTitle(this.title),
        description = HazardDescription(this.description),
        status = this.status,
        assignedTo = this.assignedTo,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )