/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.controllers

import com.zama.safeops.modules.safety.api.dto.CreateSafetyReportRequest
import com.zama.safeops.modules.safety.api.mappers.toResponse
import com.zama.safeops.modules.safety.application.services.SafetyReportService
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/safety/reports")
class SafetyReportController(private val safetyReportService: SafetyReportService) : ApiController() {

    @PostMapping
    fun create(@RequestBody req: CreateSafetyReportRequest) = created(
        "Safety report created successfully",
        safetyReportService.create(req).toResponse()
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Safety report retrieved successfully",
        safetyReportService.get(id).toResponse()
    )
}