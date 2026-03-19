/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.reporting.application.services

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate

/**
 * Service for generating analytics and reports for the dashboard.
 */
@Service
class AnalyticsService(
    private val inspectionMetricsPort: InspectionMetricsPort,
    private val hazardMetricsPort: HazardMetricsPort,
    private val safetyMetricsPort: SafetyMetricsPort
) {

    /**
     * Generate dashboard overview statistics.
     */
    fun getDashboardOverview(tenantId: TenantId): DashboardOverview {
        val today = LocalDate.now()
        val thirtyDaysAgo = today.minusDays(30)

        return DashboardOverview(
            todayInspections = inspectionMetricsPort.countTodayInspections(tenantId),
            inspectionsTrend = inspectionMetricsPort.getTrend(tenantId, 7),
            openHazards = hazardMetricsPort.countOpenHazards(tenantId),
            criticalHazards = hazardMetricsPort.countCriticalHazards(tenantId),
            complianceRate = inspectionMetricsPort.calculateComplianceRate(tenantId, thirtyDaysAgo, today),
            complianceTrend = inspectionMetricsPort.getComplianceTrend(tenantId, 6),
            whatsAppActiveUsers = inspectionMetricsPort.countWhatsAppActiveUsers(tenantId),
            activeOfficers = inspectionMetricsPort.countActiveOfficers(tenantId)
        )
    }

    /**
     * Generate monthly inspection statistics.
     */
    fun getMonthlyInspectionStats(tenantId: TenantId, months: Int = 6): List<MonthlyInspectionStats> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusMonths(months.toLong())
        return inspectionMetricsPort.getMonthlyStats(tenantId, startDate, endDate)
    }

    /**
     * Generate incidents by category for pie chart.
     */
    fun getIncidentsByCategory(tenantId: TenantId, from: LocalDate, to: LocalDate): List<CategoryStat> {
        return safetyMetricsPort.getIncidentsByCategory(tenantId, from, to)
    }

    /**
     * Generate compliance rate trend over time.
     */
    fun getComplianceTrend(tenantId: TenantId, months: Int = 6): List<CompliancePoint> {
        return inspectionMetricsPort.getComplianceTrend(tenantId, months)
    }

    /**
     * Generate safety report.
     */
    fun generateSafetyReport(
        tenantId: TenantId,
        periodStart: Instant,
        periodEnd: Instant
    ): SafetyReport {
        return SafetyReport(
            periodStart = periodStart,
            periodEnd = periodEnd,
            generatedAt = Instant.now(),
            summary = generateReportSummary(tenantId, periodStart, periodEnd),
            inspectionStats = inspectionMetricsPort.getDetailedStats(tenantId, periodStart, periodEnd),
            hazardStats = hazardMetricsPort.getDetailedStats(tenantId, periodStart, periodEnd),
            safetyEventStats = safetyMetricsPort.getDetailedStats(tenantId, periodStart, periodEnd)
        )
    }

    /**
     * Get recent inspections for dashboard.
     */
    fun getRecentInspections(tenantId: TenantId, limit: Int = 5): List<InspectionSummary> {
        return inspectionMetricsPort.getRecentInspections(tenantId, limit)
    }

    /**
     * Get active hazards for dashboard.
     */
    fun getActiveHazards(tenantId: TenantId, limit: Int = 5): List<HazardSummary> {
        return hazardMetricsPort.getActiveHazards(tenantId, limit)
    }

    private fun generateReportSummary(tenantId: TenantId, start: Instant, end: Instant): ReportSummary {
        return ReportSummary(
            totalInspections = inspectionMetricsPort.countInPeriod(tenantId, start, end),
            completedInspections = inspectionMetricsPort.countCompleted(tenantId, start, end),
            averageScore = inspectionMetricsPort.calculateAverageScore(tenantId, start, end),
            totalHazards = hazardMetricsPort.countInPeriod(tenantId, start, end),
            resolvedHazards = hazardMetricsPort.countResolved(tenantId, start, end),
            totalSafetyEvents = safetyMetricsPort.countInPeriod(tenantId, start, end),
            criticalEvents = safetyMetricsPort.countCritical(tenantId, start, end)
        )
    }
}

// ==================== Domain Models for Reporting ====================

data class DashboardOverview(
    val todayInspections: Int,
    val inspectionsTrend: TrendDirection,
    val openHazards: Int,
    val criticalHazards: Int,
    val complianceRate: Double,
    val complianceTrend: List<CompliancePoint>,
    val whatsAppActiveUsers: Int,
    val activeOfficers: Int
)

data class TrendDirection(
    val value: Int,
    val direction: Direction,
    val period: String
) {
    enum class Direction { UP, DOWN, STABLE }
}

data class MonthlyInspectionStats(
    val month: String,
    val completed: Int,
    val failed: Int
)

data class CategoryStat(
    val category: String,
    val count: Int,
    val percentage: Double
)

data class CompliancePoint(
    val month: String,
    val rate: Double
)

data class SafetyReport(
    val periodStart: Instant,
    val periodEnd: Instant,
    val generatedAt: Instant,
    val summary: ReportSummary,
    val inspectionStats: InspectionStats,
    val hazardStats: HazardStats,
    val safetyEventStats: SafetyEventStats
)

data class ReportSummary(
    val totalInspections: Int,
    val completedInspections: Int,
    val averageScore: Double,
    val totalHazards: Int,
    val resolvedHazards: Int,
    val totalSafetyEvents: Int,
    val criticalEvents: Int
)

data class InspectionSummary(
    val id: String,
    val location: String,
    val officerName: String,
    val date: LocalDate,
    val score: Int?,
    val status: String
)

data class HazardSummary(
    val id: String,
    val title: String,
    val location: String,
    val severity: String,
    val status: String,
    val reportedAt: Instant
)

// Placeholder classes for detailed stats
data class InspectionStats(val total: Int, val passed: Int, val failed: Int)
data class HazardStats(val total: Int, val open: Int, val resolved: Int)
data class SafetyEventStats(val total: Int, val bySeverity: Map<String, Int>)

// ==================== Ports ====================

interface InspectionMetricsPort {
    fun countTodayInspections(tenantId: TenantId): Int
    fun getTrend(tenantId: TenantId, days: Int): TrendDirection
    fun calculateComplianceRate(tenantId: TenantId, from: LocalDate, to: LocalDate): Double
    fun getComplianceTrend(tenantId: TenantId, months: Int): List<CompliancePoint>
    fun countWhatsAppActiveUsers(tenantId: TenantId): Int
    fun countActiveOfficers(tenantId: TenantId): Int
    fun getMonthlyStats(tenantId: TenantId, from: LocalDate, to: LocalDate): List<MonthlyInspectionStats>
    fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): InspectionStats
    fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int
    fun countCompleted(tenantId: TenantId, from: Instant, to: Instant): Int
    fun calculateAverageScore(tenantId: TenantId, from: Instant, to: Instant): Double
    fun getRecentInspections(tenantId: TenantId, limit: Int): List<InspectionSummary>
}

interface HazardMetricsPort {
    fun countOpenHazards(tenantId: TenantId): Int
    fun countCriticalHazards(tenantId: TenantId): Int
    fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): HazardStats
    fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int
    fun countResolved(tenantId: TenantId, from: Instant, to: Instant): Int
    fun getActiveHazards(tenantId: TenantId, limit: Int): List<HazardSummary>
}

interface SafetyMetricsPort {
    fun getIncidentsByCategory(tenantId: TenantId, from: LocalDate, to: LocalDate): List<CategoryStat>
    fun getDetailedStats(tenantId: TenantId, from: Instant, to: Instant): SafetyEventStats
    fun countInPeriod(tenantId: TenantId, from: Instant, to: Instant): Int
    fun countCritical(tenantId: TenantId, from: Instant, to: Instant): Int
}
