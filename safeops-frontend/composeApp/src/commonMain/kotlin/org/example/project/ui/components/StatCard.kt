package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
 * A card component displaying a statistic with an icon, value, and subtitle
 *
 * @param title The statistic label (e.g., "TODAY'S INSPECTIONS")
 * @param value The main value to display (e.g., "12", "93%")
 * @param subtitle Additional context text (e.g., "+3 from yesterday")
 * @param subtitleColor Color for the subtitle text
 * @param icon Emoji icon to display
 * @param iconBackground Background color for the icon container
 * @param modifier Modifier for styling
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: String,
    iconBackground: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCardContent(
                    title = title,
                    value = value,
                    subtitle = subtitle,
                    subtitleColor = subtitleColor
                )
                
                StatCardIcon(
                    icon = icon,
                    backgroundColor = iconBackground
                )
            }
        }
    }
}

/**
 * Content section of the StatCard (title, value, subtitle)
 */
@Composable
private fun StatCardContent(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MiningSafetyColors.OnBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = subtitleColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Icon section of the StatCard
 */
@Composable
private fun StatCardIcon(
    icon: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
