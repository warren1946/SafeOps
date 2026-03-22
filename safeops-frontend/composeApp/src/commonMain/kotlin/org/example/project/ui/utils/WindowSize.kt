package org.example.project.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size categories for responsive design
 */
enum class WindowSize {
    COMPACT,    // Phone portrait (< 600dp)
    MEDIUM,     // Phone landscape/tablet portrait (600dp - 840dp)
    EXPANDED    // Tablet landscape/desktop (> 840dp)
}

/**
 * Data class containing window size information
 */
data class WindowSizeInfo(
    val widthSize: WindowSize,
    val heightSize: WindowSize,
    val widthDp: Dp,
    val heightDp: Dp
) {
    val isCompact: Boolean get() = widthSize == WindowSize.COMPACT
    val isMedium: Boolean get() = widthSize == WindowSize.MEDIUM
    val isExpanded: Boolean get() = widthSize == WindowSize.EXPANDED
    
    val isMobile: Boolean get() = widthSize == WindowSize.COMPACT
    val isTablet: Boolean get() = widthSize == WindowSize.MEDIUM
    val isDesktop: Boolean get() = widthSize == WindowSize.EXPANDED
}

/**
 * Calculate window size category based on width
 */
fun getWindowSizeClass(width: Dp): WindowSize {
    return when {
        width < 600.dp -> WindowSize.COMPACT
        width < 840.dp -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

/**
 * Composable that provides window size information
 * 
 * Note: This is a simplified version. In production, you'd use:
 * - LocalWindowInfo.current.containerSize for Compose Multiplatform
 * - Or windowSizeClass from Material3 Window Size classes
 */
@Composable
fun rememberWindowSizeInfo(): WindowSizeInfo {
    // For WASM/JS, we'll use a default approach
    // In a real app, you'd get this from the browser window
    // For now, we use a responsive approach based on available space
    
    return WindowSizeInfo(
        widthSize = WindowSize.EXPANDED, // Default to expanded for web
        heightSize = WindowSize.EXPANDED,
        widthDp = 1200.dp,
        heightDp = 800.dp
    )
}

/**
 * Breakpoints for responsive design
 */
object Breakpoints {
    val COMPACT_MAX = 600.dp      // Phones
    val MEDIUM_MAX = 840.dp       // Tablets
    // Above 840.dp is EXPANDED    // Desktops/Large tablets
}

/**
 * Responsive padding values based on screen size
 */
fun getResponsivePadding(windowSize: WindowSize): Dp {
    return when (windowSize) {
        WindowSize.COMPACT -> 16.dp    // Small padding on phones
        WindowSize.MEDIUM -> 24.dp     // Medium padding on tablets
        WindowSize.EXPANDED -> 48.dp   // Large padding on desktop
    }
}

/**
 * Responsive card width based on screen size
 */
fun getResponsiveCardWidth(windowSize: WindowSize): Dp {
    return when (windowSize) {
        WindowSize.COMPACT -> 360.dp   // Full width minus padding on phones
        WindowSize.MEDIUM -> 420.dp    // Medium width on tablets
        WindowSize.EXPANDED -> 480.dp  // Fixed width on desktop
    }
}

/**
 * Determines if sidebar should be shown based on window size
 */
fun shouldShowSidebar(windowSize: WindowSize): Boolean {
    return windowSize != WindowSize.COMPACT
}

/**
 * Determines if bottom navigation should be shown instead of sidebar
 */
fun shouldShowBottomNav(windowSize: WindowSize): Boolean {
    return windowSize == WindowSize.COMPACT
}
