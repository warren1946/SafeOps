package org.example.project.ui.theme

import androidx.compose.ui.graphics.Color

// Mining Safety Theme Colors
object MiningSafetyColors {
    // Primary - Deep Safety Blue
    val Primary = Color(0xFF1E40AF)
    val PrimaryDark = Color(0xFF1E3A8A)
    val PrimaryLight = Color(0xFF3B82F6)
    val OnPrimary = Color(0xFFFFFFFF)
    
    // Secondary - Safety Orange
    val Secondary = Color(0xFFF97316)
    val SecondaryDark = Color(0xFFEA580C)
    val SecondaryLight = Color(0xFFFB923C)
    val OnSecondary = Color(0xFFFFFFFF)
    
    // Success - Safety Green
    val Success = Color(0xFF22C55E)
    val OnSuccess = Color(0xFFFFFFFF)
    
    // Error - Safety Red
    val Error = Color(0xFFEF4444)
    val OnError = Color(0xFFFFFFFF)
    
    // Warning - Safety Yellow
    val Warning = Color(0xFFEAB308)
    val OnWarning = Color(0xFF000000)
    
    // Background
    val Background = Color(0xFFF8FAFC)
    val OnBackground = Color(0xFF1E293B)
    
    // Surface
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F5F9)
    val OnSurface = Color(0xFF1E293B)
    val OnSurfaceVariant = Color(0xFF64748B)
    
    // Outline
    val Outline = Color(0xFFCBD5E1)
    val OutlineVariant = Color(0xFFE2E8F0)
    
    // Gradients
    val GradientStart = Color(0xFF1E40AF)
    val GradientEnd = Color(0xFF3B82F6)
    
    // Surface Tints
    val SurfaceTint = Primary.copy(alpha = 0.05f)
}
