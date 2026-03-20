/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.dashboard.api.dto.DashboardFilterRequest
import com.zama.safeops.modules.dashboard.api.mappers.toResponse
import com.zama.safeops.modules.dashboard.application.services.DashboardService
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RequiresRole("ADMIN", "OFFICER", "VIEWER")
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

    @GetMapping("/inspections/top-failing")
    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    fun topFailing() = ok(
        "Top failing inspections",
        dashboardService.getTopFailingInspections()
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

    @GetMapping("/inspections/score-trend")
    fun scoreTrend() = ok(
        "Inspection score trend",
        dashboardService.getInspectionScoreTrend()
    )

    @GetMapping("/inspections/reviewer-comments")
    fun reviewerComments() = ok(
        "Reviewer comments summary",
        dashboardService.getReviewerCommentsSummary()
    )

    // Additional endpoints for backward compatibility with client expectations

    @GetMapping("/stats")
    fun stats() = ok(
        "Dashboard statistics retrieved successfully",
        dashboardService.getSummary().toResponse()
    )

    @GetMapping("/recent-inspections")
    fun recentInspections() = ok(
        "Recent inspections retrieved successfully",
        dashboardService.getTopFailingInspections(limit = 10)
    )
}