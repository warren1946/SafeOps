/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.mappers

import com.zama.safeops.modules.inspections.api.dto.InspectionItemResponse
import com.zama.safeops.modules.inspections.api.dto.InspectionResponse
import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.model.InspectionItem
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId

fun Inspection.toResponse(items: List<InspectionItem>) = InspectionResponse(
    id = id?.value ?: error("Inspection ID must not be null"),
    title = title,
    targetType = targetType,
    targetId = targetId,
    inspectorId = inspectorId,
    assignedReviewerId = assignedReviewerId,
    reviewerComments = reviewerComments,
    status = status,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
    items = items.map { it.toResponse() }
)

fun InspectionItem.toResponse() = InspectionItemResponse(
    id = id?.value ?: error("InspectionItem ID must not be null"),
    title = title,
    status = status,
    comment = comment,
    createdAt = createdAt.toString()
)

fun Long.toInspectionId() = InspectionId(this)