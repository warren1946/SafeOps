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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * A card container for chart components with a title
 *
 * @param title The chart title
 * @param modifier Modifier for styling
 * @param content The chart content to display inside the card
 */
@Composable
fun ChartCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MiningSafetyColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MiningSafetyColors.OnSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

/**
 * A simple bar chart component
 *
 * @param data List of label to value pairs (value between 0.0 and 1.0)
 * @param barColor Color for the bars
 * @param modifier Modifier for styling
 */
@Composable
fun SimpleBarChart(
    data: List<Pair<String, Float>>,
    barColor: Color = MiningSafetyColors.Primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            BarChartItem(
                label = label,
                value = value,
                barColor = barColor
            )
        }
    }
}

/**
 * Individual bar in the bar chart
 */
@Composable
private fun BarChartItem(
    label: String,
    value: Float,
    barColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height((value * 100).dp)
                .background(
                    barColor,
                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

/**
 * A simple line chart placeholder component
 *
 * @param labels X-axis labels
 * @param modifier Modifier for styling
 */
@Composable
fun SimpleLineChartPlaceholder(
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MiningSafetyColors.OnSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Simple visual representation of a line chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    MiningSafetyColors.Success.copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📈 Line Chart",
                style = MaterialTheme.typography.bodyMedium,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}
