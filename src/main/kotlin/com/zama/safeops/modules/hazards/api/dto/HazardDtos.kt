/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.dto

import com.zama.safeops.config.validation.Sanitized
import com.zama.safeops.modules.hazards.domain.model.HazardPriority
import com.zama.safeops.modules.hazards.domain.model.HazardSeverity
import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.shared.entities.SortDirection
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

private const val MAX_TITLE_LENGTH = 200
private const val MAX_DESCRIPTION_LENGTH = 5000
private const val MAX_SEARCH_LENGTH = 200

data class CreateHazardRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = MAX_TITLE_LENGTH, message = "Title must not exceed $MAX_TITLE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_TITLE_LENGTH)
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "Description must not exceed $MAX_DESCRIPTION_LENGTH characters")
    @field:Sanitized(maxLength = MAX_DESCRIPTION_LENGTH)
    val description: String,

    @field:NotNull(message = "Severity is required")
    val severity: HazardSeverity = HazardSeverity.MEDIUM,

    val priority: HazardPriority? = null,
    val dueDate: Instant? = null,
    val locationType: SafetyLocationType? = null,

    @field:Min(1, message = "Location ID must be positive")
    val locationId: Long? = null
)

data class UpdateHazardRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = MAX_TITLE_LENGTH, message = "Title must not exceed $MAX_TITLE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_TITLE_LENGTH)
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "Description must not exceed $MAX_DESCRIPTION_LENGTH characters")
    @field:Sanitized(maxLength = MAX_DESCRIPTION_LENGTH)
    val description: String,

    @field:NotNull(message = "Severity is required")
    val severity: HazardSeverity,

    val priority: HazardPriority?,
    val dueDate: Instant?,
    val locationType: SafetyLocationType? = null,

    @field:Min(1, message = "Location ID must be positive")
    val locationId: Long? = null
)

data class AssignHazardRequest(
    @field:NotNull(message = "User ID is required")
    @field:Min(1, message = "User ID must be positive")
    val userId: Long
)

data class HazardResponse(
    val id: Long,
    val title: String,
    val description: String,
    val severity: HazardSeverity,
    val priority: HazardPriority,
    val status: String,
    val assignedTo: Long?,
    val createdBy: Long?,
    val locationType: SafetyLocationType?,
    val locationId: Long?,
    val dueDate: String?,
    val resolvedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class HazardFilterCriteria(
    val severity: HazardSeverity? = null,
    val priority: HazardPriority? = null,
    val status: HazardStatus? = null,

    @field:Min(1, message = "Assigned To ID must be positive")
    val assignedTo: Long? = null,

    val overdueOnly: Boolean = false,
    val locationType: SafetyLocationType? = null,

    @field:Min(1, message = "Location ID must be positive")
    val locationId: Long? = null,

    @field:Size(max = MAX_SEARCH_LENGTH, message = "Search term too long")
    @field:Sanitized(maxLength = MAX_SEARCH_LENGTH, checkSql = true)
    val search: String? = null,

    val sortBy: HazardSortField = HazardSortField.DATE,
    val direction: SortDirection = SortDirection.DESC
)

enum class HazardSortField {
    DATE,
    SEVERITY,
    PRIORITY,
    STATUS,
    ASSIGNED_TO,
    LOCATION,
    TITLE,
    DUE_DATE
}
