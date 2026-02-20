/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.mappers

import com.zama.safeops.modules.hazards.api.dto.HazardResponse
import com.zama.safeops.modules.hazards.domain.model.Hazard

fun Hazard.toResponse() = HazardResponse(
    id = id?.value ?: error("Hazard ID must not be null"),
    title = title.value,
    description = description.value,
    status = status.name,
    assignedTo = assignedTo,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)