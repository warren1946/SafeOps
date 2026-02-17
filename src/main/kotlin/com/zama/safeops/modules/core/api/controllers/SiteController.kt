/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateSiteRequest
import com.zama.safeops.modules.core.api.dto.SiteResponse
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.SiteService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/sites")
class SiteController(
    private val siteService: SiteService
) {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateSiteRequest): SiteResponse =
        siteService.createSite(req.name, req.mineId).toResponse()

    @GetMapping
    fun list(): List<SiteResponse> =
        siteService.listSites().map { it.toResponse() }
}