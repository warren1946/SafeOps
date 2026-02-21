/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.api.controllers

import com.zama.safeops.modules.dashboard.api.dto.DashboardFilterRequest
import com.zama.safeops.modules.dashboard.api.mappers.toResponse
import com.zama.safeops.modules.dashboard.application.services.DashboardService
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) : ApiController() {

    @GetMapping("/summary")
    fun summary() = ok(
        "Dashboard summary retrieved successfully",
        dashboardService.getSummary().toResponse()
    )

    @GetMapping("/hazards/active")
    fun activeHazards() = ok(
        "Active hazards retrieved successfully",
        dashboardService.getActiveHazards(limit = 5).map { it.toResponse() }
    )

    @GetMapping("/events/trends")
    fun eventTrends() = ok(
        "Event trends retrieved successfully",
        dashboardService.getEventTrends().map { it.toResponse() }
    )

    @PostMapping("/summary/filter")
    fun summaryFiltered(@RequestBody req: DashboardFilterRequest) = ok(
        "Filtered dashboard summary retrieved successfully",
        dashboardService.getSummaryFiltered(req).toResponse()
    )

    @PostMapping("/hazards/active/filter")
    fun activeHazardsFiltered(@RequestBody req: DashboardFilterRequest) = ok(
        "Filtered active hazards retrieved successfully",
        dashboardService.getActiveHazardsFiltered(req).map { it.toResponse() }
    )

    @PostMapping("/events/trends/filter")
    fun eventTrendsFiltered(@RequestBody req: DashboardFilterRequest) = ok(
        "Filtered event trends retrieved successfully",
        dashboardService.getEventTrendsFiltered(req).map { it.toResponse() }
    )
}