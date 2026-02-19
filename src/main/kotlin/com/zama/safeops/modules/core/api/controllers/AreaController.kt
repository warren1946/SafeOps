/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateAreaRequest
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.AreaService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/areas")
class AreaController(private val areaService: AreaService) : ApiController() {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateAreaRequest) = created(
        "Area created successfully",
        areaService.createArea(req.name, req.shaftId).toResponse()
    )

    @GetMapping
    fun list() = ok(
        "Areas retrieved successfully",
        areaService.listAreas().map { it.toResponse() }
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Area retrieved successfully",
        areaService.getArea(id).toResponse()
    )
}