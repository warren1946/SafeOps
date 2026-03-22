package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.DataTable
import org.example.project.ui.components.SectionHeader
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.components.TableCell
import org.example.project.ui.components.TableRow
import org.example.project.ui.components.TableScoreCell
import org.example.project.ui.components.TableStatusCell
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Data class representing an inspection
 */
data class Inspection(
    val id: String,
    val site: String,
    val location: String,
    val officer: String,
    val date: String,
    val score: String?,
    val status: InspectionStatus
)

/**
 * Inspection status enum
 */
enum class InspectionStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    COMPLETED("Completed", MiningSafetyColors.Success),
    IN_PROGRESS("In-Progress", MiningSafetyColors.Primary),
    FLAGGED("Flagged", MiningSafetyColors.Error),
    SCHEDULED("Scheduled", MiningSafetyColors.Warning)
}

/**
 * Inspections screen with data table
 */
@Composable
fun InspectionsScreen() {
    val inspections = rememberInspectionsData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        InspectionsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions row
        InspectionsActionsRow()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Inspections table
        DataTable(
            columns = listOf("ID", "Site / Location", "Officer", "Date", "Score", "Status")
        ) {
            inspections.forEach { inspection ->
                InspectionRow(inspection = inspection)
            }
        }
    }
}

@Composable
private fun InspectionsHeader() {
    Text(
        text = "Inspections",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "View and manage all safety inspections",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun InspectionsActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filter button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("🔍 Filter", color = MiningSafetyColors.OnSurface)
        }
        
        // Export button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("📥 Export", color = MiningSafetyColors.OnSurface)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // New inspection button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.Primary
            )
        ) {
            Text("➕ New Inspection")
        }
    }
}

@Composable
private fun InspectionRow(inspection: Inspection) {
    TableRow {
        TableCell(
            text = inspection.id,
            fontWeight = FontWeight.Medium
        )
        TableCell(
            text = "${inspection.site}\n${inspection.location}",
            weight = 1.5f
        )
        TableCell(text = inspection.officer)
        TableCell(text = inspection.date)
        TableScoreCell(score = inspection.score)
        TableStatusCell(
            status = inspection.status.label,
            statusColor = inspection.status.color
        )
    }
}

@Composable
private fun rememberInspectionsData(): List<Inspection> {
    return listOf(
        Inspection(
            id = "INS-001",
            site = "Shaft A",
            location = "Level 3",
            officer = "Mike Johnson",
            date = "2026-02-08",
            score = "94%",
            status = InspectionStatus.COMPLETED
        ),
        Inspection(
            id = "INS-002",
            site = "Processing Plant",
            location = "Main Facility",
            officer = "Sarah Williams",
            date = "2026-02-08",
            score = null,
            status = InspectionStatus.IN_PROGRESS
        ),
        Inspection(
            id = "INS-003",
            site = "Tailings Dam B",
            location = "Dam Perimeter",
            officer = "Carlos Rivera",
            date = "2026-02-07",
            score = "78%",
            status = InspectionStatus.COMPLETED
        ),
        Inspection(
            id = "INS-004",
            site = "Underground Tunnel",
            location = "Tunnel 5",
            officer = "Aisha Patel",
            date = "2026-02-07",
            score = "88%",
            status = InspectionStatus.COMPLETED
        ),
        Inspection(
            id = "INS-005",
            site = "Equipment Yard",
            location = "North Section",
            officer = "Tom Baker",
            date = "2026-02-06",
            score = "52%",
            status = InspectionStatus.FLAGGED
        ),
        Inspection(
            id = "INS-006",
            site = "Ventilation System",
            location = "Main Shaft",
            officer = "Mike Johnson",
            date = "2026-02-06",
            score = "91%",
            status = InspectionStatus.COMPLETED
        )
    )
}
