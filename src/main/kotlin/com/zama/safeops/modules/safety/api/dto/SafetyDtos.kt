/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.dto

import com.zama.safeops.modules.safety.domain.model.SafetyAlertType
import com.zama.safeops.modules.safety.domain.model.SafetyEventType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.safety.domain.model.SafetySeverity

data class CreateSafetyEventRequest(
    val type: SafetyEventType,
    val description: String,
    val severity: SafetySeverity,
    val locationType: SafetyLocationType,
    val locationId: Long,
    val reporterId: Long
)

data class SafetyEventResponse(
    val id: Long,
    val type: SafetyEventType,
    val description: String,
    val severity: SafetySeverity,
    val locationType: SafetyLocationType,
    val locationId: Long,
    val reporterId: Long,
    val createdAt: String
)

data class CreateSafetyAlertRequest(
    val eventId: Long,
    val alertType: SafetyAlertType,
    val message: String,
    val recipientId: Long
)

data class SafetyAlertResponse(
    val id: Long,
    val eventId: Long,
    val alertType: SafetyAlertType,
    val message: String,
    val recipientId: Long,
    val acknowledged: Boolean,
    val acknowledgedAt: String?,
    val createdAt: String
)

data class AcknowledgeAlertRequest(
    val acknowledged: Boolean = true
)

data class CreateSafetyReportRequest(
    val title: String,
    val periodStart: String,
    val periodEnd: String,
    val createdBy: Long
)

data class SafetyReportResponse(
    val id: Long,
    val title: String,
    val periodStart: String,
    val periodEnd: String,
    val createdBy: Long,
    val createdAt: String,
    val summary: String,
    val eventCount: Int,
    val highSeverityCount: Int,
    val criticalSeverityCount: Int
)