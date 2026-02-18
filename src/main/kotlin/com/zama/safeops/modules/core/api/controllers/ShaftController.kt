/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateShaftRequest
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.ShaftService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/shafts")
class ShaftController(private val shaftService: ShaftService) : ApiController() {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateShaftRequest) = created(
        "Shaft created successfully",
        shaftService.createShaft(req.name, req.siteId).toResponse()
    )

    @GetMapping
    fun list() = ok(
        "Shafts retrieved successfully",
        shaftService.listShafts().map { it.toResponse() }
    )
}