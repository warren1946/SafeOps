/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.domain.model

import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionItemId
import java.time.Instant

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
    val updatedAt: Instant = Instant.now()
)

data class InspectionItem(
    val id: InspectionItemId? = null,
    val inspectionId: InspectionId,
    val title: String,
    val status: InspectionItemStatus,
    val comment: String? = null,
    val createdAt: Instant = Instant.now()
)

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