package com.zama.safeops.frontend.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zama.safeops.frontend.domain.model.Hazard
import com.zama.safeops.frontend.domain.model.HazardSeverity
import com.zama.safeops.frontend.domain.model.Inspection
import com.zama.safeops.frontend.domain.model.InspectionStatus
import com.zama.safeops.frontend.domain.model.SafetyScore
import com.zama.safeops.frontend.domain.model.TrendDirection
import com.zama.safeops.frontend.presentation.screens.auth.AuthViewModel
import com.zama.safeops.frontend.presentation.screens.auth.LoginScreen
import com.zama.safeops.frontend.presentation.theme.SafetyBlue
import com.zama.safeops.frontend.presentation.theme.SafetyGreen
import com.zama.safeops.frontend.presentation.theme.SafetyOrange
import com.zama.safeops.frontend.presentation.theme.SafetyRed
import com.zama.safeops.frontend.presentation.theme.SafetyYellow

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            Scaffold(
                topBar = { DashboardTopBar() },
                bottomBar = { DashboardBottomBar() },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* TODO: Quick action */ },
                        containerColor = SafetyOrange
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar() {
    val navigator = LocalNavigator.currentOrThrow
    val authViewModel = koinScreenModel<AuthViewModel>()

    TopAppBar(
        title = { Text("SafeOps Dashboard") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = { /* TODO: Notifications */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = {
                authViewModel.logout()
                navigator.replace(LoginScreen())
            }) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
private fun DashboardBottomBar() {
    val tabNavigator = LocalTabNavigator.current

    NavigationBar {
        NavigationBarItem(
            selected = tabNavigator.current == HomeTab,
            onClick = { tabNavigator.current = HomeTab },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = tabNavigator.current == InspectionsTab,
            onClick = { tabNavigator.current = InspectionsTab },
            icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
            label = { Text("Inspections") }
        )
        NavigationBarItem(
            selected = tabNavigator.current == HazardsTab,
            onClick = { tabNavigator.current = HazardsTab },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            label = { Text("Hazards") }
        )
        NavigationBarItem(
            selected = tabNavigator.current == EquipmentTab,
            onClick = { tabNavigator.current = EquipmentTab },
            icon = { Icon(Icons.Default.Build, contentDescription = null) },
            label = { Text("Equipment") }
        )
    }
}

// Tabs
object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 0u, title = "Home")

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DashboardViewModel>()
        val safetyScore by viewModel.safetyScore.collectAsState()
        val recentInspections by viewModel.recentInspections.collectAsState()
        val openHazards by viewModel.openHazards.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SafetyScoreCard(safetyScore)
                }

                item {
                    Text(
                        "Recent Inspections",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (recentInspections.isEmpty()) {
                    item {
                        EmptyStateMessage("No recent inspections")
                    }
                } else {
                    items(recentInspections) { inspection ->
                        InspectionCard(inspection)
                    }
                }

                item {
                    Text(
                        "Open Hazards",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (openHazards.isEmpty()) {
                    item {
                        EmptyStateMessage("No open hazards")
                    }
                } else {
                    items(openHazards) { hazard ->
                        HazardCard(hazard)
                    }
                }
            }
        }
    }
}

object InspectionsTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 1u, title = "Inspections")

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Inspections List (Coming Soon)")
        }
    }
}

object HazardsTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 2u, title = "Hazards")

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Hazards List (Coming Soon)")
        }
    }
}

object EquipmentTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 3u, title = "Equipment")

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Equipment Status (Coming Soon)")
        }
    }
}

// UI Components
@Composable
private fun SafetyScoreCard(score: SafetyScore?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Safety Score",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = score?.overallScore?.toString() ?: "--",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "/100",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Inspections",
                    value = "${score?.inspectionsCompleted ?: 0}/${score?.inspectionsScheduled ?: 0}"
                )
                StatItem(
                    label = "Open Hazards",
                    value = (score?.openHazards ?: 0).toString()
                )
                StatItem(
                    label = "Days Safe",
                    value = (score?.daysSinceLastIncident ?: 0).toString()
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InspectionCard(inspection: Inspection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    inspection.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    inspection.location ?: "No location",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusChip(
                text = inspection.status.name,
                color = when (inspection.status) {
                    InspectionStatus.COMPLETED -> SafetyGreen
                    InspectionStatus.IN_PROGRESS -> SafetyBlue
                    InspectionStatus.SCHEDULED -> SafetyYellow
                    InspectionStatus.OVERDUE -> SafetyRed
                    InspectionStatus.CANCELLED -> Color.Gray
                }
            )
        }
    }
}

@Composable
private fun HazardCard(hazard: Hazard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hazard.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    hazard.location ?: "No location",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusChip(
                text = hazard.severity.name,
                color = when (hazard.severity) {
                    HazardSeverity.CRITICAL -> SafetyRed
                    HazardSeverity.HIGH -> SafetyOrange
                    HazardSeverity.MEDIUM -> SafetyYellow
                    HazardSeverity.LOW -> SafetyGreen
                }
            )
        }
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
