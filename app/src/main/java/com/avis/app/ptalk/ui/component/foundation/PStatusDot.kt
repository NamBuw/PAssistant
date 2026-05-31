package com.avis.app.ptalk.ui.component.foundation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors

/**
 * Animated status dot.
 *
 * - When `pulsing = true` (e.g., device online), draws a slow pulsing
 *   halo around a solid dot.
 * - When `pulsing = false` (offline), shows a static muted dot.
 */
@Composable
fun PStatusDot(
    online: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    onlineColor: Color = LocalAppColors.current.success,
    offlineColor: Color = LocalAppColors.current.textMuted
) {
    val color = if (online) onlineColor else offlineColor

    if (online) {
        val transition = rememberInfiniteTransition(label = "status-pulse")
        val pulse by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1600),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulse"
        )

        Box(
            modifier = modifier.size(size * 2.4f),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 2.4f)) {
                val maxRadius = this.size.minDimension / 2f
                val radius = maxRadius * pulse
                val alpha = (1f - pulse).coerceIn(0f, 1f)
                drawCircle(
                    color = color.copy(alpha = alpha * 0.6f),
                    radius = radius,
                    center = Offset(this.size.width / 2f, this.size.height / 2f)
                )
            }
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, CircleShape)
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(size)
                .background(color, CircleShape)
        )
    }
}
