/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateSiteRequest
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.SiteService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/sites")
class SiteController(private val siteService: SiteService) : ApiController() {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateSiteRequest) = created(
        "Site created successfully",
        siteService.createSite(req.name, req.mineId).toResponse()
    )

    @GetMapping
    fun list() = ok(
        "Sites retrieved successfully",
        siteService.listSites().map { it.toResponse() }
    )
}