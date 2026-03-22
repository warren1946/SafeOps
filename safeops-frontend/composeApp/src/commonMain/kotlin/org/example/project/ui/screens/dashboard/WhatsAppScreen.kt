package org.example.project.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Chat message data class
 */
data class ChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: String,
    val isUser: Boolean = false,
    val isSystem: Boolean = false
)

/**
 * WhatsApp integration screen
 */
@Composable
fun WhatsAppScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        WhatsAppHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Chat preview
        ChatPreviewCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // How it works
        HowItWorksSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick keywords
        QuickKeywordsSection()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Active officers
        ActiveOfficersSection()
    }
}

@Composable
private fun WhatsAppHeader() {
    Text(
        text = "WhatsApp Integration",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Monitor WhatsApp inspection workflows",
        style = MaterialTheme.typography.bodyMedium,
        color = MiningSafetyColors.OnSurfaceVariant
    )
}

@Composable
private fun ChatPreviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0FDF4) // Light green background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Chat header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bot avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF25D366)), // WhatsApp green
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "SafeOps Bot",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Inspection workflow - Live preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = MiningSafetyColors.OnSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chat messages
            ChatMessageBubble(
                message = ChatMessage(
                    id = "1",
                    sender = "Mike Johnson",
                    message = "START INSPECTION",
                    timestamp = "08:15 AM",
                    isUser = true
                )
            )
            
            ChatMessageBubble(
                message = ChatMessage(
                    id = "2",
                    sender = "System",
                    message = "Welcome Mike! Starting inspection for Shaft A - Level 3. Question 1/12: Are all emergency exits clearly marked? (Yes/No)",
                    timestamp = "08:15 AM",
                    isSystem = true
                )
            )
            
            ChatMessageBubble(
                message = ChatMessage(
                    id = "3",
                    sender = "Mike Johnson",
                    message = "Yes",
                    timestamp = "08:16 AM",
                    isUser = true
                )
            )
            
            ChatMessageBubble(
                message = ChatMessage(
                    id = "4",
                    sender = "System",
                    message = "✅ Noted. Question 2/12: Is ventilation equipment operational? (Yes/No)",
                    timestamp = "08:16 AM",
                    isSystem = true
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Input hint
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Text(
                    text = "Preview only — messages come from WhatsApp",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MiningSafetyColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = when {
        message.isUser -> Color(0xFFDCF8C6) // WhatsApp light green
        message.isSystem -> Color.White
        else -> Color.White
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!message.isUser) {
            Text(
                text = "🤖 System",
                style = MaterialTheme.typography.labelSmall,
                color = MiningSafetyColors.OnSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
        
        Surface(
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 12.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 12.dp,
                bottomStart = 12.dp,
                bottomEnd = 12.dp
            ),
            color = backgroundColor,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HowItWorksSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "How It Works",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            StepItem("1", "Officer sends START INSPECTION via WhatsApp")
            StepItem("2", "Bot guides through checklist questions")
            StepItem("3", "Photos and severity ratings captured")
            StepItem("4", "GPS location auto-attached")
            StepItem("5", "Data syncs to dashboard in real-time")
        }
    }
}

@Composable
private fun StepItem(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MiningSafetyColors.Primary.copy(alpha = 0.1f)
        ) {
            Text(
                text = number,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MiningSafetyColors.Primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun QuickKeywordsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Keywords",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KeywordChip("START INSPECTION")
                KeywordChip("REPORT HAZARD")
                KeywordChip("STATUS")
                KeywordChip("HELP")
                KeywordChip("EMERGENCY")
            }
        }
    }
}

@Composable
private fun KeywordChip(keyword: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MiningSafetyColors.SurfaceVariant
    ) {
        Text(
            text = keyword,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun ActiveOfficersSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Active Officers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ActiveOfficerItem("Mike Johnson")
            ActiveOfficerItem("Sarah Williams")
            ActiveOfficerItem("Carlos Rivera")
        }
    }
}

@Composable
private fun ActiveOfficerItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Online indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF22C55E))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Simple FlowRow implementation
 */
@Composable
private fun FlowRow(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Simplified - just use Column with wrapping for now
    Column(
        verticalArrangement = verticalArrangement
    ) {
        Row(
            horizontalArrangement = horizontalArrangement
        ) {
            content()
        }
    }
}
