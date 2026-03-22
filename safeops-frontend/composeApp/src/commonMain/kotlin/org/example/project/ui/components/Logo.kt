package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.logo_dark
import kotlinproject.composeapp.generated.resources.logo_white
import org.jetbrains.compose.resources.painterResource

/**
 * Logo variant based on background color
 */
enum class LogoVariant {
    DARK,   // For light/white backgrounds
    WHITE   // For dark backgrounds
}

/**
 * SafeOps Logo component that automatically selects the appropriate logo
 * based on the background color.
 *
 * @param variant LogoVariant.DARK for light backgrounds, LogoVariant.WHITE for dark backgrounds
 * @param modifier Modifier for styling
 * @param size Size of the logo in dp
 */
@Composable
fun SafeOpsLogo(
    variant: LogoVariant = LogoVariant.DARK,
    modifier: Modifier = Modifier,
    size: Int = 120
) {
    val painter = when (variant) {
        LogoVariant.DARK -> painterResource(Res.drawable.logo_dark)
        LogoVariant.WHITE -> painterResource(Res.drawable.logo_white)
    }
    
    Image(
        painter = painter,
        contentDescription = "SafeOps Logo",
        modifier = modifier.size(size.dp)
    )
}

/**
 * Convenience function to get logo variant based on background brightness.
 * Returns WHITE logo for dark backgrounds, DARK logo for light backgrounds.
 *
 * @param isDarkBackground true if background is dark
 * @return appropriate LogoVariant
 */
fun getLogoForBackground(isDarkBackground: Boolean): LogoVariant {
    return if (isDarkBackground) LogoVariant.WHITE else LogoVariant.DARK
}
