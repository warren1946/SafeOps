/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.mappers

import com.zama.safeops.modules.safety.api.dto.CreateSafetyReportRequest
import com.zama.safeops.modules.safety.api.dto.SafetyAlertResponse
import com.zama.safeops.modules.safety.api.dto.SafetyEventResponse
import com.zama.safeops.modules.safety.api.dto.SafetyReportResponse
import com.zama.safeops.modules.safety.domain.model.SafetyAlert
import com.zama.safeops.modules.safety.domain.model.SafetyEvent
import com.zama.safeops.modules.safety.domain.model.SafetyReport
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyAlertId
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyReportId
import java.time.Instant

fun SafetyEvent.toResponse() = SafetyEventResponse(
    id = id?.value ?: error("SafetyEvent ID must not be null"),
    type = type,
    description = description,
    severity = severity,
    locationType = locationType,
    locationId = locationId,
    reporterId = reporterId,
    createdAt = createdAt.toString()
)

fun SafetyAlert.toResponse() = SafetyAlertResponse(
    id = id?.value ?: error("SafetyAlert ID must not be null"),
    eventId = eventId.value,
    alertType = alertType,
    message = message,
    recipientId = recipientId,
    acknowledged = acknowledged,
    acknowledgedAt = acknowledgedAt?.toString(),
    createdAt = createdAt.toString()
)

fun SafetyReport.toResponse() = SafetyReportResponse(
    id = id?.value ?: error("SafetyReport ID must not be null"),
    title = title,
    periodStart = periodStart.toString(),
    periodEnd = periodEnd.toString(),
    createdBy = createdBy,
    createdAt = createdAt.toString(),
    summary = summary,
    eventCount = eventCount,
    highSeverityCount = highSeverityCount,
    criticalSeverityCount = criticalSeverityCount
)

fun Long.toSafetyEventId() = SafetyEventId(this)
fun Long.toSafetyAlertId() = SafetyAlertId(this)
fun Long.toSafetyReportId() = SafetyReportId(this)

fun CreateSafetyReportRequest.toPeriod(): Pair<Instant, Instant> = Instant.parse(periodStart) to Instant.parse(periodEnd)