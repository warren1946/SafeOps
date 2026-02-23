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
import com.zama.safeops.modules.inspections.domain.model.InspectionScore
import com.zama.safeops.modules.inspections.domain.model.InspectionSummaryResponse
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

fun Inspection.calculateScore(): InspectionScore {
    /*val items = this.items
    if (items.isEmpty()) {
        return InspectionScore(score = 0, maxScore = 0, percentage = 0.0)
    }

    val maxScore = items.size
    val score = items.count { it.isCompliant }
    val percentage = (score.toDouble() / maxScore.toDouble()) * 100.0

    return InspectionScore(
        score = score,
        maxScore = maxScore,
        percentage = percentage
    )*/
    return InspectionScore(
        score = 0,
        maxScore = 0,
        percentage = 0.0
    )
}

fun Inspection.toSummaryResponse(): InspectionSummaryResponse {
    return InspectionSummaryResponse(
        id = this.id!!.value,
        title = this.title,
        status = this.status,
        targetType = this.targetType,
        targetId = this.targetId,
        officerId = this.inspectorId,
        performedAt = this.createdAt, // or updatedAt if you prefer
        score = 0,        // placeholder until InspectionItems exist
        maxScore = 0,     // placeholder
        percentage = 0.0  // placeholder
    )
}


fun Long.toInspectionId() = InspectionId(this)