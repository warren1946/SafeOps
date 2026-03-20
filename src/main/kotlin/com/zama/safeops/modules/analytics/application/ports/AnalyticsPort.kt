/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.application.ports

import com.zama.safeops.modules.analytics.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant
import java.time.LocalDate

/**
 * Port for analytics data access.
 */
interface AnalyticsPort {

    // Dashboard metrics
    fun getInspectionsCount(tenantId: TenantId, from: LocalDate, to: LocalDate): Int
    fun getInspectionsTrend(tenantId: TenantId, days: Int): Trend
    fun getOpenHazardsCount(tenantId: TenantId): Int
    fun getCriticalHazardsCount(tenantId: TenantId): Int
    fun getComplianceRate(tenantId: TenantId, from: LocalDate, to: LocalDate): Double
    fun getComplianceTrend(tenantId: TenantId, months: Int): List<ComplianceTrendPoint>
    fun getActiveIncidentsCount(tenantId: TenantId): Int
    fun getPersonnelInField(tenantId: TenantId): Int
    fun getRecentActivity(tenantId: TenantId, limit: Int): List<ActivityItem>
    fun getUpcomingDeadlines(tenantId: TenantId, days: Int): List<DeadlineItem>

    // Time series
    fun getTimeSeriesData(
        tenantId: TenantId,
        metric: String,
        granularity: Granularity,
        from: Instant,
        to: Instant
    ): List<DataPoint>

    // Heat maps
    fun getRiskHeatMap(
        tenantId: TenantId,
        dimension: HeatMapDimension,
        from: LocalDate,
        to: LocalDate
    ): RiskHeatMap

    // Drill down
    fun executeDrillDown(tenantId: TenantId, query: DrillDownQuery): DrillDownResult

    // Reports
    fun saveReportDefinition(definition: ReportDefinition): ReportDefinition
    fun findReportDefinition(id: String): ReportDefinition?
    fun findReportDefinitions(tenantId: TenantId): List<ReportDefinition>
    fun saveGeneratedReport(report: GeneratedReport): GeneratedReport
    fun findGeneratedReports(tenantId: TenantId, definitionId: String?, limit: Int): List<GeneratedReport>
    fun findJobById(jobId: String): SyncJob?
    fun findAllScheduledReports(): List<ReportDefinition>

    // Dashboards
    fun saveDashboardLayout(layout: DashboardLayout): DashboardLayout
    fun findDashboardLayout(id: String): DashboardLayout?
    fun findDefaultDashboard(tenantId: TenantId, userId: Long): DashboardLayout?
    fun clearDefaultDashboard(tenantId: TenantId, userId: Long)
    fun findDashboardLayouts(tenantId: TenantId, userId: Long): List<DashboardLayout>

    // Export
    fun exportRawData(
        tenantId: TenantId,
        entityType: String,
        from: LocalDate,
        to: LocalDate
    ): List<Map<String, Any>>

    // Safety KPIs
    fun getTotalHoursWorked(tenantId: TenantId, period: DateRange): Long
    fun getLostTimeInjuries(tenantId: TenantId, period: DateRange): Int
    fun getRecordableCases(tenantId: TenantId, period: DateRange): Int
    fun getNearMisses(tenantId: TenantId, period: DateRange): Int
    fun getMedicalTreatmentCases(tenantId: TenantId, period: DateRange): Int
    fun getFirstAidCases(tenantId: TenantId, period: DateRange): Int
    fun getPropertyDamageIncidents(tenantId: TenantId, period: DateRange): Int
    fun getSeverityRate(tenantId: TenantId, period: DateRange): Double
    fun getInspectionCompletionRate(tenantId: TenantId, period: DateRange): Double
    fun getHazardResolutionRate(tenantId: TenantId, period: DateRange): Double
    fun getAverageHazardResolutionTime(tenantId: TenantId, period: DateRange): Double
    fun getTrainingCompletionRate(tenantId: TenantId, period: DateRange): Double
    fun getPPEComplianceRate(tenantId: TenantId, period: DateRange): Double
    fun getIncidentsInPeriod(tenantId: TenantId, period: DateRange): List<Any>

    // Operational metrics
    fun getInspectionsCompleted(tenantId: TenantId, period: DateRange): Int
    fun getInspectionsScheduled(tenantId: TenantId, period: DateRange): Int
    fun getAverageInspectionScore(tenantId: TenantId, period: DateRange): Double
    fun getHazardsIdentified(tenantId: TenantId, period: DateRange): Int
    fun getHazardsResolved(tenantId: TenantId, period: DateRange): Int
    fun getHazardsOutstanding(tenantId: TenantId): Int
    fun getCriticalHazardsOutstanding(tenantId: TenantId): Int
    fun getSafetyMeetingsConducted(tenantId: TenantId, period: DateRange): Int
    fun getTrainingSessionsCompleted(tenantId: TenantId, period: DateRange): Int
    fun getActiveWorkforce(tenantId: TenantId): Int
}

interface ReportGenerator {
    fun generateReport(definition: ReportDefinition, period: DateRange, format: ReportFormat): GeneratedReport
}

interface MLPredictionService {
    fun generateInsights(tenantId: TenantId): List<PredictiveInsight>
}
