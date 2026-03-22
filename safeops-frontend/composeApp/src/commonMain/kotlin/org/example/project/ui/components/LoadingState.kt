package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Full-screen loading state
 */
@Composable
fun FullScreenLoading(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MiningSafetyColors.Primary,
                strokeWidth = 4.dp
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MiningSafetyColors.OnSurfaceVariant
            )
        }
    }
}

/**
 * Inline loading indicator for use in lists/cards
 */
@Composable
fun InlineLoading(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MiningSafetyColors.Primary,
            strokeWidth = 2.dp
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurfaceVariant
        )
    }
}

/**
 * Skeleton loader for table rows
 */
@Composable
fun TableSkeletonRow(
    columns: Int = 6,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(columns) { index ->
            SkeletonBox(
                modifier = Modifier.weight(if (index == 1) 1.5f else 1f)
            )
        }
    }
}

/**
 * Skeleton box for loading placeholders
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(20.dp)
            .fillMaxWidth(0.8f)
            .shimmerEffect()
    )
}

/**
 * Card skeleton loader
 */
@Composable
fun CardSkeleton(
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shimmerEffect()
            )
            // Content placeholder
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.6f))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.4f))
            }
            // Badge placeholder
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .shimmerEffect()
            )
        }
    }
}

/**
 * Shimmer effect modifier
 */
@Composable
private fun Modifier.shimmerEffect(): Modifier {
    // Simple shimmer using gray background
    // In production, use animated shimmer
    return this.background(
        color = MiningSafetyColors.SurfaceVariant.copy(alpha = 0.5f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    )
}
