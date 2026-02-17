/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateAreaRequest
import com.zama.safeops.modules.core.api.dto.AreaResponse
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.AreaService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/areas")
class AreaController(
    private val areaService: AreaService
) {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateAreaRequest): AreaResponse =
        areaService.createArea(req.name, req.shaftId).toResponse()

    @GetMapping
    fun list(): List<AreaResponse> =
        areaService.listAreas().map { it.toResponse() }
}