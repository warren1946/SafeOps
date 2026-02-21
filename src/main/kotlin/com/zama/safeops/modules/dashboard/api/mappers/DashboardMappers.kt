/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.api.mappers

import com.zama.safeops.modules.dashboard.api.dto.DashboardEventTrendPointResponse
import com.zama.safeops.modules.dashboard.api.dto.DashboardHazardSummaryResponse
import com.zama.safeops.modules.dashboard.api.dto.DashboardSummaryResponse
import com.zama.safeops.modules.dashboard.domain.model.DashboardEventTrendPoint
import com.zama.safeops.modules.dashboard.domain.model.DashboardHazardSummary
import com.zama.safeops.modules.dashboard.domain.model.DashboardSummary

fun DashboardHazardSummary.toResponse() = DashboardHazardSummaryResponse(
    id = id,
    title = title,
    status = status,
    assignedTo = assignedTo,
    createdAt = createdAt.toString()
)

fun DashboardEventTrendPoint.toResponse() = DashboardEventTrendPointResponse(
    date = date,
    total = total,
    incidents = incidents,
    nearMisses = nearMisses,
    unsafeConditions = unsafeConditions,
    unsafeActs = unsafeActs,
    observations = observations
)

fun DashboardSummary.toResponse() = DashboardSummaryResponse(
    activeHazardCount = activeHazardCount,
    submittedInspectionCount = submittedInspectionCount,
    unacknowledgedAlertCount = unacknowledgedAlertCount,
    recentEventCount = recentEventCount,
    activeOfficerCount = activeOfficerCount
)