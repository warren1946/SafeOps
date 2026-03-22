package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MiningSafetyColors.Primary,
    onPrimary = MiningSafetyColors.OnPrimary,
    primaryContainer = MiningSafetyColors.PrimaryLight.copy(alpha = 0.1f),
    onPrimaryContainer = MiningSafetyColors.Primary,
    
    secondary = MiningSafetyColors.Secondary,
    onSecondary = MiningSafetyColors.OnSecondary,
    secondaryContainer = MiningSafetyColors.SecondaryLight.copy(alpha = 0.1f),
    onSecondaryContainer = MiningSafetyColors.SecondaryDark,
    
    background = MiningSafetyColors.Background,
    onBackground = MiningSafetyColors.OnBackground,
    
    surface = MiningSafetyColors.Surface,
    onSurface = MiningSafetyColors.OnSurface,
    surfaceVariant = MiningSafetyColors.SurfaceVariant,
    onSurfaceVariant = MiningSafetyColors.OnSurfaceVariant,
    
    error = MiningSafetyColors.Error,
    onError = MiningSafetyColors.OnError,
    
    outline = MiningSafetyColors.Outline,
    outlineVariant = MiningSafetyColors.OutlineVariant,
    
    surfaceTint = MiningSafetyColors.SurfaceTint
)

private val DarkColorScheme = darkColorScheme(
    primary = MiningSafetyColors.PrimaryLight,
    onPrimary = MiningSafetyColors.OnPrimary,
    primaryContainer = MiningSafetyColors.Primary,
    onPrimaryContainer = MiningSafetyColors.OnPrimary,
    
    secondary = MiningSafetyColors.SecondaryLight,
    onSecondary = MiningSafetyColors.OnSecondary,
    secondaryContainer = MiningSafetyColors.Secondary,
    onSecondaryContainer = MiningSafetyColors.OnSecondary,
    
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    
    error = MiningSafetyColors.Error,
    onError = MiningSafetyColors.OnError,
    
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

@Composable
fun SafeOpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = SafeOpsTypography,
        content = content
    )
}
