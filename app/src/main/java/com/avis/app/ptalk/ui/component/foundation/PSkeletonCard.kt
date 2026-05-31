package com.avis.app.ptalk.ui.component.foundation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors

/**
 * Reusable shimmer brush — shifts a soft highlight horizontally to
 * simulate loading. Fully opaque colors keep the effect visible across
 * both light and dark themes.
 */
@Composable
fun shimmerBrush(): Brush {
    val colors = LocalAppColors.current
    val base = if (colors.isDark) Color(0xFF22262C) else Color(0xFFEDEEF1)
    val highlight = if (colors.isDark) Color(0xFF2E3338) else Color(0xFFF8F9FB)

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-translate"
    )

    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = androidx.compose.ui.geometry.Offset(translate * 1000f, 0f),
        end = androidx.compose.ui.geometry.Offset(translate * 1000f + 600f, 0f)
    )
}

/**
 * Skeleton placeholder shaped like a typical list-item card — circular
 * leading element + two text lines. Use a few in sequence (e.g., 4-5
 * items) while content is loading.
 */
@Composable
fun PSkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 88.dp
) {
    val colors = LocalAppColors.current
    val brush = shimmerBrush()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(brush, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp)
                        .background(brush, RoundedCornerShape(6.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(10.dp)
                        .background(brush, RoundedCornerShape(5.dp))
                )
            }
        }
    }
}
