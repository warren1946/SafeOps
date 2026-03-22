package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Data class representing an inspection item
 */
data class InspectionData(
    val title: String,
    val officer: String,
    val date: String,
    val score: String?,
    val status: String,
    val statusColor: Color
)

/**
 * Data class representing a hazard item
 */
data class HazardData(
    val title: String,
    val location: String,
    val severity: String,
    val severityColor: Color
)

/**
 * List item component for displaying inspection data
 *
 * @param title Inspection location/title
 * @param officer Name of the inspecting officer
 * @param date Date of inspection
 * @param score Optional compliance score (null for in-progress)
 * @param status Status text (e.g., "Completed", "In-Progress", "Flagged")
 * @param statusColor Color representing the status
 * @param modifier Modifier for styling
 */
@Composable
fun InspectionListItem(
    title: String,
    officer: String,
    date: String,
    score: String?,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$officer · $date",
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            score?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            StatusBadge(status = status, color = statusColor)
        }
    }
}

/**
 * List item component for displaying hazard data
 *
 * @param title Hazard title/description
 * @param location Location where hazard was found
 * @param severity Severity level (e.g., "Critical", "High", "Medium", "Low")
 * @param severityColor Color representing the severity
 * @param modifier Modifier for styling
 */
@Composable
fun HazardListItem(
    title: String,
    location: String,
    severity: String,
    severityColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
        
        StatusBadge(status = severity, color = severityColor)
    }
}

/**
 * A reusable status badge component
 *
 * @param status Status text to display
 * @param color Color for the badge
 */
@Composable
fun StatusBadge(
    status: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Section header with optional "View all" action
 *
 * @param title Section title
 * @param onViewAll Callback for view all action (null hides the button)
 */
@Composable
fun SectionHeader(
    title: String,
    onViewAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MiningSafetyColors.OnSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        onViewAll?.let {
            TextButton(onClick = it) {
                Text(
                    text = "View all →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.Primary
                )
            }
        }
    }
}

/**
 * Simple text button component
 */
@Composable
private fun TextButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        content()
    }
}
