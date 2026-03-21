package com.zama.safeops.frontend.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import cafe.adriel.voyager.navigator.tab.*
import com.zama.safeops.frontend.domain.model.*
import com.zama.safeops.frontend.presentation.rbac.*
import com.zama.safeops.frontend.presentation.screens.admin.AdminDashboardScreen
import com.zama.safeops.frontend.presentation.screens.auth.AuthViewModel
import com.zama.safeops.frontend.presentation.screens.auth.LoginScreen
import com.zama.safeops.frontend.presentation.theme.*

/**
 * Main Dashboard Screen with Role-Based Navigation
 * Different tabs and features shown based on user role
 */
class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        ProvideUser(UserSession.currentUser) {
            TabNavigator(HomeTab) {
                Scaffold(
                    topBar = { DashboardTopBar() },
                    bottomBar = { DashboardBottomBar() },
                    floatingActionButton = { RoleBasedFAB() }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        CurrentTab()
                    }
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
    val currentUser = LocalCurrentUser.current

    TopAppBar(
        title = {
            Column {
                Text("SafeOps Dashboard")
                Text(
                    currentUser.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            // Admin Panel Access - Only for ADMIN and SUPER_ADMIN
            WithAdminPanelAccess {
                IconButton(onClick = {
                    navigator.push(AdminDashboardScreen())
                }) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            IconButton(onClick = { /* TODO: Notifications */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = {
                authViewModel.logout()
                UserSession.logout()
                navigator.replaceAll(LoginScreen())
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
    val currentUser = LocalCurrentUser.current

    NavigationBar {
        // Home - All users
        NavigationBarItem(
            selected = tabNavigator.current == HomeTab,
            onClick = { tabNavigator.current = HomeTab },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )

        // Inspections - All users
        NavigationBarItem(
            selected = tabNavigator.current == InspectionsTab,
            onClick = { tabNavigator.current = InspectionsTab },
            icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
            label = { Text("Inspections") }
        )

        // Hazards - All users except VIEWER
        if (currentUser.primaryRole != UserRole.VIEWER) {
            NavigationBarItem(
                selected = tabNavigator.current == HazardsTab,
                onClick = { tabNavigator.current = HazardsTab },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                label = { Text("Hazards") }
            )
        }

        // Equipment - Only for ADMIN, SUPERVISOR, SUPER_ADMIN
        if (currentUser.primaryRole.canManageEquipment()) {
            NavigationBarItem(
                selected = tabNavigator.current == EquipmentTab,
                onClick = { tabNavigator.current = EquipmentTab },
                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                label = { Text("Equipment") }
            )
        }

        // Profile - All users
        NavigationBarItem(
            selected = tabNavigator.current == ProfileTab,
            onClick = { tabNavigator.current = ProfileTab },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") }
        )
    }
}

@Composable
private fun RoleBasedFAB() {
    val currentUser = LocalCurrentUser.current

    // Show different FAB based on role
    when (currentUser.primaryRole) {
        UserRole.SUPER_ADMIN, UserRole.ADMIN -> AdminFAB()
        UserRole.SUPERVISOR -> SupervisorFAB()
        UserRole.OFFICER -> OfficerFAB()
        else -> { /* No FAB for VIEWER */
        }
    }
}

@Composable
private fun AdminFAB() {
    val navigator = LocalNavigator.currentOrThrow

    ExtendedFloatingActionButton(
        onClick = { /* Quick action menu */ },
        containerColor = SafetyOrange,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("New") }
    )
}

@Composable
private fun SupervisorFAB() {
    ExtendedFloatingActionButton(
        onClick = { /* Create inspection */ },
        containerColor = SafetyBlue,
        icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
        text = { Text("Inspection") }
    )
}

@Composable
private fun OfficerFAB() {
    FloatingActionButton(
        onClick = { /* Report hazard */ },
        containerColor = SafetyRed
    ) {
        Icon(Icons.Default.Warning, contentDescription = "Report Hazard")
    }
}

// ==================== TABS ====================

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
        val currentUser = LocalCurrentUser.current

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
                // Role-based welcome message
                item {
                    RoleWelcomeCard(currentUser)
                }

                item {
                    SafetyScoreCard(safetyScore)
                }

                // Quick Actions - Role based
                item {
                    QuickActionsSection()
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

                // Only show hazards to users who can manage them
                WithHazardManagementPermission {
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
}

@Composable
private fun RoleWelcomeCard(user: User) {
    val (color, icon) = when (user.primaryRole) {
        UserRole.SUPER_ADMIN -> Pair(SafetyRed, Icons.Default.Security)
        UserRole.ADMIN -> Pair(SafetyOrange, Icons.Default.AdminPanelSettings)
        UserRole.SUPERVISOR -> Pair(SafetyBlue, Icons.Default.SupervisorAccount)
        UserRole.OFFICER -> Pair(SafetyGreen, Icons.Default.Engineering)
        UserRole.VIEWER -> Pair(MaterialTheme.colorScheme.primary, Icons.Default.Visibility)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Welcome, ${user.firstName ?: user.email}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Role: ${user.primaryRole.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection() {
    val currentUser = LocalCurrentUser.current

    Column {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // New Inspection - Only for users who can create
            WithInspectionCreationPermission {
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    label = "Inspection",
                    color = SafetyBlue,
                    onClick = { /* Navigate */ },
                    modifier = Modifier.weight(1f)
                )
            }

            // Report Hazard - Only for users who can manage hazards
            WithHazardManagementPermission {
                QuickActionButton(
                    icon = Icons.Default.Warning,
                    label = "Hazard",
                    color = SafetyRed,
                    onClick = { /* Navigate */ },
                    modifier = Modifier.weight(1f)
                )
            }

            // View Reports - All users
            QuickActionButton(
                icon = Icons.Default.Assessment,
                label = "Reports",
                color = SafetyGreen,
                onClick = { /* Navigate */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
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
        val currentUser = LocalCurrentUser.current

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hazards List")
                Spacer(modifier = Modifier.height(8.dp))
                // Show different message based on role
                when (currentUser.primaryRole) {
                    UserRole.OFFICER -> Text(
                        "You can report new hazards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.SUPER_ADMIN -> Text(
                        "Manage and assign hazards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    else -> {}
                }
            }
        }
    }
}

object EquipmentTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 3u, title = "Equipment")

    @Composable
    override fun Content() {
        WithEquipmentManagementPermission {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Equipment Management")
            }
        }
    }
}

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(index = 4u, title = "Profile")

    @Composable
    override fun Content() {
        val currentUser = LocalCurrentUser.current

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                ProfileHeader(currentUser)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                ProfileInfoCard(currentUser)
            }

            // Admin panel access in profile for admin users
            item {
                WithAdminPanelAccess {
                    Spacer(modifier = Modifier.height(16.dp))
                    AdminAccessCard()
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    val color = when (user.primaryRole) {
        UserRole.SUPER_ADMIN -> SafetyRed
        UserRole.ADMIN -> SafetyOrange
        UserRole.SUPERVISOR -> SafetyBlue
        UserRole.OFFICER -> SafetyGreen
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.firstName?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                user.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    user.primaryRole.name.replace("_", " "),
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(user: User) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileInfoRow("Tenant", user.tenantName ?: "N/A")
            ProfileInfoRow("Mine/Site", user.mineName ?: "N/A")
            ProfileInfoRow("User ID", user.id.toString())
            ProfileInfoRow("Status", if (user.enabled) "Active" else "Inactive")
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AdminAccessCard() {
    val navigator = LocalNavigator.currentOrThrow

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { navigator.push(AdminDashboardScreen()) },
        colors = CardDefaults.cardColors(
            containerColor = SafetyOrange.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = SafetyOrange
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Admin Panel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Manage users, settings, and system",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== UI Components ====================

@Composable
private fun SafetyScoreCard(score: SafetyScore?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Safety Score", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = score?.overallScore?.toString() ?: "--",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "/100",
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
