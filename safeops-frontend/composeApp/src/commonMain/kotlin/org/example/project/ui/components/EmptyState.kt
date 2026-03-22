package org.example.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * An empty state component for displaying placeholder content
 *
 * @param title The main title to display
 * @param subtitle Optional subtitle/description
 * @param emoji Emoji to display as the icon
 * @param modifier Modifier for styling
 */
@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    emoji: String = "🚧",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MiningSafetyColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A coming soon placeholder for unfinished features
 *
 * @param featureName Name of the feature (e.g., "Reports", "Settings")
 * @param description Optional description of the feature
 * @param modifier Modifier for styling
 */
@Composable
fun ComingSoonPlaceholder(
    featureName: String,
    description: String? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = "$featureName - Coming Soon",
        subtitle = description ?: "This feature is under development",
        emoji = "🚧",
        modifier = modifier
    )
}

/**
 * An empty list state for when no items are available
 *
 * @param message Message to display (e.g., "No inspections found")
 * @param suggestion Optional suggestion text (e.g., "Create your first inspection")
 * @param modifier Modifier for styling
 */
@Composable
fun EmptyListState(
    message: String,
    suggestion: String? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = message,
        subtitle = suggestion,
        emoji = "📭",
        modifier = modifier
    )
}

/**
 * An error state component
 *
 * @param message Error message to display
 * @param suggestion Optional suggestion for resolving the error
 * @param modifier Modifier for styling
 */
@Composable
fun ErrorState(
    message: String,
    suggestion: String? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        title = message,
        subtitle = suggestion,
        emoji = "⚠️",
        modifier = modifier
    )
}
