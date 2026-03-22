package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.di.ServiceLocator
import org.example.project.presentation.viewmodel.DashboardViewModel
import org.example.project.ui.components.*
import org.example.project.ui.components.LogoVariant
import org.example.project.ui.components.SafeOpsLogo
import org.example.project.domain.model.DashboardStatistics
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Dashboard tabs/navigation items
 */
enum class DashboardTab {
    DASHBOARD, INSPECTIONS, HAZARDS, REPORTS, TEMPLATES, WHATSAPP, USERS, SETTINGS
}

/**
 * Data class for bottom nav item
 */
data class BottomNavItem(
    val icon: String,
    val label: String,
    val tab: DashboardTab
)

/**
 * Main dashboard screen with responsive navigation
 *
 * @param viewModel DashboardViewModel instance
 * @param onLogout Callback when user logs out
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = remember { ServiceLocator.provideDashboardViewModel() },
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(DashboardTab.DASHBOARD) }
    
    // Handle logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
            viewModel.resetState()
        }
    }
    
    // Responsive layout
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxWidth = maxWidth
        val isCompact = maxWidth < 600.dp  // Phone - use bottom nav
        
        if (isCompact) {
            // Mobile layout with bottom navigation
            DashboardMobileLayout(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onLogout = { viewModel.logout() },
                statistics = uiState.statistics
            )
        } else {
            // Desktop/Tablet layout with sidebar
            DashboardDesktopLayout(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onLogout = { viewModel.logout() },
                statistics = uiState.statistics
            )
        }
    }
}

/**
 * Mobile layout with bottom navigation bar
 */
@Composable
private fun DashboardMobileLayout(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    onLogout: () -> Unit,
    statistics: DashboardStatistics?
) {
    val bottomNavItems = listOf(
        BottomNavItem("📊", "Home", DashboardTab.DASHBOARD),
        BottomNavItem("📋", "Inspect", DashboardTab.INSPECTIONS),
        BottomNavItem("⚠️", "Hazards", DashboardTab.HAZARDS),
        BottomNavItem("📈", "Reports", DashboardTab.REPORTS),
        BottomNavItem("👤", "More", DashboardTab.USERS)
    )
    
    Scaffold(
        topBar = {
            DashboardTopBarMobile(
                onMenuClick = { /* TODO: Open drawer */ }
            )
        },
        bottomBar = {
            DashboardBottomBar(
                items = bottomNavItems,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = MiningSafetyColors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            DashboardTabContent(
                selectedTab = selectedTab,
                statistics = statistics,
                onLogout = onLogout
            )
        }
    }
}

/**
 * Desktop layout with sidebar
 */
@Composable
private fun DashboardDesktopLayout(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    onLogout: () -> Unit,
    statistics: DashboardStatistics?
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Sidebar Navigation
        Sidebar(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onLogout = onLogout
        )
        
        // Main Content Area
        Scaffold(
            topBar = {
                DashboardTopBar(
                    notificationCount = 4,
                    userInitials = "JD",
                    userName = "John Doe",
                    userRole = "Admin"
                )
            },
            containerColor = MiningSafetyColors.Background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                DashboardTabContent(
                    selectedTab = selectedTab,
                    statistics = statistics,
                    onLogout = onLogout
                )
            }
        }
    }
}

/**
 * Mobile top bar with simplified design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBarMobile(
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                SafeOpsLogo(
                    variant = LogoVariant.DARK,
                    size = 32
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SafeOps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            // Notifications
            IconButton(onClick = {}) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MiningSafetyColors.Error
                        ) {
                            Text("4", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                ) {
                    Text(
                        text = "🔔",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            // Profile
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MiningSafetyColors.Primary)
                    .padding(end = 8.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "JD",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MiningSafetyColors.Surface
        )
    )
}

/**
 * Bottom navigation bar for mobile
 */
@Composable
private fun DashboardBottomBar(
    items: List<BottomNavItem>,
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    NavigationBar(
        containerColor = MiningSafetyColors.Surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = selectedTab == item.tab
            NavigationBarItem(
                icon = {
                    Text(
                        text = item.icon,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                label = { 
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    ) 
                },
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MiningSafetyColors.Primary,
                    selectedTextColor = MiningSafetyColors.Primary,
                    indicatorColor = MiningSafetyColors.Primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

/**
 * Content for each dashboard tab
 */
@Composable
private fun DashboardTabContent(
    selectedTab: DashboardTab,
    statistics: DashboardStatistics?,
    onLogout: () -> Unit
) {
    when (selectedTab) {
        DashboardTab.DASHBOARD -> DashboardContent(statistics = statistics)
        DashboardTab.INSPECTIONS -> InspectionsTab()
        DashboardTab.HAZARDS -> HazardsTab()
        DashboardTab.REPORTS -> ReportsTab()
        DashboardTab.TEMPLATES -> TemplatesTab()
        DashboardTab.WHATSAPP -> WhatsAppTab()
        DashboardTab.USERS -> UsersTab()
        DashboardTab.SETTINGS -> SettingsTab()
    }
}

// ==================== TAB CONTENT ====================

@Composable
private fun InspectionsTab() {
    InspectionsScreen()
}

@Composable
private fun HazardsTab() {
    HazardsScreen()
}

@Composable
private fun ReportsTab() {
    ReportsScreen()
}

@Composable
private fun TemplatesTab() {
    TemplatesScreen()
}

@Composable
private fun WhatsAppTab() {
    WhatsAppScreen()
}

@Composable
private fun UsersTab() {
    UsersScreen()
}

@Composable
private fun SettingsTab() {
    SettingsScreen()
}
