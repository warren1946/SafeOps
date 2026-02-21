/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.api.dto

import java.time.LocalDate

data class DashboardHazardSummaryResponse(
    val id: Long,
    val title: String,
    val status: String,
    val assignedTo: Long?,
    val createdAt: String
)

data class DashboardEventTrendPointResponse(
    val date: LocalDate,
    val total: Int,
    val incidents: Int,
    val nearMisses: Int,
    val unsafeConditions: Int,
    val unsafeActs: Int,
    val observations: Int
)

data class DashboardSummaryResponse(
    val activeHazardCount: Int,
    val submittedInspectionCount: Int,
    val unacknowledgedAlertCount: Int,
    val recentEventCount: Int,
    val activeOfficerCount: Int
)

data class ComplianceTrendResponse(
    val month: String,
    val rate: Int
)

data class DashboardFilterRequest(
    val type: FilterType,
    val id: Long
)

enum class FilterType {
    SITE,
    SHAFT,
    AREA
}