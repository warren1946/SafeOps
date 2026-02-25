/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.hazards.api.dto.AssignHazardRequest
import com.zama.safeops.modules.hazards.api.dto.CreateHazardRequest
import com.zama.safeops.modules.hazards.api.dto.HazardFilterCriteria
import com.zama.safeops.modules.hazards.api.dto.UpdateHazardRequest
import com.zama.safeops.modules.hazards.api.mappers.toResponse
import com.zama.safeops.modules.hazards.application.services.HazardQueryService
import com.zama.safeops.modules.hazards.application.services.HazardService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/hazards")
class HazardController(private val hazardService: HazardService, private val hazardQueryService: HazardQueryService) : ApiController() {

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping
    fun create(@Valid @RequestBody req: CreateHazardRequest) = created(
        "Hazard created successfully",
        hazardService.create(req, currentUserId()).toResponse()
    )

    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    @GetMapping
    fun list() = ok(
        "Hazards retrieved successfully",
        hazardService.list().map { it.toResponse() }
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Hazard retrieved successfully",
        hazardService.get(id).toResponse()
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody req: UpdateHazardRequest) = ok(
        "Hazard updated successfully",
        hazardService.update(
            id,
            req.title,
            req.description,
            req.locationType,
            req.locationId
        ).toResponse()
    )

    @PostMapping("/{id}/resolve")
    fun resolve(@PathVariable id: Long) = ok(
        "Hazard resolved successfully",
        hazardService.resolve(id).toResponse()
    )

    @PostMapping("/{id}/assign")
    fun assign(@PathVariable id: Long, @Valid @RequestBody req: AssignHazardRequest) = ok(
        "Hazard assigned successfully",
        hazardService.assign(id, req.userId).toResponse()
    )

    @PostMapping("/filter")
    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    fun filter(@RequestBody criteria: HazardFilterCriteria) = ok(
        "Filtered hazards",
        hazardQueryService.getFiltered(criteria).map { it.toResponse() }
    )
}