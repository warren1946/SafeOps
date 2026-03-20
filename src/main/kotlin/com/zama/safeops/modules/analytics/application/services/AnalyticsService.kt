/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.application.services

import com.zama.safeops.modules.analytics.application.ports.AnalyticsPort
import com.zama.safeops.modules.analytics.application.ports.MLPredictionService
import com.zama.safeops.modules.analytics.application.ports.ReportGenerator
import com.zama.safeops.modules.analytics.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Service for generating analytics, reports, and business intelligence.
 */
@Service
class AnalyticsService(
    private val analyticsPort: AnalyticsPort,
    private val reportGenerator: ReportGenerator,
    private val mlPredictionService: MLPredictionService
) {

    /**
     * Get executive summary dashboard.
     */
    fun getExecutiveSummary(tenantId: TenantId, period: DateRange): ExecutiveSummary {
        val safetyKPIs = calculateSafetyKPIs(tenantId, period)
        val financialImpact = calculateFinancialImpact(tenantId, period)
        val operationalMetrics = calculateOperationalMetrics(tenantId, period)
        val insights = generatePredictiveInsights(tenantId)
        val benchmark = getIndustryBenchmark(tenantId, safetyKPIs)
        val recommendations = generateRecommendations(tenantId, safetyKPIs, benchmark)

        return ExecutiveSummary(
            tenantId = tenantId,
            generatedAt = Instant.now(),
            period = period,
            safetyKPIs = safetyKPIs,
            financialImpact = financialImpact,
            operationalMetrics = operationalMetrics,
            predictiveInsights = insights,
            industryBenchmark = benchmark,
            recommendations = recommendations
        )
    }

    /**
     * Get real-time dashboard metrics.
     */
    fun getDashboardMetrics(tenantId: TenantId): DashboardMetrics {
        val today = LocalDate.now()
        val thisMonth = YearMonth.now()

        return DashboardMetrics(
            todayInspections = analyticsPort.getInspectionsCount(tenantId, today, today),
            inspectionsTrend = analyticsPort.getInspectionsTrend(tenantId, 7),
            openHazards = analyticsPort.getOpenHazardsCount(tenantId),
            criticalHazards = analyticsPort.getCriticalHazardsCount(tenantId),
            complianceRate = analyticsPort.getComplianceRate(tenantId, thisMonth.atDay(1), today),
            complianceTrend = analyticsPort.getComplianceTrend(tenantId, 6),
            activeIncidents = analyticsPort.getActiveIncidentsCount(tenantId),
            personnelInField = analyticsPort.getPersonnelInField(tenantId),
            recentActivity = analyticsPort.getRecentActivity(tenantId, 10),
            upcomingDeadlines = analyticsPort.getUpcomingDeadlines(tenantId, 7)
        )
    }

    /**
     * Get time series data for charts.
     */
    fun getTimeSeriesData(
        tenantId: TenantId,
        metric: String,
        granularity: Granularity,
        from: Instant,
        to: Instant
    ): TimeSeriesData {
        val dataPoints = analyticsPort.getTimeSeriesData(tenantId, metric, granularity, from, to)

        return TimeSeriesData(
            metric = metric,
            granularity = granularity,
            dataPoints = dataPoints
        )
    }

    /**
     * Get risk heat map.
     */
    fun getRiskHeatMap(
        tenantId: TenantId,
        dimension: HeatMapDimension,
        from: LocalDate,
        to: LocalDate
    ): RiskHeatMap {
        return analyticsPort.getRiskHeatMap(tenantId, dimension, from, to)
    }

    /**
     * Drill-down query for interactive exploration.
     */
    fun drillDown(tenantId: TenantId, query: DrillDownQuery): DrillDownResult {
        return analyticsPort.executeDrillDown(tenantId, query)
    }

    /**
     * Create a report definition.
     */
    fun createReportDefinition(
        tenantId: TenantId,
        name: String,
        type: ReportType,
        format: ReportFormat,
        sections: List<ReportSection>,
        schedule: ReportSchedule?,
        recipients: List<String>
    ): ReportDefinition {
        val definition = ReportDefinition(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            name = name,
            description = "",
            type = type,
            format = format,
            sections = sections,
            schedule = schedule,
            recipients = recipients
        )

        return analyticsPort.saveReportDefinition(definition)
    }

    /**
     * Generate a report on-demand.
     */
    fun generateReport(
        definitionId: String,
        period: DateRange
    ): GeneratedReport {
        val definition = analyticsPort.findReportDefinition(definitionId)
            ?: throw IllegalArgumentException("Report definition not found: $definitionId")

        val report = GeneratedReport(
            id = UUID.randomUUID().toString(),
            definitionId = definitionId,
            tenantId = definition.tenantId,
            generatedAt = Instant.now(),
            period = period,
            format = definition.format,
            fileUrl = "",  // Will be set after generation
            fileSize = 0,
            status = GenerationStatus.PENDING,
            errorMessage = null
        )

        val saved = analyticsPort.saveGeneratedReport(report)

        // Generate asynchronously
        generateReportAsync(definition, saved, period)

        return saved
    }

    /**
     * Get list of available reports.
     */
    fun getReportDefinitions(tenantId: TenantId): List<ReportDefinition> {
        return analyticsPort.findReportDefinitions(tenantId)
    }

    /**
     * Get generated reports.
     */
    fun getGeneratedReports(
        tenantId: TenantId,
        definitionId: String? = null,
        limit: Int = 20
    ): List<GeneratedReport> {
        return analyticsPort.findGeneratedReports(tenantId, definitionId, limit)
    }

    /**
     * Save dashboard layout.
     */
    fun saveDashboardLayout(
        tenantId: TenantId,
        userId: Long,
        name: String,
        widgets: List<DashboardWidget>,
        isDefault: Boolean = false
    ): DashboardLayout {
        if (isDefault) {
            // Clear existing default
            analyticsPort.clearDefaultDashboard(tenantId, userId)
        }

        val layout = DashboardLayout(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            userId = userId,
            name = name,
            isDefault = isDefault,
            widgets = widgets,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        return analyticsPort.saveDashboardLayout(layout)
    }

    /**
     * Get dashboard layout.
     */
    fun getDashboardLayout(
        tenantId: TenantId,
        userId: Long,
        layoutId: String? = null
    ): DashboardLayout? {
        return if (layoutId != null) {
            analyticsPort.findDashboardLayout(layoutId)
        } else {
            analyticsPort.findDefaultDashboard(tenantId, userId)
        }
    }

    /**
     * Get all dashboard layouts for user.
     */
    fun getDashboardLayouts(tenantId: TenantId, userId: Long): List<DashboardLayout> {
        return analyticsPort.findDashboardLayouts(tenantId, userId)
    }

    /**
     * Export data for external analysis.
     */
    fun exportData(
        tenantId: TenantId,
        entityType: String,
        from: LocalDate,
        to: LocalDate,
        format: ExportFormat
    ): DataExport {
        val data = analyticsPort.exportRawData(tenantId, entityType, from, to)

        return DataExport(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            entityType = entityType,
            period = DateRange(from, to),
            format = format,
            recordCount = data.size,
            fileUrl = generateExportFile(data, format),
            generatedAt = Instant.now()
        )
    }

    /**
     * Scheduled report generation.
     */
    @Scheduled(cron = "0 0 6 * * ?") // Daily at 6 AM
    fun generateScheduledReports() {
        val now = LocalDateTime.now()

        analyticsPort.findAllScheduledReports().forEach { definition ->
            val schedule = definition.schedule ?: return@forEach

            val shouldGenerate = when (schedule.frequency) {
                ScheduleFrequency.DAILY -> true
                ScheduleFrequency.WEEKLY -> now.dayOfWeek.value == schedule.dayOfWeek
                ScheduleFrequency.MONTHLY -> now.dayOfMonth == schedule.dayOfMonth
                ScheduleFrequency.QUARTERLY -> {
                    now.dayOfMonth == 1 && now.monthValue in listOf(1, 4, 7, 10)
                }
            }

            if (shouldGenerate && now.toLocalTime().toString() == schedule.time) {
                try {
                    val period = when (schedule.frequency) {
                        ScheduleFrequency.DAILY -> DateRange(
                            LocalDate.now().minusDays(1),
                            LocalDate.now().minusDays(1)
                        )

                        ScheduleFrequency.WEEKLY -> DateRange(
                            LocalDate.now().minusWeeks(1),
                            LocalDate.now().minusDays(1)
                        )

                        ScheduleFrequency.MONTHLY -> DateRange(
                            LocalDate.now().minusMonths(1).withDayOfMonth(1),
                            LocalDate.now().minusMonths(1).withDayOfMonth(
                                LocalDate.now().minusMonths(1).lengthOfMonth()
                            )
                        )

                        ScheduleFrequency.QUARTERLY -> DateRange(
                            LocalDate.now().minusMonths(3).withDayOfMonth(1),
                            LocalDate.now().withDayOfMonth(1).minusDays(1)
                        )
                    }

                    generateReport(definition.id, period)
                } catch (e: Exception) {
                    // Log error, continue with other reports
                }
            }
        }
    }

    /**
     * Calculate safety KPIs.
     */
    private fun calculateSafetyKPIs(tenantId: TenantId, period: DateRange): SafetyKPIs {
        val hoursWorked = analyticsPort.getTotalHoursWorked(tenantId, period)

        val lostTimeInjuries = analyticsPort.getLostTimeInjuries(tenantId, period)
        val recordableCases = analyticsPort.getRecordableCases(tenantId, period)
        val nearMisses = analyticsPort.getNearMisses(tenantId, period)

        // Calculate rates per 1,000,000 hours
        val ltifr = if (hoursWorked > 0) (lostTimeInjuries * 1_000_000.0) / hoursWorked else 0.0
        val trifr = if (hoursWorked > 0) (recordableCases * 1_000_000.0) / hoursWorked else 0.0

        // Get previous period for trend
        val previousPeriod = getPreviousPeriod(period)
        val previousLTIFR = calculateLTIFR(tenantId, previousPeriod)

        val ltifrTrend = calculateTrend(ltifr, previousLTIFR)

        return SafetyKPIs(
            period = period,
            ltifr = round(ltifr),
            trifr = round(trifr),
            severityRate = analyticsPort.getSeverityRate(tenantId, period),
            nearMissFrequencyRate = if (hoursWorked > 0) (nearMisses * 1_000_000.0) / hoursWorked else 0.0,
            lostTimeInjuries = lostTimeInjuries,
            medicalTreatmentCases = analyticsPort.getMedicalTreatmentCases(tenantId, period),
            firstAidCases = analyticsPort.getFirstAidCases(tenantId, period),
            nearMisses = nearMisses,
            propertyDamageIncidents = analyticsPort.getPropertyDamageIncidents(tenantId, period),
            ltifrTrend = ltifrTrend,
            trifrTrend = Trend(TrendDirection.STABLE, 0.0, 0.0), // Placeholder
            inspectionCompletionRate = analyticsPort.getInspectionCompletionRate(tenantId, period),
            hazardResolutionRate = analyticsPort.getHazardResolutionRate(tenantId, period),
            averageHazardResolutionTimeDays = analyticsPort.getAverageHazardResolutionTime(tenantId, period),
            trainingCompletionRate = analyticsPort.getTrainingCompletionRate(tenantId, period),
            ppeComplianceRate = analyticsPort.getPPEComplianceRate(tenantId, period)
        )
    }

    /**
     * Calculate financial impact.
     */
    private fun calculateFinancialImpact(tenantId: TenantId, period: DateRange): FinancialImpact {
        val incidents = analyticsPort.getIncidentsInPeriod(tenantId, period)

        var totalCost = BigDecimal.ZERO
        var directCosts = DirectCosts(
            medicalExpenses = BigDecimal.ZERO,
            compensationPayments = BigDecimal.ZERO,
            legalFees = BigDecimal.ZERO,
            equipmentDamage = BigDecimal.ZERO,
            productionDowntime = BigDecimal.ZERO
        )

        incidents.forEach { incident ->
            val cost = calculateIncidentCost(incident)
            totalCost = totalCost.add(cost.total)
            directCosts = directCosts.copy(
                medicalExpenses = directCosts.medicalExpenses.add(cost.medical),
                compensationPayments = directCosts.compensationPayments.add(cost.compensation),
                legalFees = directCosts.legalFees.add(cost.legal),
                equipmentDamage = directCosts.equipmentDamage.add(cost.equipment),
                productionDowntime = directCosts.productionDowntime.add(cost.downtime)
            )
        }

        val indirectMultiplier = BigDecimal("1.5") // Industry standard: indirect = 1.5x direct
        val indirectCosts = IndirectCosts(
            investigationTime = directCosts.medicalExpenses.multiply(BigDecimal("0.1")),
            trainingCosts = BigDecimal.ZERO,
            replacementLabor = directCosts.compensationPayments.multiply(BigDecimal("0.2")),
            administrativeCosts = totalCost.multiply(BigDecimal("0.1")),
            reputationImpact = totalCost.multiply(BigDecimal("0.2"))
        )

        return FinancialImpact(
            period = period,
            totalCostOfIncidents = totalCost,
            directCosts = directCosts,
            indirectCosts = indirectCosts,
            avoidedCosts = calculateAvoidedCosts(tenantId, period),
            roiOnSafetyInvestments = calculateSafetyROI(tenantId, period),
            costPerRecordableIncident = if (incidents.isNotEmpty())
                totalCost.divide(BigDecimal(incidents.size), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO,
            costPerLostTimeInjury = calculateCostPerLTI(tenantId, period, totalCost)
        )
    }

    /**
     * Calculate operational metrics.
     */
    private fun calculateOperationalMetrics(tenantId: TenantId, period: DateRange): OperationalMetrics {
        return OperationalMetrics(
            inspectionsCompleted = analyticsPort.getInspectionsCompleted(tenantId, period),
            inspectionsScheduled = analyticsPort.getInspectionsScheduled(tenantId, period),
            inspectionCompletionRate = analyticsPort.getInspectionCompletionRate(tenantId, period),
            averageInspectionScore = analyticsPort.getAverageInspectionScore(tenantId, period),
            hazardsIdentified = analyticsPort.getHazardsIdentified(tenantId, period),
            hazardsResolved = analyticsPort.getHazardsResolved(tenantId, period),
            hazardsOutstanding = analyticsPort.getHazardsOutstanding(tenantId),
            criticalHazardsOutstanding = analyticsPort.getCriticalHazardsOutstanding(tenantId),
            safetyMeetingsConducted = analyticsPort.getSafetyMeetingsConducted(tenantId, period),
            trainingSessionsCompleted = analyticsPort.getTrainingSessionsCompleted(tenantId, period),
            activeWorkforce = analyticsPort.getActiveWorkforce(tenantId),
            totalHoursWorked = analyticsPort.getTotalHoursWorked(tenantId, period)
        )
    }

    /**
     * Generate predictive insights using ML.
     */
    private fun generatePredictiveInsights(tenantId: TenantId): List<PredictiveInsight> {
        return mlPredictionService.generateInsights(tenantId)
    }

    /**
     * Get industry benchmark data.
     */
    private fun getIndustryBenchmark(tenantId: TenantId, kpis: SafetyKPIs): IndustryBenchmark {
        // In production, this would fetch from industry database/API
        return IndustryBenchmark(
            industry = "Mining - Underground",
            companySize = "Large",
            region = "Africa",
            ourLTIFR = kpis.ltifr,
            ourTRIFR = kpis.trifr,
            industryAverageLTIFR = 2.5,
            industryBestQuartileLTIFR = 0.5,
            industryWorstQuartileLTIFR = 5.0,
            ltifrPercentile = calculatePercentile(kpis.ltifr, 2.5, 0.5, 5.0),
            trifrPercentile = 50, // Placeholder
            gapToBestInClass = kpis.ltifr - 0.5,
            gapToIndustryAverage = kpis.ltifr - 2.5
        )
    }

    /**
     * Generate strategic recommendations.
     */
    private fun generateRecommendations(
        tenantId: TenantId,
        kpis: SafetyKPIs,
        benchmark: IndustryBenchmark
    ): List<StrategicRecommendation> {
        val recommendations = mutableListOf<StrategicRecommendation>()

        // Recommendation based on LTIFR gap
        if (kpis.ltifr > benchmark.industryAverageLTIFR) {
            recommendations.add(
                StrategicRecommendation(
                    id = UUID.randomUUID().toString(),
                    priority = RecommendationPriority.CRITICAL,
                    category = RecommendationCategory.TRAINING,
                    title = "Implement Behavior-Based Safety Program",
                    description = "Current LTIFR is above industry average. Implement comprehensive BBS program focusing on at-risk behaviors.",
                    expectedImpact = ExpectedImpact(
                        ltifrReduction = 30.0,
                        trifrReduction = 20.0,
                        costSavings = BigDecimal("500000"),
                        complianceImprovement = true
                    ),
                    implementationSteps = listOf(
                        "Conduct behavior observation survey",
                        "Train observers and supervisors",
                        "Implement observation cards system",
                        "Weekly feedback sessions",
                        "Monthly trend analysis"
                    ),
                    estimatedCost = BigDecimal("75000"),
                    estimatedTimeline = "6 months"
                )
            )
        }

        // Recommendation based on hazard resolution time
        if (kpis.averageHazardResolutionTimeDays > 14) {
            recommendations.add(
                StrategicRecommendation(
                    id = UUID.randomUUID().toString(),
                    priority = RecommendationPriority.HIGH,
                    category = RecommendationCategory.PROCEDURE,
                    title = "Streamline Hazard Resolution Process",
                    description = "Average hazard resolution time exceeds 14 days. Implement priority-based escalation and automated reminders.",
                    expectedImpact = ExpectedImpact(
                        ltifrReduction = 15.0,
                        trifrReduction = 10.0,
                        costSavings = BigDecimal("200000"),
                        complianceImprovement = true
                    ),
                    implementationSteps = listOf(
                        "Map current hazard resolution workflow",
                        "Identify bottlenecks",
                        "Implement digital workflow automation",
                        "Set up automated escalation rules"
                    ),
                    estimatedCost = BigDecimal("30000"),
                    estimatedTimeline = "3 months"
                )
            )
        }

        return recommendations
    }

    private fun generateReportAsync(
        definition: ReportDefinition,
        report: GeneratedReport,
        period: DateRange
    ) {
        // Implementation would be async
    }

    private fun generateExportFile(data: List<Map<String, Any>>, format: ExportFormat): String {
        // Implementation
        return ""
    }

    // Helper methods
    private fun getPreviousPeriod(period: DateRange): DateRange {
        val days = ChronoUnit.DAYS.between(period.start, period.end)
        return DateRange(
            period.start.minusDays(days),
            period.start.minusDays(1)
        )
    }

    private fun calculateLTIFR(tenantId: TenantId, period: DateRange): Double {
        val hours = analyticsPort.getTotalHoursWorked(tenantId, period)
        val lti = analyticsPort.getLostTimeInjuries(tenantId, period)
        return if (hours > 0) (lti * 1_000_000.0) / hours else 0.0
    }

    private fun calculateTrend(current: Double, previous: Double): Trend {
        if (previous == 0.0) return Trend(TrendDirection.STABLE, 0.0, previous)

        val change = ((current - previous) / previous) * 100
        return when {
            change < -5 -> Trend(TrendDirection.IMPROVING, kotlin.math.abs(change), previous)
            change > 5 -> Trend(TrendDirection.WORSENING, kotlin.math.abs(change), previous)
            else -> Trend(TrendDirection.STABLE, kotlin.math.abs(change), previous)
        }
    }

    private fun calculatePercentile(value: Double, avg: Double, best: Double, worst: Double): Int {
        if (value <= best) return 90
        if (value >= worst) return 10
        val range = worst - best
        val position = worst - value
        return (10 + (position / range) * 80).toInt()
    }

    private fun round(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    data class IncidentCost(
        val total: BigDecimal,
        val medical: BigDecimal,
        val compensation: BigDecimal,
        val legal: BigDecimal,
        val equipment: BigDecimal,
        val downtime: BigDecimal
    )

    private fun calculateIncidentCost(incident: Any): IncidentCost {
        // Placeholder - would calculate based on incident severity and type
        return IncidentCost(
            total = BigDecimal("10000"),
            medical = BigDecimal("3000"),
            compensation = BigDecimal("5000"),
            legal = BigDecimal("1000"),
            equipment = BigDecimal("500"),
            downtime = BigDecimal("500")
        )
    }

    private fun calculateAvoidedCosts(tenantId: TenantId, period: DateRange): BigDecimal {
        // Calculate based on hazards prevented, near misses, etc.
        return BigDecimal("150000")
    }

    private fun calculateSafetyROI(tenantId: TenantId, period: DateRange): Double {
        // ROI = (Benefits - Costs) / Costs * 100
        return 350.0 // 350% ROI
    }

    private fun calculateCostPerLTI(tenantId: TenantId, period: DateRange, totalCost: BigDecimal): BigDecimal {
        val lti = analyticsPort.getLostTimeInjuries(tenantId, period)
        return if (lti > 0) totalCost.divide(BigDecimal(lti), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO
    }
}

// DTOs for service responses
data class DashboardMetrics(
    val todayInspections: Int,
    val inspectionsTrend: Trend,
    val openHazards: Int,
    val criticalHazards: Int,
    val complianceRate: Double,
    val complianceTrend: List<com.zama.safeops.modules.analytics.domain.model.ComplianceTrendPoint>,
    val activeIncidents: Int,
    val personnelInField: Int,
    val recentActivity: List<com.zama.safeops.modules.analytics.domain.model.ActivityItem>,
    val upcomingDeadlines: List<com.zama.safeops.modules.analytics.domain.model.DeadlineItem>
)

data class DataExport(
    val id: String,
    val tenantId: TenantId,
    val entityType: String,
    val period: DateRange,
    val format: ExportFormat,
    val recordCount: Int,
    val fileUrl: String,
    val generatedAt: Instant
)

enum class ExportFormat {
    CSV,
    EXCEL,
    JSON,
    PARQUET
}
