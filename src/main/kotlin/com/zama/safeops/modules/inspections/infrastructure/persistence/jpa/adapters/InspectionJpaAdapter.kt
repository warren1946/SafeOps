/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.model.InspectionFilterCriteria
import com.zama.safeops.modules.inspections.domain.model.InspectionSortField
import com.zama.safeops.modules.inspections.domain.model.SortDirection
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionJpaEntity
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.repositories.SpringDataInspectionRepository
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.specs.InspectionSpecifications
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.ZoneOffset

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

    override fun findRecent(limit: Int): List<Inspection> =
        repo.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
            .map { it.toDomain() }

    override fun filter(criteria: InspectionFilterCriteria): List<Inspection> {
        val spec = Specification.where(
            InspectionSpecifications.statusEquals(criteria.status)
        )
            .and(
                InspectionSpecifications.dateBetween(
                    criteria.fromDate?.atStartOfDay()?.toInstant(ZoneOffset.UTC),
                    criteria.toDate?.plusDays(1)?.atStartOfDay()?.toInstant(ZoneOffset.UTC)
                )
            )
            .and(InspectionSpecifications.officerEquals(criteria.officerId))
            .and(InspectionSpecifications.locationEquals(criteria.locationType, criteria.locationId))
            .and(InspectionSpecifications.searchLike(criteria.search))

        val direction = if (criteria.direction == SortDirection.ASC)
            Sort.Direction.ASC else Sort.Direction.DESC

        val sort = when (criteria.sortBy) {
            InspectionSortField.DATE ->
                Sort.by(direction, "createdAt")

            InspectionSortField.STATUS ->
                Sort.by(direction, "status")

            InspectionSortField.OFFICER ->
                Sort.by(direction, "inspectorId")

            InspectionSortField.SCORE ->
                Sort.unsorted() // domain-only
        }

        return repo.findAll(spec, sort).map { it.toDomain() }
    }
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