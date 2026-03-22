package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.domain.model.DashboardStatistics
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Main dashboard content component displaying stats, charts, and lists
 *
 * @param statistics Dashboard statistics data
 * @param modifier Modifier for styling
 */
@Composable
fun DashboardContent(
    statistics: DashboardStatistics? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        StatsRow(statistics = statistics)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ChartsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ListsSection(statistics = statistics)
    }
}

/**
 * Dashboard header with title and date
 */
@Composable
private fun DashboardHeader() {
    Text(
        text = "Dashboard",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Mining safety overview",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

/**
 * Row of statistics cards
 */
@Composable
private fun StatsRow(statistics: DashboardStatistics?) {
    val stats = statistics ?: DashboardStatistics()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "TOTAL INSPECTIONS",
            value = stats.totalInspections.toString(),
            subtitle = "${stats.pendingInspections} pending",
            subtitleColor = MiningSafetyColors.Warning,
            icon = "📋",
            iconBackground = Color(0xFFFEE2E2),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "OPEN HAZARDS",
            value = stats.openHazards.toString(),
            subtitle = "${stats.criticalHazards} critical",
            subtitleColor = if (stats.criticalHazards > 0) MiningSafetyColors.Error else MiningSafetyColors.Success,
            icon = "⚠️",
            iconBackground = Color(0xFFFEF3C7),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "COMPLETED",
            value = stats.completedInspections.toString(),
            subtitle = "inspections done",
            subtitleColor = MiningSafetyColors.Success,
            icon = "✅",
            iconBackground = Color(0xFFD1FAE5),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "TEAM MEMBERS",
            value = stats.totalUsers.toString(),
            subtitle = "Active users",
            subtitleColor = MiningSafetyColors.OnSurfaceVariant,
            icon = "👥",
            iconBackground = Color(0xFFDBEAFE),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Charts section with compliance trend and incidents by type
 */
@Composable
private fun ChartsSection() {
    // Compliance Rate Trend
    ChartCard(
        title = "COMPLIANCE RATE TREND",
        modifier = Modifier.fillMaxWidth()
    ) {
        SimpleLineChartPlaceholder(
            labels = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb")
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Incidents by Type
    val incidentData = listOf(
        "Rock Fall" to 0.7f,
        "Equipment" to 1.0f,
        "Electrical" to 0.5f,
        "Chemical" to 0.3f,
        "Structural" to 0.7f,
        "Ventilation" to 0.4f
    )
    
    ChartCard(
        title = "INCIDENTS BY TYPE",
        modifier = Modifier.fillMaxWidth()
    ) {
        SimpleBarChart(data = incidentData)
    }
}

/**
 * Lists section with recent inspections and active hazards
 */
@Composable
private fun ListsSection(statistics: DashboardStatistics?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recent Inspections
        RecentInspectionsCard(
            statistics = statistics,
            modifier = Modifier.weight(1f)
        )
        
        // Active Hazards
        ActiveHazardsCard(
            statistics = statistics,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Card displaying recent inspections list
 */
@Composable
private fun RecentInspectionsCard(
    statistics: DashboardStatistics?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "RECENT ACTIVITY",
                onViewAll = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (statistics?.recentActivity.isNullOrEmpty()) {
                Text(
                    text = "No recent activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiningSafetyColors.OnSurfaceVariant
                )
            } else {
                statistics?.recentActivity?.take(5)?.forEach { activity ->
                    ActivityListItem(
                        title = activity.description,
                        subtitle = activity.userName ?: "System",
                        timestamp = activity.timestamp.take(10),
                        icon = when (activity.type) {
                            org.example.project.domain.model.ActivityType.INSPECTION_CREATED,
                            org.example.project.domain.model.ActivityType.INSPECTION_COMPLETED -> "📋"
                            org.example.project.domain.model.ActivityType.HAZARD_REPORTED,
                            org.example.project.domain.model.ActivityType.HAZARD_RESOLVED -> "⚠️"
                            org.example.project.domain.model.ActivityType.USER_REGISTERED,
                            org.example.project.domain.model.ActivityType.USER_LOGIN -> "👤"
                        }
                    )
                }
            }
        }
    }
}

/**
 * Card displaying active hazards summary
 */
@Composable
private fun ActiveHazardsCard(
    statistics: DashboardStatistics?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "HAZARD SUMMARY",
                onViewAll = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Show hazard counts by severity
            HazardSummaryItem(
                label = "Critical Hazards",
                count = statistics?.criticalHazards?.toString() ?: "0",
                color = MiningSafetyColors.Error
            )
            HazardSummaryItem(
                label = "Open Hazards",
                count = statistics?.openHazards?.toString() ?: "0",
                color = MiningSafetyColors.Warning
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Stay vigilant and report any safety concerns immediately.",
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun HazardSummaryItem(
    label: String,
    count: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurface
        )
        Text(
            text = count,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
