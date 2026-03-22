package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Generic data table component
 *
 * @param columns List of column headers
 * @param modifier Modifier for styling
 * @param content Table rows content
 */
@Composable
fun DataTable(
    columns: List<String>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MiningSafetyColors.SurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelSmall,
                        color = MiningSafetyColors.OnSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            HorizontalDivider(color = MiningSafetyColors.OutlineVariant)
            
            // Content rows
            content()
        }
    }
}

/**
 * Individual table row
 *
 * @param onClick Callback when row is clicked
 * @param modifier Modifier for styling
 * @param content Row content
 */
@Composable
fun TableRow(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
        HorizontalDivider(
            color = MiningSafetyColors.OutlineVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Table cell with text content
 *
 * @param text Text to display
 * @param modifier Modifier for styling
 * @param weight Weight for column sizing
 * @param color Text color
 * @param fontWeight Font weight
 */
@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    weight: Float = 1f,
    color: Color = MiningSafetyColors.OnSurface,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier.weight(weight)
    )
}

/**
 * Table cell with a status badge
 *
 * @param status Status text
 * @param statusColor Color for the badge
 * @param modifier Modifier for styling
 * @param weight Weight for column sizing
 */
@Composable
fun RowScope.TableStatusCell(
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier,
    weight: Float = 1f
) {
    Box(
        modifier = modifier.weight(weight),
        contentAlignment = Alignment.CenterStart
    ) {
        StatusBadge(status = status, color = statusColor)
    }
}

/**
 * Table cell with score and optional color coding
 *
 * @param score Score value (e.g., "94%")
 * @param modifier Modifier for styling
 * @param weight Weight for column sizing
 */
@Composable
fun RowScope.TableScoreCell(
    score: String?,
    modifier: Modifier = Modifier,
    weight: Float = 1f
) {
    val color = when {
        score == null -> MiningSafetyColors.OnSurfaceVariant
        score.replace("%", "").toIntOrNull()?.let { it >= 80 } == true -> MiningSafetyColors.Success
        score.replace("%", "").toIntOrNull()?.let { it >= 60 } == true -> MiningSafetyColors.Warning
        else -> MiningSafetyColors.Error
    }
    
    Text(
        text = score ?: "—",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.weight(weight)
    )
}

/**
 * Empty table state
 *
 * @param message Message to display
 * @param modifier Modifier for styling
 */
@Composable
fun EmptyTableState(
    message: String = "No data available",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📭",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}

/**
 * Action button cell for tables
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for styling
 * @param weight Weight for column sizing
 */
@Composable
fun RowScope.TableActionCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    weight: Float = 1f
) {
    Box(
        modifier = modifier.weight(weight),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MiningSafetyColors.Primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
