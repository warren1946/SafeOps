/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.mappers

import com.zama.safeops.modules.inspections.api.dto.InspectionItemResponse
import com.zama.safeops.modules.inspections.api.dto.InspectionResponse
import com.zama.safeops.modules.inspections.domain.model.*
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
    val relevant = items.filter { it.status != InspectionItemStatus.NOT_APPLICABLE }

    if (relevant.isEmpty()) {
        return InspectionScore(0, 0, 0.0)
    }

    val max = relevant.size
    val score = relevant.count { it.status == InspectionItemStatus.PASS }
    val pct = (score.toDouble() / max.toDouble()) * 100.0

    return InspectionScore(score, max, pct)
}

fun Inspection.toSummaryResponse(): InspectionSummaryResponse {
    val score = calculateScore()

    return InspectionSummaryResponse(
        id = id!!.value,
        title = title,
        status = status,
        targetType = targetType,
        targetId = targetId,
        officerId = inspectorId,
        performedAt = createdAt,
        score = score.score,
        maxScore = score.maxScore,
        percentage = score.percentage,
        completeness = this.completenessPercentage()
    )
}

fun Inspection.completenessPercentage(): Double {
    if (items.isEmpty()) return 0.0
    return (items.size.toDouble() / items.size.toDouble()) * 100.0
}

fun Long.toInspectionId() = InspectionId(this)