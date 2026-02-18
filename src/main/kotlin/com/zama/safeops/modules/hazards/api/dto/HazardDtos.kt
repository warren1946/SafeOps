/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.dto

data class CreateHazardRequest(
    val title: String,
    val description: String
)

data class UpdateHazardRequest(
    val title: String,
    val description: String
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
    val createdAt: String,
    val updatedAt: String
)