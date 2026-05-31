package com.avis.app.ptalk.ui.component.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avis.app.ptalk.LocalAppColors

/**
 * Circular avatar showing the device/user initial on a brand-tinted
 * gradient. Optionally overlays an animated status dot at the
 * bottom-end edge.
 */
@Composable
fun PDeviceAvatar(
    name: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    online: Boolean? = null,
    gradient: List<Color>? = null
) {
    val colors = LocalAppColors.current
    val initial = (name?.trim()?.firstOrNull()?.uppercaseChar() ?: 'P').toString()

    val brushColors = gradient ?: listOf(
        colors.primary,
        colors.primary.copy(alpha = 0.7f)
    )

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.linearGradient(brushColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = (size.value * 0.42f).sp,
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (online != null) {
            val dotSize = (size.value * 0.28f).coerceAtLeast(8f).dp
            Box(
                modifier = Modifier
                    .size(dotSize + 4.dp)
                    .align(Alignment.BottomEnd)
                    .background(colors.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PStatusDot(online = online, size = dotSize)
            }
        }
    }
}
