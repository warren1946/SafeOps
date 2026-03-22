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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.DataTable
import org.example.project.ui.components.StatusBadge
import org.example.project.ui.components.TableCell
import org.example.project.ui.components.TableRow
import org.example.project.ui.components.TableStatusCell
import org.example.project.ui.theme.MiningSafetyColors

/**
 * User role enum
 */
enum class UserRole(val label: String, val icon: String) {
    ADMIN("Admin", "👤"),
    SAFETY_OFFICER("Safety Officer", "🦺"),
    MANAGER("Manager", "📊"),
    SUPERVISOR("Supervisor", "👷")
}

/**
 * User status enum
 */
enum class UserStatus(val label: String, val color: Color) {
    ACTIVE("Active", MiningSafetyColors.Success),
    INACTIVE("Inactive", MiningSafetyColors.OnSurfaceVariant)
}

/**
 * Data class for user
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String,
    val status: UserStatus
)

/**
 * Users screen with user management table
 */
@Composable
fun UsersScreen() {
    val users = rememberUsersData()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        UsersHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Actions
        UsersActionsRow()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Users table
        UsersTable(users = users)
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
private fun UsersActionsRow() {
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
private fun UsersTable(users: List<User>) {
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
                    text = "Name",
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
                    text = "Phone",
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
                UserRow(user = user)
            }
        }
    }
}

@Composable
private fun UserRow(user: User) {
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
                    text = user.name.take(2).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = user.name,
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
            Text(
                text = user.role.icon + " " + user.role.label,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Phone
        Text(
            text = user.phone,
            style = MaterialTheme.typography.bodySmall,
            color = MiningSafetyColors.OnSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        
        // Status
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            StatusBadge(
                status = user.status.label,
                color = user.status.color
            )
        }
    }
}

@Composable
private fun rememberUsersData(): List<User> {
    return listOf(
        User(
            id = "USR-001",
            name = "John Doe",
            email = "john@safeops.io",
            role = UserRole.ADMIN,
            phone = "+27 82 123 4567",
            status = UserStatus.ACTIVE
        ),
        User(
            id = "USR-002",
            name = "Mike Johnson",
            email = "mike@safeops.io",
            role = UserRole.SAFETY_OFFICER,
            phone = "+27 83 234 5678",
            status = UserStatus.ACTIVE
        ),
        User(
            id = "USR-003",
            name = "Sarah Williams",
            email = "sarah@safeops.io",
            role = UserRole.SAFETY_OFFICER,
            phone = "+27 84 345 6789",
            status = UserStatus.ACTIVE
        ),
        User(
            id = "USR-004",
            name = "Carlos Rivera",
            email = "carlos@safeops.io",
            role = UserRole.SAFETY_OFFICER,
            phone = "+27 85 456 7890",
            status = UserStatus.ACTIVE
        ),
        User(
            id = "USR-005",
            name = "Aisha Patel",
            email = "aisha@safeops.io",
            role = UserRole.MANAGER,
            phone = "+27 86 567 8901",
            status = UserStatus.ACTIVE
        ),
        User(
            id = "USR-006",
            name = "Tom Baker",
            email = "tom@safeops.io",
            role = UserRole.SAFETY_OFFICER,
            phone = "+27 87 678 9012",
            status = UserStatus.INACTIVE
        )
    )
}
