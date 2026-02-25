/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.dto

import com.zama.safeops.modules.inspections.domain.model.InspectionItemStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionSortField
import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.shared.entities.SortDirection
import java.time.LocalDate

data class CreateInspectionRequest(
    val title: String,
    val targetType: InspectionTargetType,
    val targetId: Long,
    val inspectorId: Long
)

data class AddInspectionItemRequest(
    val title: String,
    val status: InspectionItemStatus,
    val comment: String?
)

data class InspectionResponse(
    val id: Long,
    val title: String,
    val targetType: InspectionTargetType,
    val targetId: Long,
    val inspectorId: Long,
    val assignedReviewerId: Long?,
    val reviewerComments: String?,
    val status: InspectionStatus,
    val createdAt: String,
    val updatedAt: String,
    val items: List<InspectionItemResponse>
)

data class InspectionItemResponse(
    val id: Long,
    val title: String,
    val status: InspectionItemStatus,
    val comment: String?,
    val createdAt: String
)

data class AssignReviewerRequest(
    val reviewerId: Long
)

data class ApproveInspectionRequest(
    val reviewerComments: String?
)

data class RejectInspectionRequest(
    val reviewerComments: String?
)

data class InspectionFilterRequest(
    val status: InspectionStatus? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val officerId: Long? = null,
    val locationType: SafetyLocationType? = null,
    val locationId: Long? = null,
    val search: String? = null,
    val sortBy: InspectionSortField = InspectionSortField.DATE,
    val direction: SortDirection = SortDirection.DESC
)