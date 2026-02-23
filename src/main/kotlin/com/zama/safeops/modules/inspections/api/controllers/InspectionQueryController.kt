/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.inspections.api.dto.InspectionFilterRequest
import com.zama.safeops.modules.inspections.api.mappers.toSummaryResponse
import com.zama.safeops.modules.inspections.application.services.InspectionQueryService
import com.zama.safeops.modules.inspections.domain.model.InspectionFilterCriteria
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/inspections")
class InspectionQueryController(
    private val inspectionQueryService: InspectionQueryService
) : ApiController() {

    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    @PostMapping("/filter")
    fun filter(@RequestBody req: InspectionFilterRequest) = ok(
        "Inspections retrieved successfully",
        inspectionQueryService
            .getFiltered(req.toCriteria())
            .map { it.toSummaryResponse() }
    )
}

private fun InspectionFilterRequest.toCriteria() = InspectionFilterCriteria(
    status = this.status,
    fromDate = this.fromDate,
    toDate = this.toDate,
    officerId = this.officerId,
    locationType = this.locationType,
    locationId = this.locationId,
    search = this.search,
    sortBy = this.sortBy,
    direction = this.direction
)