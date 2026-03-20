/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.infrastructure.adapters

import com.zama.safeops.modules.analytics.application.ports.ReportGenerator
import com.zama.safeops.modules.analytics.domain.model.*
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Stub implementation of ReportGenerator.
 */
@Component
class StubReportGenerator : ReportGenerator {
    override fun generateReport(definition: ReportDefinition, period: DateRange, format: ReportFormat): GeneratedReport {
        return GeneratedReport(
            id = java.util.UUID.randomUUID().toString(),
            definitionId = definition.id,
            tenantId = definition.tenantId,
            generatedAt = Instant.now(),
            period = period,
            format = format,
            fileUrl = "",
            fileSize = 0,
            status = GenerationStatus.COMPLETED,
            errorMessage = null
        )
    }
}
