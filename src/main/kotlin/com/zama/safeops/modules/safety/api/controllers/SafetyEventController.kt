/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.controllers

import com.zama.safeops.modules.safety.api.dto.CreateSafetyEventRequest
import com.zama.safeops.modules.safety.api.mappers.toResponse
import com.zama.safeops.modules.safety.application.services.SafetyEventService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/safety/events")
class SafetyEventController(
    private val safetyEventService: SafetyEventService
) : ApiController() {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateSafetyEventRequest) = created(
        "Safety event created successfully",
        safetyEventService.create(req).toResponse()
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Safety event retrieved successfully",
        safetyEventService.get(id).toResponse()
    )
}