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
    id = id!!.value,
    title = title.value,
    description = description.value,
    severity = severity,
    priority = priority,
    status = status.name,
    assignedTo = assignedTo,
    createdBy = createdBy,
    locationType = locationType,
    locationId = locationId,
    dueDate = dueDate?.toString(),
    resolvedAt = resolvedAt?.toString(),
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString()
)