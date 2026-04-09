/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.dto

import com.zama.safeops.config.validation.Sanitized
import com.zama.safeops.modules.inspections.domain.model.InspectionItemStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionSortField
import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.shared.entities.SortDirection
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

private const val MAX_TITLE_LENGTH = 200
private const val MAX_COMMENT_LENGTH = 5000
private const val MAX_SEARCH_LENGTH = 200

data class CreateInspectionRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = MAX_TITLE_LENGTH, message = "Title must not exceed $MAX_TITLE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_TITLE_LENGTH)
    val title: String,

    @field:NotNull(message = "Target type is required")
    val targetType: InspectionTargetType,

    @field:NotNull(message = "Target ID is required")
    @field:Min(1, message = "Target ID must be positive")
    val targetId: Long,

    @field:NotNull(message = "Inspector ID is required")
    @field:Min(1, message = "Inspector ID must be positive")
    val inspectorId: Long,

    @field:NotNull(message = "Template ID is required")
    @field:Min(1, message = "Template ID must be positive")
    val templateId: Long
)

data class AddInspectionItemRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = MAX_TITLE_LENGTH, message = "Title must not exceed $MAX_TITLE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_TITLE_LENGTH)
    val title: String,

    @field:NotNull(message = "Status is required")
    val status: InspectionItemStatus,

    @field:Size(max = MAX_COMMENT_LENGTH, message = "Comment must not exceed $MAX_COMMENT_LENGTH characters")
    @field:Sanitized(maxLength = MAX_COMMENT_LENGTH)
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
    @field:NotNull(message = "Reviewer ID is required")
    @field:Min(1, message = "Reviewer ID must be positive")
    val reviewerId: Long
)

data class ApproveInspectionRequest(
    @field:Size(max = MAX_COMMENT_LENGTH, message = "Comments must not exceed $MAX_COMMENT_LENGTH characters")
    @field:Sanitized(maxLength = MAX_COMMENT_LENGTH)
    val reviewerComments: String?
)

data class RejectInspectionRequest(
    @field:Size(max = MAX_COMMENT_LENGTH, message = "Comments must not exceed $MAX_COMMENT_LENGTH characters")
    @field:Sanitized(maxLength = MAX_COMMENT_LENGTH)
    val reviewerComments: String?
)

data class InspectionFilterRequest(
    val status: InspectionStatus? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,

    @field:Min(1, message = "Officer ID must be positive")
    val officerId: Long? = null,

    val locationType: SafetyLocationType? = null,

    @field:Min(1, message = "Location ID must be positive")
    val locationId: Long? = null,

    @field:Size(max = MAX_SEARCH_LENGTH, message = "Search term too long")
    @field:Sanitized(maxLength = MAX_SEARCH_LENGTH)
    val search: String? = null,

    val sortBy: InspectionSortField = InspectionSortField.DATE,
    val direction: SortDirection = SortDirection.DESC
) {
    init {
        // Validate date range
        if (fromDate != null && toDate != null) {
            require(!fromDate.isAfter(toDate)) { "From date cannot be after to date" }
        }
    }
}
