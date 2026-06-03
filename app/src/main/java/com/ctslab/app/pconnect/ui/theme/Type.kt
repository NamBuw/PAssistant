package com.ctslab.app.pconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Vietnamese diacritics stack above/below base characters, so every style needs explicit
// lineHeight to prevent clipping on tight-line-height devices (API 29+ renders combining
// marks correctly, but the view's bounding box must be tall enough to show them).
// Rule: lineHeight = fontSize * 1.4 for body, 1.3 for display/headline.

val AppTypography = Typography(
    // "ĐĂNG NHẬP", section headings
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = PTalkTokens.FontSizes.LoginHeadline,   // 34sp
        lineHeight = (34 * 1.3f).sp,                      // 44.2sp
        letterSpacing = 0.5.sp,                           // was 2sp — reduced for Vietnamese HOA+diacritic
    ),
    // splash "P-Connect" title
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = PTalkTokens.FontSizes.SplashTitle,     // 32sp
        lineHeight = (32 * 1.3f).sp,
        letterSpacing = 0.25.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = PTalkTokens.FontSizes.SubGreeting,     // 20sp
        lineHeight = (20 * 1.4f).sp,
        letterSpacing = 0.sp,
    ),
    // card/screen titles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = PTalkTokens.FontSizes.SubGreeting,     // 20sp
        lineHeight = (20 * 1.35f).sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = PTalkTokens.FontSizes.Greeting,        // 18sp
        lineHeight = (18 * 1.35f).sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = PTalkTokens.FontSizes.Status,          // 14sp
        lineHeight = (14 * 1.4f).sp,
        letterSpacing = 0.sp,
    ),
    // buttons, list item headers
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = PTalkTokens.FontSizes.LoginButton,     // 15sp
        lineHeight = (15 * 1.4f).sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = PTalkTokens.FontSizes.LoginHeaderTitle, // 15sp
        lineHeight = (15 * 1.4f).sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = PTalkTokens.FontSizes.GuestButton,     // 14sp
        lineHeight = (14 * 1.4f).sp,
        letterSpacing = 0.1.sp,
    ),
    // body text, subheadlines
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = PTalkTokens.FontSizes.Input,           // 15sp
        lineHeight = (15 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = PTalkTokens.FontSizes.LoginSubheadline, // 13sp
        lineHeight = (13 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = PTalkTokens.FontSizes.BrandTitle,      // 12sp
        lineHeight = (12 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
    // captions, labels, hints
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = PTalkTokens.FontSizes.LoginHeaderSubtitle, // 11sp
        lineHeight = (11 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = PTalkTokens.FontSizes.LoginLabel,      // 10sp
        lineHeight = (10 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = PTalkTokens.FontSizes.SplashFooter,    // 11sp
        lineHeight = (11 * 1.5f).sp,
        letterSpacing = 0.sp,
    ),
)
