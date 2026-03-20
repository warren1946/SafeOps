package com.zama.safeops.frontend.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors - Mining Safety Theme
val SafetyOrange = Color(0xFFF97316)
val SafetyOrangeDark = Color(0xFFEA580C)
val SafetyGreen = Color(0xFF22C55E)
val SafetyRed = Color(0xFFEF4444)
val SafetyYellow = Color(0xFFEAB308)
val SafetyBlue = Color(0xFF3B82F6)

val PrimaryLight = Color(0xFF1E40AF)
val PrimaryDark = Color(0xFF60A5FA)
val SecondaryLight = SafetyOrange
val SecondaryDark = SafetyOrange

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = SafetyGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF166534),
    error = SafetyRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SecondaryDark,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF9A3412),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = SafetyGreen,
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF166534),
    onTertiaryContainer = Color(0xFFDCFCE7),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B)
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
