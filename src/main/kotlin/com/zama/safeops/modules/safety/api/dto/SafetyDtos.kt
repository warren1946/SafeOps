/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api.dto

import com.zama.safeops.config.validation.Sanitized
import com.zama.safeops.modules.safety.domain.model.SafetyAlertType
import com.zama.safeops.modules.safety.domain.model.SafetyEventType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.safety.domain.model.SafetySeverity
import jakarta.validation.constraints.*

private const val MAX_DESCRIPTION_LENGTH = 5000
private const val MAX_MESSAGE_LENGTH = 2000
private const val MAX_TITLE_LENGTH = 200

data class CreateSafetyEventRequest(
    @field:NotNull(message = "Event type is required")
    val type: SafetyEventType,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "Description must not exceed $MAX_DESCRIPTION_LENGTH characters")
    @field:Sanitized(maxLength = MAX_DESCRIPTION_LENGTH)
    val description: String,

    @field:NotNull(message = "Severity is required")
    val severity: SafetySeverity,

    @field:NotNull(message = "Location type is required")
    val locationType: SafetyLocationType,

    @field:NotNull(message = "Location ID is required")
    @field:Min(1, message = "Location ID must be positive")
    val locationId: Long,

    @field:NotNull(message = "Reporter ID is required")
    @field:Min(1, message = "Reporter ID must be positive")
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
    @field:NotNull(message = "Event ID is required")
    @field:Min(1, message = "Event ID must be positive")
    val eventId: Long,

    @field:NotNull(message = "Alert type is required")
    val alertType: SafetyAlertType,

    @field:NotBlank(message = "Message is required")
    @field:Size(max = MAX_MESSAGE_LENGTH, message = "Message must not exceed $MAX_MESSAGE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_MESSAGE_LENGTH)
    val message: String,

    @field:NotNull(message = "Recipient ID is required")
    @field:Min(1, message = "Recipient ID must be positive")
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
    @field:NotBlank(message = "Title is required")
    @field:Size(max = MAX_TITLE_LENGTH, message = "Title must not exceed $MAX_TITLE_LENGTH characters")
    @field:Sanitized(maxLength = MAX_TITLE_LENGTH)
    val title: String,

    @field:NotBlank(message = "Period start is required")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Period start must be in YYYY-MM-DD format"
    )
    val periodStart: String,

    @field:NotBlank(message = "Period end is required")
    @field:Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Period end must be in YYYY-MM-DD format"
    )
    val periodEnd: String,

    @field:NotNull(message = "Created by ID is required")
    @field:Min(1, message = "Created by ID must be positive")
    val createdBy: Long
) {
    init {
        // Validate date range
        require(periodStart <= periodEnd) { "Period start cannot be after period end" }
    }
}

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
