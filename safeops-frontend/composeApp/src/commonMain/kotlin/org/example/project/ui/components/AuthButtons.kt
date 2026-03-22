package org.example.project.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.theme.MiningSafetyColors

/**
 * Primary action button for auth screens with loading state
 *
 * @param text Button text
 * @param onClick Click handler
 * @param isLoading Whether to show loading spinner
 * @param enabled Whether button is enabled
 * @param modifier Modifier for styling
 */
@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MiningSafetyColors.Primary
        ),
        enabled = enabled && !isLoading
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.height(24.dp),
                color = MiningSafetyColors.OnPrimary,
                strokeWidth = 2.dp
            )
        }
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Secondary button for auth screens (e.g., Sign Up)
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for styling
 */
@Composable
fun AuthSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MiningSafetyColors.Secondary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Text link button for auth screens (e.g., Forgot Password)
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for styling
 */
@Composable
fun AuthTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MiningSafetyColors.Primary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Divider with "or" text for auth screens
 *
 * @param modifier Modifier for styling
 */
@Composable
fun AuthDivider(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MiningSafetyColors.Outline
        )
        Text(
            text = "  or  ",
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurfaceVariant
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MiningSafetyColors.Outline
        )
    }
}

/**
 * Footer link row (e.g., "Don't have an account? Sign Up")
 *
 * @param questionText Question text (e.g., "Don't have an account?")
 * @param actionText Action text (e.g., "Sign Up")
 * @param onActionClick Click handler for action
 * @param modifier Modifier for styling
 */
@Composable
fun AuthFooterLink(
    questionText: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MiningSafetyColors.OnSurfaceVariant
        )
        TextButton(onClick = onActionClick) {
            Text(
                text = actionText,
                color = MiningSafetyColors.Secondary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
