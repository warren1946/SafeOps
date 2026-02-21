/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.dto

import com.zama.safeops.modules.safety.domain.model.SafetyLocationType

data class CreateHazardRequest(
    val title: String,
    val description: String,
    val locationType: SafetyLocationType,
    val locationId: Long
)

data class UpdateHazardRequest(
    val title: String,
    val description: String,
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
    val status: String,
    val assignedTo: Long?,
    val locationType: SafetyLocationType,
    val locationId: Long,
    val createdAt: String,
    val updatedAt: String
)