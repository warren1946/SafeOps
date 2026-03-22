package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Layout configuration for auth screens
 */
data class AuthLayoutConfig(
    val maxContentWidth: Int = 1200,
    val cardMaxWidth: Int = 480,
    val padding: Int = 48,
    val cardPadding: Int = 48,
    val showSidePanel: Boolean = true
)

/**
 * A web-optimized layout for authentication screens
 *
 * @param title Main title (e.g., "Welcome Back")
 * @param subtitle Subtitle text
 * @param emoji Icon emoji to display
 * @param config Layout configuration
 * @param footer Optional footer content (e.g., sign up link)
 * @param content Main form content
 */
@Composable
fun AuthLayout(
    title: String,
    subtitle: String,
    emoji: String = "⚡",
    config: AuthLayoutConfig = AuthLayoutConfig(),
    footer: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MiningSafetyColors.Primary,
                        MiningSafetyColors.PrimaryDark
                    )
                )
            )
    ) {
        // Left side - Branding panel (for wider screens)
        if (config.showSidePanel) {
            AuthSidePanel(
                emoji = emoji,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Right side - Auth form
        AuthFormContainer(
            title = title,
            subtitle = subtitle,
            config = config,
            footer = footer,
            content = content
        )
    }
}

/**
 * Side panel with branding for larger screens
 */
@Composable
private fun AuthSidePanel(
    emoji: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large emoji/icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MiningSafetyColors.Secondary)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displayLarge
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "SafeOps",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Mining Safety Management Platform",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feature bullets
        FeatureBullet("✓", "MSHA & OSHA Compliant")
        FeatureBullet("✓", "Real-time Safety Monitoring")
        FeatureBullet("✓", "WhatsApp Integration")
        FeatureBullet("✓", "Advanced Analytics")
    }
}

/**
 * Feature bullet point for side panel
 */
@Composable
private fun FeatureBullet(icon: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

/**
 * Container for the authentication form
 */
@Composable
private fun AuthFormContainer(
    title: String,
    subtitle: String,
    config: AuthLayoutConfig,
    footer: @Composable (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(max = config.cardMaxWidth.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(config.padding.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Auth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MiningSafetyColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(config.cardPadding.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MiningSafetyColors.OnBackground,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Form Content
                content()
            }
        }
        
        // Footer
        footer?.let {
            Spacer(modifier = Modifier.height(24.dp))
            it()
        }
    }
}

/**
 * A compact auth layout for mobile/smaller screens
 *
 * @param title Main title
 * @param subtitle Subtitle text
 * @param emoji Icon emoji
 * @param footer Optional footer
 * @param content Main form content
 */
@Composable
fun AuthLayoutCompact(
    title: String,
    subtitle: String,
    emoji: String = "⚡",
    footer: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MiningSafetyColors.Primary,
                        MiningSafetyColors.PrimaryDark
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MiningSafetyColors.Secondary)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "SafeOps",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Mining Safety Management",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Auth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MiningSafetyColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MiningSafetyColors.OnBackground,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiningSafetyColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                content()
            }
        }
        
        footer?.let {
            Spacer(modifier = Modifier.height(16.dp))
            it()
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
