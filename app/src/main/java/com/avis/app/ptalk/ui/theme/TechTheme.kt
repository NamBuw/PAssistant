package com.avis.app.ptalk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * App Theme Colors - PTalk Minimalist Style
 */
object TechColors {
    // PTIT Brand Colors
    val PTITRed = Color(0xFFE31B23)
    val PTITRedDark = Color(0xFFD30005)
    
    // Minimalist theme colors
    val BackgroundLight = Color(0xFFF5F7FA)
    val BackgroundWhite = Color(0xFFFFFFFF)
    
    // Cards
    val CardWhite = Color(0xFFFFFFFF)
    val CardGray = Color(0xFFF5F7FA)
    
    // Text colors
    val TextPrimary = Color(0xFF111111)
    val TextSecondary = Color(0xFF707072)
    val TextMuted = Color(0xFF9E9EA0)
    
    // Other
    val Outline = Color(0xFFE5E5E5)
    val LinkBlue = Color(0xFF2563EB)
    
    // Status colors
    val SuccessGreen = Color(0xFF2E7D52)
    val WarningOrange = Color(0xFFE07B20)
    val ErrorRed = Color(0xFFD30005)
    val OrangeAccent = Color(0xFFFF9800)
}

/**
 * Dynamic theme colors based on system preference
 */
data class AppColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardHighlight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val accent: Color,
    val glow: Color,
    val isDark: Boolean
)

@Composable
fun appColors(darkTheme: Boolean = isSystemInDarkTheme()): AppColors {
    // Using a light-first minimalist approach for both, or we can just apply light theme.
    // For now, we apply the PTalk minimalist light look for both to maintain consistency.
    return AppColors(
        background = TechColors.BackgroundLight,
        surface = TechColors.BackgroundWhite,
        card = TechColors.CardWhite,
        cardHighlight = TechColors.CardGray,
        textPrimary = TechColors.TextPrimary,
        textSecondary = TechColors.TextSecondary,
        textMuted = TechColors.TextMuted,
        primary = TechColors.PTITRedDark,
        accent = TechColors.LinkBlue,
        glow = Color.Transparent, // Removed cyberpunk glow
        isDark = false
    )
}
