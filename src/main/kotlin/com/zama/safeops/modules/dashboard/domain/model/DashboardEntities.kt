/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.domain.model

import java.time.Instant
import java.time.LocalDate

data class DashboardHazardSummary(
    val id: Long,
    val title: String,
    val status: String,
    val assignedTo: Long?,
    val createdAt: Instant
)

data class DashboardEventTrendPoint(
    val date: LocalDate,
    val total: Int,
    val incidents: Int,
    val nearMisses: Int,
    val unsafeConditions: Int,
    val unsafeActs: Int,
    val observations: Int
)

data class DashboardSummary(
    val activeHazardCount: Int,
    val submittedInspectionCount: Int,
    val unacknowledgedAlertCount: Int,
    val recentEventCount: Int,
    val activeOfficerCount: Int
)