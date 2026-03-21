package com.zama.safeops.frontend.presentation.screens.admin

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.zama.safeops.frontend.domain.model.UserRole
import com.zama.safeops.frontend.presentation.rbac.LocalCurrentUser
import com.zama.safeops.frontend.presentation.rbac.WithAdminPanelAccess
import com.zama.safeops.frontend.presentation.rbac.WithTenantManagementPermission
import com.zama.safeops.frontend.presentation.screens.auth.AuthViewModel
import com.zama.safeops.frontend.presentation.screens.auth.LoginScreen
import com.zama.safeops.frontend.presentation.theme.SafetyBlue
import com.zama.safeops.frontend.presentation.theme.SafetyGreen
import com.zama.safeops.frontend.presentation.theme.SafetyOrange
import com.zama.safeops.frontend.presentation.theme.SafetyRed

/**
 * Admin Dashboard - Only accessible by SUPER_ADMIN and ADMIN roles
 */
class AdminDashboardScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authViewModel = koinScreenModel<AuthViewModel>()
        val currentUser = LocalCurrentUser.current

        // Only allow access to admin users
        WithAdminPanelAccess {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text("Admin Dashboard")
                                Text(
                                    currentUser.tenantName ?: "System Admin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                authViewModel.logout()
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
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User Role Card
                    item {
                        RoleInfoCard(currentUser.primaryRole)
                    }

                    // Quick Stats
                    item {
                        AdminStatsSection()
                    }

                    // Admin Actions Grid
                    item {
                        Text(
                            "Administration",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        AdminActionsGrid(navigator)
                    }

                    // Super Admin Only Section
                    if (currentUser.hasRole(UserRole.SUPER_ADMIN)) {
                        item {
                            Text(
                                "Super Admin",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            SuperAdminSection(navigator)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleInfoCard(role: UserRole) {
    val (color, icon, description) = when (role) {
        UserRole.SUPER_ADMIN -> Triple(SafetyRed, Icons.Default.Security, "Full system access")
        UserRole.ADMIN -> Triple(SafetyOrange, Icons.Default.AdminPanelSettings, "Tenant administrator")
        else -> Triple(SafetyBlue, Icons.Default.Person, "Standard user")
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
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    role.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminStatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Users",
            value = "24",
            icon = Icons.Default.People,
            color = SafetyBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Mines",
            value = "5",
            icon = Icons.Default.LocationOn,
            color = SafetyGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Inspections",
            value = "142",
            icon = Icons.Default.Assignment,
            color = SafetyOrange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AdminActionsGrid(navigator: Any) {
    val actions = listOf(
        AdminAction(
            "Users",
            Icons.Default.People,
            SafetyBlue,
            "Manage users and permissions"
        ),
        AdminAction(
            "Mines & Sites",
            Icons.Default.LocationOn,
            SafetyGreen,
            "Configure locations"
        ),
        AdminAction(
            "Inspections",
            Icons.Default.Assignment,
            SafetyOrange,
            "Review all inspections"
        ),
        AdminAction(
            "Hazards",
            Icons.Default.Warning,
            SafetyRed,
            "Manage hazards"
        ),
        AdminAction(
            "Reports",
            Icons.Default.Assessment,
            SafetyBlue,
            "View analytics"
        ),
        AdminAction(
            "Settings",
            Icons.Default.Settings,
            MaterialTheme.colorScheme.primary,
            "System settings"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(280.dp)
    ) {
        items(actions) { action ->
            AdminActionCard(action)
        }
    }
}

@Composable
private fun SuperAdminSection(navigator: Any) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SuperAdminCard(
            title = "Tenant Management",
            description = "Manage organizations and tenants",
            icon = Icons.Default.Business,
            onClick = { /* Navigate to tenant management */ }
        )

        SuperAdminCard(
            title = "System Configuration",
            description = "Global system settings",
            icon = Icons.Default.Build,
            onClick = { /* Navigate to system config */ }
        )

        SuperAdminCard(
            title = "Audit Logs",
            description = "View system activity logs",
            icon = Icons.Default.History,
            onClick = { /* Navigate to audit logs */ }
        )
    }
}

@Composable
private fun SuperAdminCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
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
                tint = SafetyRed,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    description,
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

data class AdminAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

@Composable
private fun AdminActionCard(action: AdminAction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = { /* Navigate to action */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                action.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
