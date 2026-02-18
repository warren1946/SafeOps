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
    id = this.id!!.value,
    title = this.title.value,
    description = this.description.value,
    status = this.status.name,
    assignedTo = this.assignedTo,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)