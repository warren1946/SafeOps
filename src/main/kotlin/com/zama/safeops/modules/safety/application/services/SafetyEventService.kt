/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.core.application.ports.AreaPort
import com.zama.safeops.modules.core.application.ports.ShaftPort
import com.zama.safeops.modules.core.application.ports.SitePort
import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.safety.api.dto.CreateSafetyEventRequest
import com.zama.safeops.modules.safety.application.exceptions.SafetyEventNotFoundException
import com.zama.safeops.modules.safety.application.exceptions.SafetyInvalidInputException
import com.zama.safeops.modules.safety.application.ports.SafetyAlertPort
import com.zama.safeops.modules.safety.application.ports.SafetyEventPort
import com.zama.safeops.modules.safety.domain.model.*
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SafetyEventService(
    private val eventPort: SafetyEventPort,
    private val alertPort: SafetyAlertPort,
    private val userPort: UserPort,
    private val areaPort: AreaPort,
    private val shaftPort: ShaftPort,
    private val sitePort: SitePort
) {

    @Transactional
    fun create(req: CreateSafetyEventRequest): SafetyEvent {
        validateEventInput(req)
        validateReporter(req.reporterId)
        validateLocation(req.locationType, req.locationId)

        val saved = eventPort.create(
            SafetyEvent(
                type = req.type,
                description = req.description,
                severity = req.severity,
                locationType = req.locationType,
                locationId = req.locationId,
                reporterId = req.reporterId
            )
        )

        ensureEventId(saved.id?.value)

        if (saved.severity == SafetySeverity.HIGH || saved.severity == SafetySeverity.CRITICAL) {
            autoCreateAlert(saved)
        }

        return saved
    }

    @Transactional(readOnly = true)
    fun get(id: Long): SafetyEvent =
        eventPort.findById(SafetyEventId(id)) ?: throw SafetyEventNotFoundException(id)

    private fun validateEventInput(req: CreateSafetyEventRequest) {
        if (req.description.isBlank()) {
            throw SafetyInvalidInputException("Description cannot be blank")
        }
        if (req.locationId <= 0) {
            throw SafetyInvalidInputException("Invalid locationId: ${req.locationId}")
        }
        if (req.reporterId <= 0) {
            throw SafetyInvalidInputException("Invalid reporterId: ${req.reporterId}")
        }
    }

    private fun validateReporter(reporterId: Long) {
        if (!userPort.existsById(UserId(reporterId))) {
            throw SafetyInvalidInputException("Reporter with ID $reporterId does not exist")
        }
    }

    private fun validateLocation(type: SafetyLocationType, id: Long) {
        val exists = when (type) {
            SafetyLocationType.AREA -> areaPort.exists(AreaId(id))
            SafetyLocationType.SHAFT -> shaftPort.exists(ShaftId(id))
            SafetyLocationType.SITE -> sitePort.exists(SiteId(id))
        }
        if (!exists) {
            throw SafetyInvalidInputException("Location $type with ID $id does not exist")
        }
    }

    private fun autoCreateAlert(event: SafetyEvent) {
        val alertType = when (event.severity) {
            SafetySeverity.HIGH -> SafetyAlertType.HIGH_SEVERITY_EVENT
            SafetySeverity.CRITICAL -> SafetyAlertType.CRITICAL_SEVERITY_EVENT
            else -> return
        }

        alertPort.create(
            SafetyAlert(
                eventId = event.id ?: error("SafetyEvent ID must not be null for alert"),
                alertType = alertType,
                message = "Safety event (${event.severity}) reported: ${event.description}",
                recipientId = event.reporterId // simple default; can evolve later
            )
        )
    }

    private fun ensureEventId(id: Long?) =
        id ?: error("SafetyEvent ID must not be null after persistence")
}