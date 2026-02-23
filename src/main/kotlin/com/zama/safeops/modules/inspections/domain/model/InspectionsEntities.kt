/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.domain.model

import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionItemId
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import java.time.Instant
import java.time.LocalDate

data class Inspection(
    val id: InspectionId? = null,
    val title: String,
    val targetType: InspectionTargetType,
    val targetId: Long,
    val inspectorId: Long,
    val assignedReviewerId: Long? = null,
    val reviewerComments: String? = null,
    val status: InspectionStatus = InspectionStatus.DRAFT,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val items: List<InspectionItem> = emptyList()
)

data class InspectionItem(
    val id: InspectionItemId? = null,
    val inspectionId: InspectionId,
    val title: String,
    val status: InspectionItemStatus,
    val comment: String? = null,
    val createdAt: Instant = Instant.now()
)

data class InspectionScore(
    val score: Int,
    val maxScore: Int,
    val percentage: Double
)

data class InspectionFilterCriteria(
    val status: InspectionStatus? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val officerId: Long? = null,
    val locationType: SafetyLocationType? = null,
    val locationId: Long? = null,
    val search: String? = null,
    val sortBy: InspectionSortField = InspectionSortField.DATE,
    val direction: SortDirection = SortDirection.DESC,
    val onlyFailing: Boolean = false
)

data class InspectionSummaryResponse(
    val id: Long,
    val title: String,
    val status: InspectionStatus,
    val targetType: InspectionTargetType,
    val targetId: Long,
    val officerId: Long?,
    val performedAt: Instant?,
    val score: Int,
    val maxScore: Int,
    val percentage: Double,
    val completeness: Double
)

enum class InspectionSortField {
    DATE,
    SCORE,
    STATUS,
    OFFICER
}

enum class SortDirection {
    ASC,
    DESC
}

enum class InspectionStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED
}

enum class InspectionItemStatus {
    PASS,
    FAIL,
    NOT_APPLICABLE
}

enum class InspectionTargetType {
    AREA,
    SHAFT,
    SITE
}