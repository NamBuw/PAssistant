package com.avis.app.ptalk.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * PAssistant shape system — slightly more rounded than M3 default
 * to match the "Friendly Premium" visual direction.
 *
 * Usage: components consume MaterialTheme.shapes.<size>; design tokens
 * (PTalkTokens.Shapes) remain available for legacy call sites.
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
