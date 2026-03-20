/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.infrastructure.adapters

import com.zama.safeops.modules.analytics.application.ports.AnalyticsPort
import com.zama.safeops.modules.analytics.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate

/**
 * Stub implementation of AnalyticsPort.
 * Returns empty/zero results until a real implementation is configured.
 */
@Component
class StubAnalyticsAdapter : AnalyticsPort {

    override fun getInspectionsCount(tenantId: TenantId, from: LocalDate, to: LocalDate): Int = 0
    override fun getInspectionsTrend(tenantId: TenantId, days: Int): Trend = Trend(TrendDirection.STABLE, 0.0, 0.0)
    override fun getOpenHazardsCount(tenantId: TenantId): Int = 0
    override fun getCriticalHazardsCount(tenantId: TenantId): Int = 0
    override fun getComplianceRate(tenantId: TenantId, from: LocalDate, to: LocalDate): Double = 0.0
    override fun getComplianceTrend(tenantId: TenantId, months: Int): List<ComplianceTrendPoint> = emptyList()
    override fun getActiveIncidentsCount(tenantId: TenantId): Int = 0
    override fun getPersonnelInField(tenantId: TenantId): Int = 0
    override fun getRecentActivity(tenantId: TenantId, limit: Int): List<ActivityItem> = emptyList()
    override fun getUpcomingDeadlines(tenantId: TenantId, days: Int): List<DeadlineItem> = emptyList()

    override fun getTimeSeriesData(
        tenantId: TenantId,
        metric: String,
        granularity: Granularity,
        from: Instant,
        to: Instant
    ): List<DataPoint> = emptyList()

    override fun getRiskHeatMap(
        tenantId: TenantId,
        dimension: HeatMapDimension,
        from: LocalDate,
        to: LocalDate
    ): RiskHeatMap = RiskHeatMap(dimension, emptyList())

    override fun executeDrillDown(tenantId: TenantId, query: DrillDownQuery): DrillDownResult =
        DrillDownResult(emptyList(), emptyList(), 0, emptyMap())

    override fun saveReportDefinition(definition: ReportDefinition): ReportDefinition = definition
    override fun findReportDefinition(id: String): ReportDefinition? = null
    override fun findReportDefinitions(tenantId: TenantId): List<ReportDefinition> = emptyList()
    override fun saveGeneratedReport(report: GeneratedReport): GeneratedReport = report
    override fun findGeneratedReports(tenantId: TenantId, definitionId: String?, limit: Int): List<GeneratedReport> = emptyList()
    override fun findJobById(jobId: String): SyncJob? = null
    override fun findAllScheduledReports(): List<ReportDefinition> = emptyList()

    override fun saveDashboardLayout(layout: DashboardLayout): DashboardLayout = layout
    override fun findDashboardLayout(id: String): DashboardLayout? = null
    override fun findDefaultDashboard(tenantId: TenantId, userId: Long): DashboardLayout? = null
    override fun clearDefaultDashboard(tenantId: TenantId, userId: Long) {}
    override fun findDashboardLayouts(tenantId: TenantId, userId: Long): List<DashboardLayout> = emptyList()

    override fun exportRawData(
        tenantId: TenantId,
        entityType: String,
        from: LocalDate,
        to: LocalDate
    ): List<Map<String, Any>> = emptyList()

    override fun getTotalHoursWorked(tenantId: TenantId, period: DateRange): Long = 0
    override fun getLostTimeInjuries(tenantId: TenantId, period: DateRange): Int = 0
    override fun getRecordableCases(tenantId: TenantId, period: DateRange): Int = 0
    override fun getNearMisses(tenantId: TenantId, period: DateRange): Int = 0
    override fun getMedicalTreatmentCases(tenantId: TenantId, period: DateRange): Int = 0
    override fun getFirstAidCases(tenantId: TenantId, period: DateRange): Int = 0
    override fun getPropertyDamageIncidents(tenantId: TenantId, period: DateRange): Int = 0
    override fun getSeverityRate(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getInspectionCompletionRate(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getHazardResolutionRate(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getAverageHazardResolutionTime(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getTrainingCompletionRate(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getPPEComplianceRate(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getIncidentsInPeriod(tenantId: TenantId, period: DateRange): List<Any> = emptyList()

    override fun getInspectionsCompleted(tenantId: TenantId, period: DateRange): Int = 0
    override fun getInspectionsScheduled(tenantId: TenantId, period: DateRange): Int = 0
    override fun getAverageInspectionScore(tenantId: TenantId, period: DateRange): Double = 0.0
    override fun getHazardsIdentified(tenantId: TenantId, period: DateRange): Int = 0
    override fun getHazardsResolved(tenantId: TenantId, period: DateRange): Int = 0
    override fun getHazardsOutstanding(tenantId: TenantId): Int = 0
    override fun getCriticalHazardsOutstanding(tenantId: TenantId): Int = 0
    override fun getSafetyMeetingsConducted(tenantId: TenantId, period: DateRange): Int = 0
    override fun getTrainingSessionsCompleted(tenantId: TenantId, period: DateRange): Int = 0
    override fun getActiveWorkforce(tenantId: TenantId): Int = 0
}
