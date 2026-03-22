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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Hazard severity levels
 */
enum class HazardSeverity(val label: String, val color: androidx.compose.ui.graphics.Color) {
    CRITICAL("Critical", MiningSafetyColors.Error),
    HIGH("High", MiningSafetyColors.Warning),
    MEDIUM("Medium", androidx.compose.ui.graphics.Color(0xFFF59E0B)),
    LOW("Low", MiningSafetyColors.Success)
}

/**
 * Data class for hazard report
 */
data class HazardReport(
    val id: String,
    val title: String,
    val location: String,
    val reportedBy: String,
    val date: String,
    val severity: HazardSeverity,
    val status: HazardStatus
)

enum class HazardStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    OPEN("Open", MiningSafetyColors.Error),
    INVESTIGATING("Investigating", MiningSafetyColors.Warning),
    RESOLVED("Resolved", MiningSafetyColors.Success)
}

/**
 * Hazards screen with hazard report cards
 */
@Composable
fun HazardsScreen() {
    val hazards = rememberHazardsData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HazardsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Summary stats
        HazardsSummaryRow()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        HazardsActionsRow()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hazard cards
        hazards.forEach { hazard ->
            HazardCard(hazard = hazard)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HazardsHeader() {
    Text(
        text = "Hazard Reports",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Track and manage identified hazards",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun HazardsSummaryRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HazardSummaryCard(
            count = "7",
            label = "Open",
            color = MiningSafetyColors.Error,
            modifier = Modifier.weight(1f)
        )
        HazardSummaryCard(
            count = "12",
            label = "Investigating",
            color = MiningSafetyColors.Warning,
            modifier = Modifier.weight(1f)
        )
        HazardSummaryCard(
            count = "45",
            label = "Resolved",
            color = MiningSafetyColors.Success,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HazardSummaryCard(
    count: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
private fun HazardsActionsRow() {
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
            Text("🔍 Filter", color = MiningSafetyColors.OnSurface)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.Error
            )
        ) {
            Text("➕ Report Hazard")
        }
    }
}

@Composable
private fun HazardCard(hazard: HazardReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Severity indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(hazard.severity.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = hazard.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📍 ${hazard.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = "By ${hazard.reportedBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = hazard.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                }
            }
            
            // Badges
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(
                    status = hazard.severity.label,
                    color = hazard.severity.color
                )
                StatusBadge(
                    status = hazard.status.label,
                    color = hazard.status.color
                )
            }
        }
    }
}

@Composable
private fun rememberHazardsData(): List<HazardReport> {
    return listOf(
        HazardReport(
            id = "HZ-001",
            title = "Loose rock fall risk - Shaft A",
            location = "Shaft A - Level 3",
            reportedBy = "Mike Johnson",
            date = "2026-02-08",
            severity = HazardSeverity.CRITICAL,
            status = HazardStatus.OPEN
        ),
        HazardReport(
            id = "HZ-002",
            title = "Water seepage near electrical panel",
            location = "Processing Plant",
            reportedBy = "Sarah Williams",
            date = "2026-02-08",
            severity = HazardSeverity.HIGH,
            status = HazardStatus.INVESTIGATING
        ),
        HazardReport(
            id = "HZ-003",
            title = "Damaged safety railing",
            location = "Equipment Yard",
            reportedBy = "Tom Baker",
            date = "2026-02-07",
            severity = HazardSeverity.MEDIUM,
            status = HazardStatus.RESOLVED
        ),
        HazardReport(
            id = "HZ-004",
            title = "Excessive dust levels",
            location = "Underground Tunnel 5",
            reportedBy = "Carlos Rivera",
            date = "2026-02-07",
            severity = HazardSeverity.HIGH,
            status = HazardStatus.OPEN
        ),
        HazardReport(
            id = "HZ-005",
            title = "Faulty emergency exit signage",
            location = "Admin Building",
            reportedBy = "Aisha Patel",
            date = "2026-02-06",
            severity = HazardSeverity.LOW,
            status = HazardStatus.RESOLVED
        )
    )
}
