package com.avis.app.ptalk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

// ═══════════════════════════════════════════════════════════════════════
// PTalk Design Tokens — Kotlin static mirror of ptalk_design_tokens.json
// Single Source of Truth for PAssistant ↔ PTalk design synchronization
// ═══════════════════════════════════════════════════════════════════════

object PTalkTokens {

    // ── Colors ─────────────────────────────────────────────────────────

    object Colors {
        // Base
        val Black = Color(0xFF000000)
        val White = Color(0xFFFFFFFF)

        // Brand
        val PTITRed = Color(0xFFE31B23)
        val PTITRedDark = Color(0xFFD30005)

        // Main screen semantic
        val TextGreeting = Color(0xFF6BAF8A)
        val TextSubGreeting = Color(0xFF2E7D52)
        val TextStatus = Color(0xFF8BAF9A)
        val TextBrand = Color(0xFF5A9E7D)
        val HamburgerTint = Color(0xFF222222)

        // Splash
        val SplashBg = Color(0xFFFFFFFF)
        val SplashTopBarBg = Color(0xFF111111)
        val SplashTopBarText = Color(0xFFFFFFFF)
        val SplashTitle = Color(0xFF111111)
        val SplashSubtitle = Color(0xFF707072)
        val SplashFooterText = Color(0xFF9E9EA0)
        val SplashDivider = Color(0xFFE5E5E5)

        // Login
        val LoginBg = Color(0xFFFFFFFF)
        val LoginHeadline = Color(0xFF111111)
        val LoginSubheadline = Color(0xFF707072)
        val LoginLabel = Color(0xFF707072)
        val LoginInputText = Color(0xFF111111)
        val LoginInputHint = Color(0xFF9E9EA0)
        val LoginError = Color(0xFFD30005)
        val LoginBtnText = Color(0xFFFFFFFF)
        val LoginGuestText = Color(0xFF111111)
        val LoginDividerText = Color(0xFF9E9EA0)
        val LoginDividerLine = Color(0xFFE5E5E5)
        val LoginFooter = Color(0xFF9E9EA0)

        // Profile
        val ProfileBg = Color(0xFFF5F7FA)
        val ProfileHeaderText = Color(0xFF111111)
        val ProfileBackTint = Color(0xFF222222)

        // Component-level inline colors
        val InputFieldBg = Color(0xFFF5F5F5)
        val StatusChipBg = Color(0xFFF5F5F5)
        val StatusChipDarkBg = Color(0xFF222222)
        val IconButtonBg = Color(0xFFF5F5F5)
        val LogoCardBg = Color(0xFFF5F5F5)
        val LogoCardStroke = Color(0xFFE5E5E5)
        val CancelButtonBg = Color(0xFFFA5252)
        val ProgressTrack = Color(0xFFE8E8E8)
        val ProgressFill = Color(0xFF2E7D52)
        val GuestBtnStroke = Color(0xFFCACACA)
        val GuestBtnPressedBg = Color(0xFFE5E5E5)
        val GuestBtnPressedStroke = Color(0xFF707072)

        // Gradient: Home background (pastel green)
        val HomeBgGradientStart = Color(0xFFE8F5E9)
        val HomeBgGradientCenter = Color(0xFFC8E6C9)
        val HomeBgGradientEnd = Color(0xFFB2DFDB)

        // Gradient: Glass header
        val GlassHeaderStart = Color(0xFFFFFFFF)
        val GlassHeaderCenter = Color(0xF2FFFFFF)
        val GlassHeaderEnd = Color(0xE6FFFFFF)
        val GlassHeaderStroke = Color(0x80FFFFFF)

        // Hold button (tablet)
        val HoldBtnOuter = Color(0xFFDBF5E9)
        val HoldBtnGradientStart = Color(0xFF4DC79A)
        val HoldBtnGradientEnd = Color(0xFF2D9D6F)
        val HoldBtnInnerStroke = Color(0xFFC5F1DD)

        // Orange accent (used in ScanDeviceScreen)
        val OrangeAccent = Color(0xFFFF9800)
    }

    // ── Spacing (dp) ───────────────────────────────────────────────────

    object Spacing {
        val XS: Dp = 4.dp
        val S: Dp = 8.dp
        val M: Dp = 12.dp
        val L: Dp = 16.dp
        val XL: Dp = 24.dp
        val XXL: Dp = 32.dp
        val HeroTop: Dp = 40.dp
        val FooterTop: Dp = 36.dp
        val FooterBottom: Dp = 32.dp
        val BottomDividerMargin: Dp = 48.dp
        val FooterMarginBottom: Dp = 20.dp
        val TouchTargetMin: Dp = 48.dp
    }

    // ── Brand bar dimensions (dp) ──────────────────────────────────────

    object BrandBar {
        val AccentHeight: Dp = 3.dp
        val BarHeight: Dp = 84.dp
        val CtsWidth: Dp = 84.dp
        val CtsHeight: Dp = 56.dp
        val PtitSize: Dp = 80.dp
        val SideMargin: Dp = 16.dp
        val SeparatorHeight: Dp = 28.dp
        val SeparatorMarginStart: Dp = 8.dp
        val TextMarginStart: Dp = 10.dp
    }

    // ── Splash dimensions (dp) ─────────────────────────────────────────

    object SplashDimens {
        val TopBarHeight: Dp = 40.dp
        val LogoSize: Dp = 220.dp
        val CtsLogoWidth: Dp = 160.dp
        val CtsLogoHeight: Dp = 110.dp
    }

    // ── Login dimensions (dp) ──────────────────────────────────────────

    object LoginDimens {
        val MinHeight: Dp = 700.dp
        val AccentHeight: Dp = 4.dp
        val HeaderHeight: Dp = 100.dp
        val HeaderPaddingH: Dp = 24.dp
        val CtsLogoWidth: Dp = 108.dp
        val CtsLogoHeight: Dp = 80.dp
        val PtitLogoSize: Dp = 88.dp
        val HeaderSeparatorHeight: Dp = 36.dp
        val HeaderSeparatorMargin: Dp = 12.dp
        val HeroSize: Dp = 240.dp
        val InputHeight: Dp = 52.dp
        val InputPaddingH: Dp = 16.dp
        val FormMarginH: Dp = 24.dp
        val SubheadlinePaddingH: Dp = 32.dp
    }

    // ── Interactive element sizes (dp) ─────────────────────────────────

    object Interactive {
        val BtnMicSize: Dp = 72.dp
        val BtnMicPadding: Dp = 16.dp
        val BtnCancelHeight: Dp = 44.dp
        val CharacterSize: Dp = 400.dp
        val WaveformHeight: Dp = 280.dp
    }

    // ── Tablet overrides (dp) ──────────────────────────────────────────

    object Tablet {
        val MainCenterSideMargin: Dp = 24.dp
        val MainCenterTopMargin: Dp = 8.dp
        val MainCenterBottomMargin: Dp = 12.dp
        val CharacterMaxSize: Dp = 320.dp
        val LoginHeroTop: Dp = 48.dp
        val LoginFormTopMargin: Dp = 32.dp
        val LoginFormSideMargin: Dp = 12.dp
        val LoginSubheadlineSidePadding: Dp = 24.dp
        val LoginFooterSidePadding: Dp = 24.dp
    }

    // ── Component shapes ───────────────────────────────────────────────

    object Shapes {
        val LoginButton = RoundedCornerShape(30.dp)
        val GuestButton = RoundedCornerShape(30.dp)
        val CancelButton = RoundedCornerShape(24.dp)
        val InputField = RoundedCornerShape(12.dp)
        val LangBadge = RoundedCornerShape(18.dp)
        val StatusChip = RoundedCornerShape(24.dp)
        val IconButton = RoundedCornerShape(18.dp)
        val GlassHeader = RoundedCornerShape(24.dp)
        val ProgressBar = RoundedCornerShape(4.dp)
    }

    // ── Typography sizes (sp) ──────────────────────────────────────────

    object FontSizes {
        val Greeting = 18.sp
        val SubGreeting = 20.sp
        val Status = 14.sp
        val BrandTitle = 12.sp
        val BrandSubtitle = 10.sp
        val BtnCancel = 14.sp
        val LoginHeadline = 34.sp
        val LoginSubheadline = 13.sp
        val LoginHeaderTitle = 15.sp
        val LoginHeaderSubtitle = 11.sp
        val LoginLabel = 10.sp
        val Input = 15.sp
        val LoginError = 12.sp
        val LoginButton = 15.sp
        val GuestButton = 14.sp
        val LoginFooter = 10.sp
        val SplashTopbar = 11.sp
        val SplashTitle = 32.sp
        val SplashSubtitle = 13.sp
        val SplashFooter = 11.sp
    }

    // ── Pre-built gradients ────────────────────────────────────────────

    object Gradients {
        val HomeBackground = Brush.linearGradient(
            colors = listOf(
                Colors.HomeBgGradientStart,
                Colors.HomeBgGradientCenter,
                Colors.HomeBgGradientEnd
            )
        )
        val GlassHeader = Brush.linearGradient(
            colors = listOf(
                Colors.GlassHeaderStart,
                Colors.GlassHeaderCenter,
                Colors.GlassHeaderEnd
            )
        )
    }
}

// Keep legacy aliases so existing code continues to compile
// These delegate to the canonical PTalkTokens values
object TechColors {
    val PTITRed = PTalkTokens.Colors.PTITRed
    val PTITRedDark = PTalkTokens.Colors.PTITRedDark
    val BackgroundLight = PTalkTokens.Colors.ProfileBg
    val BackgroundWhite = PTalkTokens.Colors.White
    val CardWhite = PTalkTokens.Colors.White
    val CardGray = PTalkTokens.Colors.ProfileBg
    val TextPrimary = PTalkTokens.Colors.LoginHeadline
    val TextSecondary = PTalkTokens.Colors.LoginSubheadline
    val TextMuted = PTalkTokens.Colors.LoginInputHint
    val Outline = PTalkTokens.Colors.LoginDividerLine
    val LinkBlue = Color(0xFF2563EB)
    val SuccessGreen = PTalkTokens.Colors.TextSubGreeting
    val WarningOrange = Color(0xFFE07B20)
    val ErrorRed = PTalkTokens.Colors.PTITRedDark
    val OrangeAccent = PTalkTokens.Colors.OrangeAccent
}

/**
 * Dynamic theme colors based on system preference.
 * Currently both modes resolve to PTalk's light-mode minimalist look.
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
    // Both modes use PTalk's minimalist light-mode palette for brand consistency
    return AppColors(
        background = PTalkTokens.Colors.ProfileBg,
        surface = PTalkTokens.Colors.White,
        card = PTalkTokens.Colors.White,
        cardHighlight = PTalkTokens.Colors.ProfileBg,
        textPrimary = PTalkTokens.Colors.LoginHeadline,      // #111111
        textSecondary = PTalkTokens.Colors.LoginSubheadline,  // #707072
        textMuted = PTalkTokens.Colors.LoginInputHint,        // #9E9EA0
        primary = PTalkTokens.Colors.PTITRedDark,             // #D30005
        accent = TechColors.LinkBlue,                         // #2563EB
        glow = Color.Transparent,
        isDark = false
    )
}
