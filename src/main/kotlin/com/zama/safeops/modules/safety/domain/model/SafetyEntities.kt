/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.domain.model

import com.zama.safeops.modules.safety.domain.valueobjects.SafetyAlertId
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyReportId
import java.time.Instant

data class SafetyEvent(
    val id: SafetyEventId? = null,
    val type: SafetyEventType,
    val description: String,
    val severity: SafetySeverity,
    val locationType: SafetyLocationType,
    val locationId: Long,
    val reporterId: Long,
    val createdAt: Instant = Instant.now()
)

data class SafetyAlert(
    val id: SafetyAlertId? = null,
    val eventId: SafetyEventId,
    val alertType: SafetyAlertType,
    val message: String,
    val recipientId: Long,
    val acknowledged: Boolean = false,
    val acknowledgedAt: Instant? = null,
    val createdAt: Instant = Instant.now()
)

data class SafetyReport(
    val id: SafetyReportId? = null,
    val title: String,
    val periodStart: Instant,
    val periodEnd: Instant,
    val createdBy: Long,
    val createdAt: Instant = Instant.now(),
    val summary: String,
    val eventCount: Int,
    val highSeverityCount: Int,
    val criticalSeverityCount: Int
)

enum class SafetyEventType {
    INCIDENT,
    NEAR_MISS,
    UNSAFE_CONDITION,
    UNSAFE_ACT,
    OBSERVATION
}

enum class SafetySeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class SafetyLocationType {
    AREA,
    SHAFT,
    SITE
}

enum class SafetyAlertType {
    HIGH_SEVERITY_EVENT,
    CRITICAL_SEVERITY_EVENT,
    MANUAL_ESCALATION,
    FOLLOW_UP_REQUIRED
}