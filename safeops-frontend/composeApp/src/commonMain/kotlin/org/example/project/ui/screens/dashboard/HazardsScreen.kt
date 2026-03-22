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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.di.ServiceLocator
import org.example.project.domain.model.Hazard
import org.example.project.domain.model.HazardSeverity
import org.example.project.domain.model.HazardStatus
import org.example.project.presentation.viewmodel.HazardsViewModel
import org.example.project.ui.components.CardSkeleton
import org.example.project.ui.components.EmptyState
import org.example.project.ui.components.FullScreenError
import org.example.project.ui.components.InlineError
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Hazards screen with hazard report cards - connected to ViewModel
 */
@Composable
fun HazardsScreen(
    viewModel: HazardsViewModel = remember { ServiceLocator.provideHazardsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        HazardsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Summary stats
        HazardsSummaryRow(
            openCount = uiState.hazards.count { it.status == HazardStatus.OPEN },
            inProgressCount = uiState.hazards.count { it.status == HazardStatus.IN_PROGRESS },
            resolvedCount = uiState.hazards.count { it.status == HazardStatus.RESOLVED }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        HazardsActionsRow(
            onRefresh = viewModel::refreshHazards
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on state
        when {
            uiState.isLoading && uiState.hazards.isEmpty() -> {
                // Show skeleton loaders
                repeat(3) {
                    CardSkeleton()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            uiState.error != null && uiState.hazards.isEmpty() -> {
                FullScreenError(
                    message = uiState.error ?: "Failed to load hazards",
                    onRetry = viewModel::refreshHazards
                )
            }
            uiState.hazards.isEmpty() -> {
                EmptyState(
                    icon = "⚠️",
                    title = "No hazards reported",
                    message = "Great! No hazards have been reported. Stay vigilant and report any safety concerns."
                )
            }
            else -> {
                // Show error banner if there's an error but we have cached data
                if (uiState.error != null) {
                    InlineError(
                        message = uiState.error ?: "Update failed",
                        onRetry = viewModel::refreshHazards,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Hazard cards
                uiState.hazards.forEach { hazard ->
                    HazardCard(
                        hazard = hazard,
                        onClick = { viewModel.selectHazard(hazard) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
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
private fun HazardsSummaryRow(
    openCount: Int,
    inProgressCount: Int,
    resolvedCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HazardSummaryCard(
            count = openCount.toString(),
            label = "Open",
            color = MiningSafetyColors.Error,
            modifier = Modifier.weight(1f)
        )
        HazardSummaryCard(
            count = inProgressCount.toString(),
            label = "In Progress",
            color = MiningSafetyColors.Warning,
            modifier = Modifier.weight(1f)
        )
        HazardSummaryCard(
            count = resolvedCount.toString(),
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
private fun HazardsActionsRow(
    onRefresh: () -> Unit
) {
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
        
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("🔄 Refresh", color = MiningSafetyColors.OnSurface)
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
private fun HazardCard(
    hazard: Hazard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
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
                    .background(getSeverityColor(hazard.severity).copy(alpha = 0.1f)),
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
                        text = "📍 ${hazard.location ?: "Unknown location"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    Text(
                        text = "ID: ${hazard.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                    hazard.createdAt?.let { date ->
                        Text(
                            text = "•",
                            color = MiningSafetyColors.OnSurfaceVariant
                        )
                        Text(
                            text = date.take(10),
                            style = MaterialTheme.typography.bodySmall,
                            color = MiningSafetyColors.OnSurfaceVariant
                        )
                    }
                }
            }
            
            // Badges
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(
                    status = hazard.severity.name,
                    color = getSeverityColor(hazard.severity)
                )
                StatusBadge(
                    status = hazard.status.name,
                    color = when (hazard.status) {
                        HazardStatus.OPEN -> MiningSafetyColors.Error
                        HazardStatus.IN_PROGRESS -> MiningSafetyColors.Warning
                        HazardStatus.RESOLVED -> MiningSafetyColors.Success
                        HazardStatus.CLOSED -> MiningSafetyColors.OnSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun getSeverityColor(severity: HazardSeverity): androidx.compose.ui.graphics.Color {
    return when (severity) {
        HazardSeverity.CRITICAL -> MiningSafetyColors.Error
        HazardSeverity.HIGH -> MiningSafetyColors.Warning
        HazardSeverity.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
        HazardSeverity.LOW -> MiningSafetyColors.Success
    }
}
