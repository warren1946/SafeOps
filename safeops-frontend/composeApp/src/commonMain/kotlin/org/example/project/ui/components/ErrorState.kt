package org.example.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Full-screen error state with retry
 */
@Composable
fun FullScreenError(
    message: String = "Something went wrong",
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Error icon
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayLarge
            )
            
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MiningSafetyColors.OnBackground
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MiningSafetyColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiningSafetyColors.Primary
                    )
                ) {
                    Text("🔄 Try Again")
                }
            }
        }
    }
}

/**
 * Inline error message for use in lists/cards
 */
@Composable
fun InlineError(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Failed to load",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = MiningSafetyColors.Error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
        
        if (onRetry != null) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MiningSafetyColors.SurfaceVariant
                )
            ) {
                Text(
                    text = "Retry",
                    color = MiningSafetyColors.Primary
                )
            }
        }
    }
}

/**
 * Empty state when no data is available
 */
@Composable
fun EmptyState(
    icon: String = "📭",
    title: String = "No data found",
    message: String = "There's nothing to show here yet.",
    actionButton: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.displayMedium
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                color = MiningSafetyColors.OnBackground
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MiningSafetyColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            if (actionButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                actionButton()
            }
        }
    }
}

/**
 * Error snackbar/banner
 */
@Composable
fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Error.copy(alpha = 0.1f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MiningSafetyColors.Error,
                modifier = Modifier.weight(1f)
            )
            
            androidx.compose.material3.TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Dismiss",
                    color = MiningSafetyColors.Error
                )
            }
        }
    }
}
