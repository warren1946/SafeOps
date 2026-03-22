package org.example.project.domain.model

/**
 * Domain model for Dashboard statistics
 */
data class DashboardStatistics(
    val totalInspections: Int = 0,
    val pendingInspections: Int = 0,
    val completedInspections: Int = 0,
    val openHazards: Int = 0,
    val criticalHazards: Int = 0,
    val totalUsers: Int = 0,
    val recentActivity: List<Activity> = emptyList()
)

/**
 * Activity item for dashboard
 */
data class Activity(
    val id: Long,
    val type: ActivityType,
    val description: String,
    val timestamp: String,
    val userId: Long? = null,
    val userName: String? = null
)

enum class ActivityType {
    INSPECTION_CREATED,
    INSPECTION_COMPLETED,
    HAZARD_REPORTED,
    HAZARD_RESOLVED,
    USER_REGISTERED,
    USER_LOGIN;
    
    companion object {
        fun fromString(type: String): ActivityType = try {
            valueOf(type.uppercase())
        } catch (e: IllegalArgumentException) {
            INSPECTION_CREATED
        }
    }
}

/**
 * Chart data for dashboard
 */
data class DashboardChartData(
    val labels: List<String> = emptyList(),
    val datasets: List<ChartDataset> = emptyList()
)

data class ChartDataset(
    val label: String,
    val data: List<Double> = emptyList(),
    val color: String? = null
)
