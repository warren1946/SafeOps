package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.screens.dashboard.DashboardTab
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Data class representing a sidebar navigation item
 */
data class SidebarItemData(
    val icon: String,
    val label: String,
    val tab: DashboardTab
)

/**
 * Sidebar navigation component for the dashboard
 *
 * @param selectedTab Currently selected tab
 * @param onTabSelected Callback when a tab is selected
 * @param onLogout Callback when logout is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun Sidebar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sidebarColor = Color(0xFF1E293B) // Dark slate
    
    val menuItems = listOf(
        SidebarItemData("📊", "Dashboard", DashboardTab.DASHBOARD),
        SidebarItemData("📋", "Inspections", DashboardTab.INSPECTIONS),
        SidebarItemData("⚠️", "Hazards", DashboardTab.HAZARDS),
        SidebarItemData("📈", "Reports", DashboardTab.REPORTS),
        SidebarItemData("📄", "Templates", DashboardTab.TEMPLATES),
        SidebarItemData("💬", "WhatsApp", DashboardTab.WHATSAPP),
        SidebarItemData("👥", "Users", DashboardTab.USERS),
        SidebarItemData("⚙️", "Settings", DashboardTab.SETTINGS)
    )
    
    Column(
        modifier = modifier
            .width(240.dp)
            .fillMaxHeight()
            .background(sidebarColor)
            .padding(vertical = 16.dp)
    ) {
        // Logo Section
        SidebarLogo()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation Items
        menuItems.forEach { item ->
            SidebarItem(
                icon = item.icon,
                label = item.label,
                isSelected = selectedTab == item.tab,
                onClick = { onTabSelected(item.tab) }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout
        SidebarItem(
            icon = "🚪",
            label = "Logout",
            isSelected = false,
            onClick = onLogout
        )
    }
}

/**
 * Logo section for the sidebar
 */
@Composable
private fun SidebarLogo() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "SafeOps",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "SAFETY PLATFORM",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8)
        )
    }
}

/**
 * Individual sidebar item component
 *
 * @param icon Emoji icon for the item
 * @param label Text label
 * @param isSelected Whether this item is currently selected
 * @param onClick Callback when clicked
 */
@Composable
fun SidebarItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) 
        MiningSafetyColors.Primary.copy(alpha = 0.2f) 
    else 
        Color.Transparent
    
    val contentColor = if (isSelected) 
        MiningSafetyColors.PrimaryLight 
    else 
        Color(0xFF94A3B8)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
