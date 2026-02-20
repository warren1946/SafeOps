/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionJpaEntity
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.repositories.SpringDataInspectionRepository
import org.springframework.stereotype.Component

@Component
class InspectionJpaAdapter(
    private val repo: SpringDataInspectionRepository
) : InspectionPort {

    override fun create(inspection: Inspection): Inspection =
        repo.save(inspection.toEntity()).toDomain()

    override fun update(inspection: Inspection): Inspection =
        repo.save(inspection.toEntity()).toDomain()

    override fun findById(id: InspectionId): Inspection? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun findAll(): List<Inspection> =
        repo.findAll().map { it.toDomain() }
}

private fun Inspection.toEntity() = InspectionJpaEntity(
    id = id?.value,
    title = title,
    targetType = targetType,
    targetId = targetId,
    inspectorId = inspectorId,
    assignedReviewerId = assignedReviewerId,
    reviewerComments = reviewerComments,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun InspectionJpaEntity.toDomain() = Inspection(
    id = InspectionId(id!!),
    title = title,
    targetType = targetType,
    targetId = targetId,
    inspectorId = inspectorId,
    assignedReviewerId = assignedReviewerId,
    reviewerComments = reviewerComments,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)