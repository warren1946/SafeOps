/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.controllers

import com.zama.safeops.modules.safety.api.dto.AcknowledgeAlertRequest
import com.zama.safeops.modules.safety.api.mappers.toResponse
import com.zama.safeops.modules.safety.application.services.SafetyAlertService
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/safety/alerts")
class SafetyAlertController(
    private val safetyAlertService: SafetyAlertService
) : ApiController() {

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Safety alert retrieved successfully",
        safetyAlertService.get(id).toResponse()
    )

    @PostMapping("/{id}/acknowledge")
    fun acknowledge(
        @PathVariable id: Long,
        @RequestBody req: AcknowledgeAlertRequest
    ) = ok(
        "Safety alert acknowledged successfully",
        safetyAlertService.acknowledge(id).toResponse()
    )
}