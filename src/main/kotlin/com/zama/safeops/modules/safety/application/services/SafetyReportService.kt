/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.safety.api.dto.CreateSafetyReportRequest
import com.zama.safeops.modules.safety.api.mappers.toPeriod
import com.zama.safeops.modules.safety.application.exceptions.SafetyInvalidInputException
import com.zama.safeops.modules.safety.application.exceptions.SafetyReportNotFoundException
import com.zama.safeops.modules.safety.application.ports.SafetyEventPort
import com.zama.safeops.modules.safety.application.ports.SafetyReportPort
import com.zama.safeops.modules.safety.domain.model.SafetyReport
import com.zama.safeops.modules.safety.domain.model.SafetySeverity
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyReportId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SafetyReportService(
    private val reportPort: SafetyReportPort,
    private val eventPort: SafetyEventPort,
    private val userPort: UserPort
) {

    @Transactional
    fun create(req: CreateSafetyReportRequest): SafetyReport {
        val (start, end) = req.toPeriod()

        if (req.title.isBlank()) {
            throw SafetyInvalidInputException("Report title cannot be blank")
        }

        if (start.isAfter(end)) {
            throw SafetyInvalidInputException("periodStart must be before periodEnd")
        }

        if (!userPort.existsById(UserId(req.createdBy))) {
            throw SafetyInvalidInputException("User with ID ${req.createdBy} does not exist")
        }

        val events = eventPort.findByPeriod(start, end)

        val eventCount = events.size
        val highCount = events.count { it.severity == SafetySeverity.HIGH }
        val criticalCount = events.count { it.severity == SafetySeverity.CRITICAL }

        val summary = buildString {
            append("Safety report for period $start to $end. ")
            append("Total events: $eventCount. ")
            append("High severity: $highCount. ")
            append("Critical severity: $criticalCount.")
        }

        val saved = reportPort.create(
            SafetyReport(
                title = req.title,
                periodStart = start,
                periodEnd = end,
                createdBy = req.createdBy,
                summary = summary,
                eventCount = eventCount,
                highSeverityCount = highCount,
                criticalSeverityCount = criticalCount
            )
        )

        if (saved.id == null) error("SafetyReport ID must not be null after persistence")
        return saved
    }

    @Transactional(readOnly = true)
    fun get(id: Long): SafetyReport =
        reportPort.findById(SafetyReportId(id)) ?: throw SafetyReportNotFoundException(id)
}