/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.safety.application.ports.SafetyReportPort
import com.zama.safeops.modules.safety.domain.model.SafetyReport
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyReportId
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyReportJpaEntity
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories.SpringDataSafetyReportRepository
import org.springframework.stereotype.Component

@Component
class SafetyReportJpaAdapter(
    private val repo: SpringDataSafetyReportRepository
) : SafetyReportPort {

    override fun create(report: SafetyReport): SafetyReport =
        repo.save(report.toEntity()).toDomain()

    override fun findById(id: SafetyReportId): SafetyReport? =
        repo.findById(id.value).orElse(null)?.toDomain()
}

private fun SafetyReport.toEntity() = SafetyReportJpaEntity(
    id = id?.value,
    title = title,
    periodStart = periodStart,
    periodEnd = periodEnd,
    createdBy = createdBy,
    createdAt = createdAt,
    summary = summary,
    eventCount = eventCount,
    highSeverityCount = highSeverityCount,
    criticalSeverityCount = criticalSeverityCount
)

private fun SafetyReportJpaEntity.toDomain() = SafetyReport(
    id = id?.let { SafetyReportId(it) },
    title = title,
    periodStart = periodStart,
    periodEnd = periodEnd,
    createdBy = createdBy,
    createdAt = createdAt,
    summary = summary,
    eventCount = eventCount,
    highSeverityCount = highSeverityCount,
    criticalSeverityCount = criticalSeverityCount
)