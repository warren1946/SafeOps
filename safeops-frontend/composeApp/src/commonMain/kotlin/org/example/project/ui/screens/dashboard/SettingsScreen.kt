package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Settings screen with configuration options
 */
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        SettingsHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Notifications
        NotificationsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Security
        SecuritySection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // WhatsApp API
        WhatsAppApiSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Data & Export
        DataExportSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // System
        SystemSection()
    }
}

@Composable
private fun SettingsHeader() {
    Text(
        text = "Settings",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Configure your SafeOps platform",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun NotificationsSection() {
    SettingsSectionCard(
        icon = "🔔",
        title = "Notifications",
        description = "Configure alert thresholds and notification channels"
    ) {
        var emailNotifications by remember { mutableStateOf(true) }
        var pushNotifications by remember { mutableStateOf(true) }
        var smsAlerts by remember { mutableStateOf(false) }
        
        SettingToggleItem(
            title = "Email Notifications",
            subtitle = "Receive daily summary emails",
            checked = emailNotifications,
            onCheckedChange = { emailNotifications = it }
        )
        
        SettingToggleItem(
            title = "Push Notifications",
            subtitle = "Real-time alerts on critical hazards",
            checked = pushNotifications,
            onCheckedChange = { pushNotifications = it }
        )
        
        SettingToggleItem(
            title = "SMS Alerts",
            subtitle = "Emergency notifications via SMS",
            checked = smsAlerts,
            onCheckedChange = { smsAlerts = it }
        )
    }
}

@Composable
private fun SecuritySection() {
    SettingsSectionCard(
        icon = "🔒",
        title = "Security",
        description = "Manage authentication, sessions, and access control"
    ) {
        var twoFactorAuth by remember { mutableStateOf(true) }
        var biometricLogin by remember { mutableStateOf(false) }
        
        SettingToggleItem(
            title = "Two-Factor Authentication",
            subtitle = "Require OTP for login",
            checked = twoFactorAuth,
            onCheckedChange = { twoFactorAuth = it }
        )
        
        SettingToggleItem(
            title = "Biometric Login",
            subtitle = "Use fingerprint/face recognition",
            checked = biometricLogin,
            onCheckedChange = { biometricLogin = it }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingActionItem(
            title = "Change Password",
            onClick = { }
        )
        
        SettingActionItem(
            title = "Manage API Keys",
            onClick = { }
        )
        
        SettingActionItem(
            title = "Active Sessions",
            onClick = { }
        )
    }
}

@Composable
private fun WhatsAppApiSection() {
    SettingsSectionCard(
        icon = "💬",
        title = "WhatsApp API",
        description = "Configure WhatsApp Business API connection"
    ) {
        SettingActionItem(
            title = "API Configuration",
            subtitle = "Connected",
            statusColor = MiningSafetyColors.Success,
            onClick = { }
        )
        
        SettingActionItem(
            title = "Bot Settings",
            subtitle = "Customize inspection workflow",
            onClick = { }
        )
        
        SettingActionItem(
            title = "Phone Numbers",
            subtitle = "3 numbers connected",
            onClick = { }
        )
    }
}

@Composable
private fun DataExportSection() {
    SettingsSectionCard(
        icon = "💾",
        title = "Data & Export",
        description = "Database backups, export settings, retention policies"
    ) {
        SettingActionItem(
            title = "Export Data",
            subtitle = "Download all inspection data",
            onClick = { }
        )
        
        SettingActionItem(
            title = "Backup Settings",
            subtitle = "Daily automated backups enabled",
            onClick = { }
        )
        
        SettingActionItem(
            title = "Data Retention",
            subtitle = "Retain data for 7 years",
            onClick = { }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MiningSafetyColors.SurfaceVariant
            )
        ) {
            Text("📥 Download All Data", color = MiningSafetyColors.OnSurface)
        }
    }
}

@Composable
private fun SystemSection() {
    SettingsSectionCard(
        icon = "⚙️",
        title = "System",
        description = "Platform settings and information"
    ) {
        SettingInfoItem(
            title = "Version",
            value = "v2.4.1"
        )
        
        SettingInfoItem(
            title = "Last Sync",
            value = "Just now"
        )
        
        SettingInfoItem(
            title = "Database Size",
            value = "124 MB"
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingActionItem(
            title = "Check for Updates",
            onClick = { }
        )
        
        SettingActionItem(
            title = "View Logs",
            onClick = { }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    icon: String,
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiningSafetyColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                }
                
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiningSafetyColors.SurfaceVariant
                    )
                ) {
                    Text(
                        "Configure",
                        color = MiningSafetyColors.OnSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content
            content()
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MiningSafetyColors.Primary,
                checkedTrackColor = MiningSafetyColors.Primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingActionItem(
    title: String,
    subtitle: String? = null,
    statusColor: androidx.compose.ui.graphics.Color = MiningSafetyColors.OnSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }
        }
        
        Text(
            text = "→",
            style = MaterialTheme.typography.bodyLarge,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun SettingInfoItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
