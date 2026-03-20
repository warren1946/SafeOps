/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.reporting.infrastructure.adapters

import com.zama.safeops.modules.reporting.application.services.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate

/**
 * Stub implementation of InspectionMetricsPort.
 */
@Component
class StubInspectionMetricsAdapter : InspectionMetricsPort {
    override fun countTodayInspections(tenantId: TenantId): Int = 0
    override fun getTrend(tenantId: TenantId, days: Int): TrendDirection =
        TrendDirection(0, TrendDirection.Direction.STABLE, "")

    override fun calculateComplianceRate(tenantId: TenantId, from: LocalDate, to: LocalDate): Double = 0.0
    override fun getComplianceTrend(tenantId: TenantId, months: Int): List<CompliancePoint> = emptyList()
    override fun countWhatsAppActiveUsers(tenantId: TenantId): Int = 0
    override fun countActiveOfficers(tenantId: TenantId): Int = 0
    override fun getMonthlyStats(tenantId: TenantId, from: LocalDate, to: LocalDate): List<MonthlyInspectionStats> = emptyList()
    override fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): InspectionStats =
        InspectionStats(0, 0, 0)

    override fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int = 0
    override fun countCompleted(tenantId: TenantId, from: Instant, to: Instant): Int = 0
    override fun calculateAverageScore(tenantId: TenantId, from: Instant, to: Instant): Double = 0.0
    override fun getRecentInspections(tenantId: TenantId, limit: Int): List<InspectionSummary> = emptyList()
}

/**
 * Stub implementation of HazardMetricsPort.
 */
@Component
class StubHazardMetricsAdapter : HazardMetricsPort {
    override fun countOpenHazards(tenantId: TenantId): Int = 0
    override fun countCriticalHazards(tenantId: TenantId): Int = 0
    override fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): HazardStats =
        HazardStats(0, 0, 0)

    override fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int = 0
    override fun countResolved(tenantId: TenantId, from: Instant, to: Instant): Int = 0
    override fun getActiveHazards(tenantId: TenantId, limit: Int): List<HazardSummary> = emptyList()
}

/**
 * Stub implementation of SafetyMetricsPort.
 */
@Component
class StubSafetyMetricsAdapter : SafetyMetricsPort {
    override fun getIncidentsByCategory(tenantId: TenantId, from: LocalDate, to: LocalDate): List<CategoryStat> = emptyList()
    override fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): SafetyEventStats =
        SafetyEventStats(0, emptyMap())

    override fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int = 0
    override fun countCritical(tenantId: TenantId, from: Instant, to: Instant): Int = 0
}
