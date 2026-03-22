package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.di.ServiceLocator
import org.example.project.domain.model.User
import org.example.project.domain.model.UserRole
import org.example.project.presentation.viewmodel.UsersViewModel
import org.example.project.ui.components.CardSkeleton
import org.example.project.ui.components.EmptyState
import org.example.project.ui.components.FullScreenError
import org.example.project.ui.components.InlineError
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Users screen with user management table - connected to ViewModel
 */
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = remember { ServiceLocator.provideUsersViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        UsersHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        UsersActionsRow(
            onRefresh = viewModel::refreshUsers
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on state
        when {
            uiState.isLoading && uiState.users.isEmpty() -> {
                // Show skeleton loaders
                repeat(4) {
                    CardSkeleton()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            uiState.error != null && uiState.users.isEmpty() -> {
                FullScreenError(
                    message = uiState.error ?: "Failed to load users",
                    onRetry = viewModel::refreshUsers
                )
            }
            uiState.users.isEmpty() -> {
                EmptyState(
                    icon = "👥",
                    title = "No users found",
                    message = "There are no users in the system yet. Add users to get started."
                )
            }
            else -> {
                // Show error banner if there's an error but we have cached data
                if (uiState.error != null) {
                    InlineError(
                        message = uiState.error ?: "Update failed",
                        onRetry = viewModel::refreshUsers,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Users table
                UsersTable(
                    users = uiState.users,
                    onUserClick = viewModel::selectUser
                )
            }
        }
    }
}

@Composable
private fun UsersHeader() {
    Text(
        text = "Users",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Manage team members and roles",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun UsersActionsRow(
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
            Text("🔍 Search", color = MiningSafetyColors.OnSurface)
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
                containerColor = MiningSafetyColors.Primary
            )
        ) {
            Text("➕ Add User")
        }
    }
}

@Composable
private fun UsersTable(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiningSafetyColors.SurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = "Role",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.8f)
                )
            }
            
            // Rows
            users.forEach { user ->
                UserRow(
                    user = user,
                    onClick = { onUserClick(user) }
                )
            }
        }
    }
}

@Composable
private fun UserRow(
    user: User,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name with avatar
        Row(
            modifier = Modifier.weight(1.5f),
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
                    text = user.email.take(2).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = user.email.substringBefore("@"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MiningSafetyColors.OnSurfaceVariant
                )
            }
        }
        
        // Role
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val roleDisplay = when (user.roles.firstOrNull()) {
                UserRole.ADMIN -> "👤 Admin"
                UserRole.SUPERVISOR -> "👷 Supervisor"
                UserRole.OFFICER -> "🦺 Officer"
                null -> "👤 Unknown"
            }
            Text(
                text = roleDisplay,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Status
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            StatusBadge(
                status = if (user.isActive) "Active" else "Inactive",
                color = if (user.isActive) MiningSafetyColors.Success else MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}
