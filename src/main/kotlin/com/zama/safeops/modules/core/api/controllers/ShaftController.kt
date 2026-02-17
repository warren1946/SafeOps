/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateShaftRequest
import com.zama.safeops.modules.core.api.dto.ShaftResponse
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.ShaftService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/shafts")
class ShaftController(
    private val shaftService: ShaftService
) {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateShaftRequest): ShaftResponse =
        shaftService.createShaft(req.name, req.siteId).toResponse()

    @GetMapping
    fun list(): List<ShaftResponse> =
        shaftService.listShafts().map { it.toResponse() }
}