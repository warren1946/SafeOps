/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.inspections.application.ports.InspectionItemPort
import com.zama.safeops.modules.inspections.domain.model.InspectionItem
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionItemId
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionItemJpaEntity
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.repositories.SpringDataInspectionItemRepository
import org.springframework.stereotype.Component

@Component
class InspectionItemJpaAdapter(
    private val repo: SpringDataInspectionItemRepository
) : InspectionItemPort {

    override fun create(item: InspectionItem): InspectionItem =
        repo.save(item.toEntity()).toDomain()

    override fun findByInspectionId(inspectionId: InspectionId): List<InspectionItem> =
        repo.findByInspectionId(inspectionId.value).map { it.toDomain() }

    override fun findById(id: InspectionItemId): InspectionItem? =
        repo.findById(id.value).orElse(null)?.toDomain()
}

private fun InspectionItem.toEntity() = InspectionItemJpaEntity(
    id = id?.value,
    inspectionId = inspectionId.value,
    title = title,
    status = status,
    comment = comment,
    createdAt = createdAt
)

private fun InspectionItemJpaEntity.toDomain() = InspectionItem(
    id = InspectionItemId(id!!),
    inspectionId = InspectionId(inspectionId),
    title = title,
    status = status,
    comment = comment,
    createdAt = createdAt
)