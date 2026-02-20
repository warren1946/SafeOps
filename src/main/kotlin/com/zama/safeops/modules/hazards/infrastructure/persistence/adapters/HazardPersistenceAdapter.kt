/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.adapters

import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.Hazard
import com.zama.safeops.modules.hazards.domain.model.HazardDescription
import com.zama.safeops.modules.hazards.domain.model.HazardId
import com.zama.safeops.modules.hazards.domain.model.HazardTitle
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

private fun Hazard.toEntity() = HazardJpaEntity(
    id = id?.value,
    title = title.value,
    description = description.value,
    status = status,
    assignedTo = assignedTo,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun HazardJpaEntity.toDomain() = Hazard(
    id = id?.let { HazardId(it) },
    title = HazardTitle(title),
    description = HazardDescription(description),
    status = status,
    assignedTo = assignedTo,
    createdAt = createdAt,
    updatedAt = updatedAt
)