package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.di.ServiceLocator
import org.example.project.domain.model.Inspection
import org.example.project.domain.model.InspectionStatus
import org.example.project.domain.model.Result
import org.example.project.presentation.viewmodel.InspectionsViewModel
import org.example.project.ui.components.DataTable
import org.example.project.ui.components.EmptyState
import org.example.project.ui.components.FullScreenError
import org.example.project.ui.components.FullScreenLoading
import org.example.project.ui.components.InlineError
import org.example.project.ui.components.SectionHeader
import org.example.project.ui.components.TableCell
import org.example.project.ui.components.TableRow
import org.example.project.ui.components.TableScoreCell
import org.example.project.ui.components.TableStatusCell
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Inspections screen with data table - connected to ViewModel
 */
@Composable
fun InspectionsScreen(
    viewModel: InspectionsViewModel = remember { ServiceLocator.provideInspectionsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        InspectionsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions row
        InspectionsActionsRow(
            onRefresh = viewModel::refreshInspections
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on state
        when {
            uiState.isLoading && uiState.inspections.isEmpty() -> {
                FullScreenLoading("Loading inspections...")
            }
            uiState.error != null && uiState.inspections.isEmpty() -> {
                FullScreenError(
                    message = uiState.error ?: "Failed to load inspections",
                    onRetry = viewModel::refreshInspections
                )
            }
            uiState.inspections.isEmpty() -> {
                EmptyState(
                    icon = "📋",
                    title = "No inspections found",
                    message = "There are no inspections to display. Create your first inspection to get started."
                )
            }
            else -> {
                // Show error banner if there's an error but we have cached data
                if (uiState.error != null) {
                    InlineError(
                        message = uiState.error ?: "Update failed",
                        onRetry = viewModel::refreshInspections,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Inspections table
                InspectionsTable(
                    inspections = uiState.inspections,
                    onInspectionClick = viewModel::selectInspection
                )
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
private fun InspectionsActionsRow(
    onRefresh: () -> Unit
) {
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
        
        // Refresh button
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("🔄 Refresh", color = MiningSafetyColors.OnSurface)
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
private fun InspectionsTable(
    inspections: List<Inspection>,
    onInspectionClick: (Inspection) -> Unit
) {
    DataTable(
        columns = listOf("ID", "Title", "Type", "Status", "Assigned To", "Date")
    ) {
        inspections.forEach { inspection ->
            InspectionRow(
                inspection = inspection,
                onClick = { onInspectionClick(inspection) }
            )
        }
    }
}

@Composable
private fun InspectionRow(
    inspection: Inspection,
    onClick: () -> Unit
) {
    TableRow(
        onClick = onClick
    ) {
        TableCell(
            text = "INS-${inspection.id.toString().padStart(3, '0')}",
            fontWeight = FontWeight.Medium
        )
        TableCell(
            text = inspection.title,
            weight = 1.5f
        )
        TableCell(
            text = inspection.targetType.name
        )
        TableStatusCell(
            status = inspection.status.name,
            statusColor = when (inspection.status) {
                InspectionStatus.COMPLETED -> MiningSafetyColors.Success
                InspectionStatus.IN_PROGRESS -> MiningSafetyColors.Primary
                InspectionStatus.PENDING -> MiningSafetyColors.Warning
                InspectionStatus.CANCELLED -> MiningSafetyColors.Error
            }
        )
        TableCell(
            text = inspection.assignedOfficerId?.toString() ?: "Unassigned"
        )
        TableCell(
            text = inspection.createdAt?.take(10) ?: "-"
        )
    }
}
