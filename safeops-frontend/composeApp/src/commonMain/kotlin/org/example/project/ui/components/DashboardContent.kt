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
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Main dashboard content component displaying stats, charts, and lists
 *
 * @param modifier Modifier for styling
 */
@Composable
fun DashboardContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        StatsRow()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ChartsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ListsSection()
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
        text = "Mining safety overview - Friday, February 20, 2026",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

/**
 * Row of statistics cards
 */
@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            title = "TODAY'S INSPECTIONS",
            value = "12",
            subtitle = "+3 from yesterday",
            subtitleColor = MiningSafetyColors.Success,
            icon = "📋",
            iconBackground = Color(0xFFFEE2E2),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "OPEN HAZARDS",
            value = "7",
            subtitle = "2 critical",
            subtitleColor = MiningSafetyColors.Error,
            icon = "⚠️",
            iconBackground = Color(0xFFFEF3C7),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "COMPLIANCE RATE",
            value = "93%",
            subtitle = "+2% this month",
            subtitleColor = MiningSafetyColors.Success,
            icon = "✅",
            iconBackground = Color(0xFFD1FAE5),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "WHATSAPP ACTIVE",
            value = "24",
            subtitle = "Officers online",
            subtitleColor = MiningSafetyColors.OnSurfaceVariant,
            icon = "💬",
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
private fun ListsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recent Inspections
        RecentInspectionsCard(modifier = Modifier.weight(1f))
        
        // Active Hazards
        ActiveHazardsCard(modifier = Modifier.weight(1f))
    }
}

/**
 * Card displaying recent inspections list
 */
@Composable
private fun RecentInspectionsCard(modifier: Modifier = Modifier) {
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
                title = "RECENT INSPECTIONS",
                onViewAll = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InspectionListItem(
                title = "Shaft A - Level 3",
                officer = "Mike Johnson",
                date = "2026-02-08",
                score = "94%",
                status = "Completed",
                statusColor = MiningSafetyColors.Success
            )
            InspectionListItem(
                title = "Processing Plant",
                officer = "Sarah Williams",
                date = "2026-02-08",
                score = null,
                status = "In-Progress",
                statusColor = MiningSafetyColors.Primary
            )
            InspectionListItem(
                title = "Tailings Dam B",
                officer = "Carlos Rivera",
                date = "2026-02-07",
                score = "78%",
                status = "Completed",
                statusColor = MiningSafetyColors.Warning
            )
            InspectionListItem(
                title = "Underground Tunnel 5",
                officer = "Aisha Patel",
                date = "2026-02-07",
                score = "88%",
                status = "Completed",
                statusColor = MiningSafetyColors.Success
            )
            InspectionListItem(
                title = "Equipment Yard",
                officer = "Tom Baker",
                date = "2026-02-06",
                score = "52%",
                status = "Flagged",
                statusColor = MiningSafetyColors.Error
            )
        }
    }
}

/**
 * Card displaying active hazards list
 */
@Composable
private fun ActiveHazardsCard(modifier: Modifier = Modifier) {
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
                title = "ACTIVE HAZARDS",
                onViewAll = {}
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HazardListItem(
                title = "Loose rock fall risk - Shaft A",
                location = "Shaft A - Level 3",
                severity = "Critical",
                severityColor = MiningSafetyColors.Error
            )
            HazardListItem(
                title = "Water seepage near electrical panel",
                location = "Processing Plant",
                severity = "High",
                severityColor = MiningSafetyColors.Warning
            )
            HazardListItem(
                title = "Excessive dust levels",
                location = "Underground Tunnel 5",
                severity = "High",
                severityColor = MiningSafetyColors.Warning
            )
        }
    }
}
