/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.dto

import com.zama.safeops.modules.hazards.domain.model.HazardPriority
import com.zama.safeops.modules.hazards.domain.model.HazardSeverity
import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.shared.entities.SortDirection
import java.time.Instant

data class CreateHazardRequest(
    val title: String,
    val description: String,
    val severity: HazardSeverity = HazardSeverity.MEDIUM,
    val priority: HazardPriority? = null, // optional override
    val dueDate: Instant? = null,
    val locationType: SafetyLocationType,
    val locationId: Long
)

data class UpdateHazardRequest(
    val title: String,
    val description: String,
    val severity: HazardSeverity,
    val priority: HazardPriority?,
    val dueDate: Instant?,
    val locationType: SafetyLocationType,
    val locationId: Long
)

data class AssignHazardRequest(
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
    val locationType: SafetyLocationType,
    val locationId: Long,
    val dueDate: String?,
    val resolvedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class HazardFilterCriteria(
    val severity: HazardSeverity? = null,
    val priority: HazardPriority? = null,
    val status: HazardStatus? = null,
    val assignedTo: Long? = null,
    val overdueOnly: Boolean = false,
    val locationType: SafetyLocationType? = null,
    val locationId: Long? = null,
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
