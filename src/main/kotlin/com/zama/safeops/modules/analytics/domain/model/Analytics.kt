/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Executive dashboard summary.
 */
data class ExecutiveSummary(
    val tenantId: TenantId,
    val generatedAt: Instant,
    val period: DateRange,
    val safetyKPIs: SafetyKPIs,
    val financialImpact: FinancialImpact,
    val operationalMetrics: OperationalMetrics,
    val predictiveInsights: List<PredictiveInsight>,
    val industryBenchmark: IndustryBenchmark,
    val recommendations: List<StrategicRecommendation>
)

data class DateRange(
    val start: LocalDate,
    val end: LocalDate
)

/**
 * Key Safety Performance Indicators.
 */
data class SafetyKPIs(
    val period: DateRange,

    // Frequency Rates (per 1,000,000 hours worked)
    val ltifr: Double,              // Lost Time Injury Frequency Rate
    val trifr: Double,              // Total Recordable Injury Frequency Rate
    val severityRate: Double,       // Injury severity rate
    val nearMissFrequencyRate: Double,

    // Counts
    val lostTimeInjuries: Int,
    val medicalTreatmentCases: Int,
    val firstAidCases: Int,
    val nearMisses: Int,
    val propertyDamageIncidents: Int,

    // Trends (vs previous period)
    val ltifrTrend: Trend,
    val trifrTrend: Trend,

    // Leading Indicators
    val inspectionCompletionRate: Double,
    val hazardResolutionRate: Double,
    val averageHazardResolutionTimeDays: Double,
    val trainingCompletionRate: Double,
    val ppeComplianceRate: Double
)

data class Trend(
    val direction: TrendDirection,
    val percentage: Double,
    val previousPeriodValue: Double
)

data class ComplianceTrendPoint(
    val month: String,
    val rate: Double
)

data class ActivityItem(
    val timestamp: Instant,
    val type: String,
    val description: String,
    val userId: Long,
    val userName: String
)

data class DeadlineItem(
    val dueDate: LocalDate,
    val type: String,
    val description: String,
    val daysRemaining: Int
)

enum class TrendDirection {
    IMPROVING,      // For rates (lower is better)
    WORSENING,      // For rates (higher is worse)
    STABLE
}

/**
 * Financial impact analysis.
 */
data class FinancialImpact(
    val period: DateRange,
    val totalCostOfIncidents: BigDecimal,
    val directCosts: DirectCosts,
    val indirectCosts: IndirectCosts,
    val avoidedCosts: BigDecimal,  // Estimated savings from prevented incidents
    val roiOnSafetyInvestments: Double,
    val costPerRecordableIncident: BigDecimal,
    val costPerLostTimeInjury: BigDecimal
)

data class DirectCosts(
    val medicalExpenses: BigDecimal,
    val compensationPayments: BigDecimal,
    val legalFees: BigDecimal,
    val equipmentDamage: BigDecimal,
    val productionDowntime: BigDecimal
)

data class IndirectCosts(
    val investigationTime: BigDecimal,
    val trainingCosts: BigDecimal,
    val replacementLabor: BigDecimal,
    val administrativeCosts: BigDecimal,
    val reputationImpact: BigDecimal  // Estimated
)

/**
 * Operational performance metrics.
 */
data class OperationalMetrics(
    val inspectionsCompleted: Int,
    val inspectionsScheduled: Int,
    val inspectionCompletionRate: Double,
    val averageInspectionScore: Double,
    val hazardsIdentified: Int,
    val hazardsResolved: Int,
    val hazardsOutstanding: Int,
    val criticalHazardsOutstanding: Int,
    val safetyMeetingsConducted: Int,
    val trainingSessionsCompleted: Int,
    val activeWorkforce: Int,
    val totalHoursWorked: Long
)

/**
 * Predictive insights from ML models.
 */
data class PredictiveInsight(
    val id: String,
    val type: InsightType,
    val severity: InsightSeverity,
    val title: String,
    val description: String,
    val confidence: Double,  // 0-1
    val predictedOutcome: String,
    val recommendedActions: List<String>,
    val timeframe: String,  // e.g., "Next 30 days"
    val relatedData: Map<String, Any>
)

enum class InsightType {
    HIGH_RISK_LOCATION,
    EQUIPMENT_FAILURE_RISK,
    BEHAVIORAL_PATTERN,
    SEASONAL_RISK,
    COMPLIANCE_RISK,
    TREND_ANOMALY
}

enum class InsightSeverity {
    INFO,
    WARNING,
    CRITICAL
}

/**
 * Industry benchmark comparison.
 */
data class IndustryBenchmark(
    val industry: String,  // Mining - Underground Coal, Mining - Open Pit, etc.
    val companySize: String,  // Small, Medium, Large
    val region: String,

    // Our performance
    val ourLTIFR: Double,
    val ourTRIFR: Double,

    // Industry averages
    val industryAverageLTIFR: Double,
    val industryBestQuartileLTIFR: Double,
    val industryWorstQuartileLTIFR: Double,

    // Comparison
    val ltifrPercentile: Int,  // 0-100
    val trifrPercentile: Int,

    // Gap analysis
    val gapToBestInClass: Double,
    val gapToIndustryAverage: Double
)

/**
 * Strategic recommendations.
 */
data class StrategicRecommendation(
    val id: String,
    val priority: RecommendationPriority,
    val category: RecommendationCategory,
    val title: String,
    val description: String,
    val expectedImpact: ExpectedImpact,
    val implementationSteps: List<String>,
    val estimatedCost: BigDecimal?,
    val estimatedTimeline: String
)

enum class RecommendationPriority {
    CRITICAL,  // Immediate action required
    HIGH,      // Implement within 30 days
    MEDIUM,    // Implement within 90 days
    LOW        // Consider for next planning cycle
}

enum class RecommendationCategory {
    TRAINING,
    PROCEDURE,
    EQUIPMENT,
    STAFFING,
    TECHNOLOGY,
    CULTURE
}

data class ExpectedImpact(
    val ltifrReduction: Double?,  // Percentage
    val trifrReduction: Double?,
    val costSavings: BigDecimal?,
    val complianceImprovement: Boolean
)

/**
 * Time-series data for charts.
 */
data class TimeSeriesData(
    val metric: String,
    val granularity: Granularity,
    val dataPoints: List<DataPoint>
)

data class DataPoint(
    val timestamp: Instant,
    val value: Double,
    val label: String?  // e.g., "Jan 2024"
)

enum class Granularity {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}

/**
 * Heat map data for risk visualization.
 */
data class RiskHeatMap(
    val dimension: HeatMapDimension,
    val cells: List<HeatMapCell>
)

enum class HeatMapDimension {
    LOCATION_TIME,      // Location vs Time
    SHIFT_TYPE,         // Shift vs Incident Type
    DEPARTMENT_TIME     // Department vs Time
}

data class HeatMapCell(
    val rowLabel: String,
    val columnLabel: String,
    val value: Double,  // Risk score 0-100
    val incidentCount: Int
)

/**
 * Report definition and generation.
 */
data class ReportDefinition(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val description: String,
    val type: ReportType,
    val format: ReportFormat,
    val sections: List<ReportSection>,
    val schedule: ReportSchedule?,
    val recipients: List<String>,
    val isActive: Boolean = true
)

enum class ReportType {
    EXECUTIVE_SUMMARY,
    OPERATIONAL_DASHBOARD,
    INCIDENT_ANALYSIS,
    COMPLIANCE_REPORT,
    FINANCIAL_IMPACT,
    BENCHMARK_COMPARISON,
    CUSTOM
}

enum class ReportFormat {
    PDF,
    EXCEL,
    POWERPOINT,
    CSV,
    JSON
}

data class ReportSection(
    val type: SectionType,
    val title: String,
    val configuration: Map<String, Any>
)

enum class SectionType {
    KPI_SUMMARY,
    TIME_SERIES_CHART,
    HEAT_MAP,
    PIE_CHART,
    BAR_CHART,
    DATA_TABLE,
    TEXT_BLOCK,
    INSIGHTS_LIST
}

data class ReportSchedule(
    val frequency: ScheduleFrequency,
    val dayOfWeek: Int?,  // 1-7 for weekly
    val dayOfMonth: Int?, // 1-31 for monthly
    val time: String,     // HH:mm
    val timezone: String
)

enum class ScheduleFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY
}

/**
 * Generated report.
 */
data class GeneratedReport(
    val id: String,
    val definitionId: String,
    val tenantId: TenantId,
    val generatedAt: Instant,
    val period: DateRange,
    val format: ReportFormat,
    val fileUrl: String,
    val fileSize: Long,
    val status: GenerationStatus,
    val errorMessage: String?
)

enum class GenerationStatus {
    PENDING,
    GENERATING,
    COMPLETED,
    FAILED
}

data class SyncJob(
    val id: String,
    val status: JobStatus,
    val startedAt: Instant,
    val completedAt: Instant?,
    val recordsProcessed: Int,
    val recordsCreated: Int,
    val recordsUpdated: Int,
    val recordsFailed: Int
)

enum class JobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Drill-down query for interactive analytics.
 */
data class DrillDownQuery(
    val dimension: String,     // e.g., "location", "department", "time"
    val filters: Map<String, List<String>>,
    val metrics: List<String>,
    val groupBy: List<String>,
    val sortBy: String?,
    val sortOrder: SortOrder,
    val limit: Int?
)

enum class SortOrder {
    ASC,
    DESC
}

/**
 * Drill-down result.
 */
data class DrillDownResult(
    val columns: List<String>,
    val rows: List<Map<String, Any>>,
    val totalCount: Int,
    val summary: Map<String, Double>
)

/**
 * Saved dashboard layout.
 */
data class DashboardLayout(
    val id: String,
    val tenantId: TenantId,
    val userId: Long,
    val name: String,
    val isDefault: Boolean,
    val widgets: List<DashboardWidget>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class DashboardWidget(
    val id: String,
    val type: WidgetType,
    val title: String,
    val configuration: WidgetConfiguration,
    val position: WidgetPosition,
    val size: WidgetSize
)

enum class WidgetType {
    KPI_CARD,
    LINE_CHART,
    BAR_CHART,
    PIE_CHART,
    HEAT_MAP,
    DATA_TABLE,
    GAUGE,
    RECENT_ACTIVITY,
    ALERTS_LIST
}

data class WidgetConfiguration(
    val metric: String?,
    val filters: Map<String, Any>?,
    val timeRange: String?,  // "7d", "30d", "90d", "1y"
    val refreshInterval: Int?  // seconds
)

data class WidgetPosition(
    val x: Int,
    val y: Int
)

data class WidgetSize(
    val width: Int,   // Grid columns (1-12)
    val height: Int   // Grid rows
)
