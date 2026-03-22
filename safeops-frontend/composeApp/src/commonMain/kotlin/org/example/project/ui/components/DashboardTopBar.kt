package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Top app bar component for the dashboard
 *
 * @param searchQuery Current search query value
 * @param onSearchQueryChange Callback when search query changes
 * @param notificationCount Number of notifications to display on badge
 * @param userInitials User initials for the avatar
 * @param userName Full user name
 * @param userRole User role (e.g., "Admin", "Manager")
 * @param onNotificationsClick Callback when notification icon is clicked
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    notificationCount: Int = 0,
    userInitials: String = "JD",
    userName: String = "John Doe",
    userRole: String = "Admin",
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            SearchField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
        },
        actions = {
            NotificationIcon(
                count = notificationCount,
                onClick = onNotificationsClick
            )
            UserProfile(
                initials = userInitials,
                name = userName,
                role = userRole
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MiningSafetyColors.Background
        ),
        modifier = modifier
    )
}

/**
 * Search input field component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("🔍 Search inspections, hazards...") },
        modifier = Modifier
            .width(300.dp)
            .height(44.dp),
        shape = RoundedCornerShape(22.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MiningSafetyColors.SurfaceVariant,
            unfocusedContainerColor = MiningSafetyColors.SurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

/**
 * Notification icon with badge
 */
@Composable
private fun NotificationIcon(
    count: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        if (count > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MiningSafetyColors.Error
                    ) {
                        Text(
                            text = count.toString(), 
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            ) {
                Text(
                    text = "🔔",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            Text(
                text = "🔔",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * User profile display component
 */
@Composable
private fun UserProfile(
    initials: String,
    name: String,
    role: String
) {
    Row(
        modifier = Modifier.padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MiningSafetyColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // User info
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = role,
                style = MaterialTheme.typography.labelSmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}
