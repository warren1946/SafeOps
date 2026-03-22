package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.ChartCard
import org.example.project.ui.components.SimpleBarChart
import org.example.project.ui.components.SimpleLineChartPlaceholder
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Reports screen with analytics and charts
 */
@Composable
fun ReportsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        ReportsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        ReportsActionsRow()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Charts
        MonthlyInspectionsChart()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        IncidentsByCategoryChart()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ComplianceRateChart()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary stats
        ReportsSummarySection()
    }
}

@Composable
private fun ReportsHeader() {
    Text(
        text = "Reports",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Analytics and compliance reporting",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun ReportsActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("📅 Date Range", color = MiningSafetyColors.OnSurface)
        }
        
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("📥 Export PDF", color = MiningSafetyColors.OnSurface)
        }
    }
}

@Composable
private fun MonthlyInspectionsChart() {
    val data = listOf(
        "Sep" to 0.75f,
        "Oct" to 0.9f,
        "Nov" to 0.85f,
        "Dec" to 1.0f,
        "Jan" to 0.92f,
        "Feb" to 0.67f
    )
    
    ChartCard(
        title = "MONTHLY INSPECTIONS",
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (month, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Value label
                    Text(
                        text = "${(value * 60).toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Bars
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Inspections bar (dark)
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((value * 100).dp)
                                .background(
                                    MiningSafetyColors.Primary,
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        // Issues bar (red)
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height((value * 30).dp)
                                .background(
                                    MiningSafetyColors.Error,
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = month,
                        style = MaterialTheme.typography.labelSmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun IncidentsByCategoryChart() {
    val data = listOf(
        "Rock Fall" to 0.7f to MiningSafetyColors.Error,
        "Equipment" to 1.0f to Color(0xFFF59E0B),
        "Electrical" to 0.5f to MiningSafetyColors.Warning,
        "Chemical" to 0.3f to Color(0xFF10B981),
        "Structural" to 0.7f to MiningSafetyColors.Primary,
        "Ventilation" to 0.4f to Color(0xFF8B5CF6)
    )
    
    ChartCard(
        title = "INCIDENTS BY CATEGORY",
        modifier = Modifier.fillMaxWidth()
    ) {
        // Pie chart representation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut chart placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(75.dp))
                    .background(MiningSafetyColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🥧",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            
            // Legend
            Column {
                data.forEach { (pair, color) ->
                    val (label, value) = pair
                    LegendItem(
                        label = label,
                        percentage = "${(value * 100).toInt()}%",
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, percentage: String, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label $percentage",
            style = MaterialTheme.typography.bodySmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun ComplianceRateChart() {
    ChartCard(
        title = "COMPLIANCE RATE OVER TIME",
        modifier = Modifier.fillMaxWidth()
    ) {
        SimpleLineChartPlaceholder(
            labels = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb")
        )
    }
}

@Composable
private fun ReportsSummarySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "SUMMARY",
                style = MaterialTheme.typography.labelMedium,
                color = MiningSafetyColors.OnSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SummaryRow("Total Inspections", "324", "+12% from last month", MiningSafetyColors.Success)
            SummaryRow("Open Hazards", "7", "-2 from last month", MiningSafetyColors.Success)
            SummaryRow("Compliance Rate", "93%", "+2% this month", MiningSafetyColors.Success)
            SummaryRow("Incidents Reported", "18", "+3 from last month", MiningSafetyColors.Warning)
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    trend: String,
    trendColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = trend,
                style = MaterialTheme.typography.labelSmall,
                color = trendColor
            )
        }
    }
}
