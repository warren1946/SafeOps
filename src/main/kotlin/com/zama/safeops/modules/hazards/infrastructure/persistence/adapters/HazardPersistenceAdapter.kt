/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.adapters

import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.*
import com.zama.safeops.modules.hazards.infrastructure.persistence.entities.HazardJpaEntity
import com.zama.safeops.modules.hazards.infrastructure.persistence.repositories.HazardRepository
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
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

    override fun findActive(limit: Int): List<Hazard> = repo.findAll()
        .filter { it.status != HazardStatus.RESOLVED }
        .sortedByDescending { it.createdAt }
        .take(limit)
        .map { it.toDomain() }

    override fun findByLocation(type: SafetyLocationType, id: Long): List<Hazard> =
        repo.findByLocationTypeAndLocationId(type, id).map { it.toDomain() }
}

private fun Hazard.toEntity() = HazardJpaEntity(
    id = id?.value,
    title = title.value,
    description = description.value,
    severity = severity,
    priority = priority,
    status = status,
    assignedTo = assignedTo,
    createdBy = createdBy,
    locationType = locationType,
    locationId = locationId,
    dueDate = dueDate,
    resolvedAt = resolvedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun HazardJpaEntity.toDomain() = Hazard(
    id = id?.let { HazardId(it) },
    title = HazardTitle(title),
    description = HazardDescription(description),
    severity = severity,
    priority = priority,
    status = status,
    assignedTo = assignedTo,
    createdBy = createdBy,
    locationType = locationType,
    locationId = locationId,
    dueDate = dueDate,
    resolvedAt = resolvedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)