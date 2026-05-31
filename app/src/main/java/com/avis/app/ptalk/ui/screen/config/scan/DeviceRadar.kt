package com.avis.app.ptalk.ui.screen.config.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.ui.theme.AppColors
import kotlin.random.Random

/**
 * Animated radar canvas with sweep + concentric rings.
 * Devices appear as glowing markers positioned by RSSI (closer to
 * center = stronger signal) and a stable angle derived from MAC.
 */
@Composable
internal fun DeviceRadar(
    devices: List<ScannedDevice>,
    isScanning: Boolean,
    colors: AppColors,
    onDeviceClick: (ScannedDevice) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "radar")

    val sweepAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val pulseScale by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse-scale"
    )

    val pulseAlpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse-alpha"
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val radarSize = (maxWidth * 0.85f).coerceIn(220.dp, 480.dp)

        Box(
            modifier = Modifier
                .size(radarSize)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val radarGreen = Color(0xFF00FF00)
            val radarDarkGreen = Color(0xFF003300)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val center = Offset(size.width / 2, size.height / 2)
                        val maxRadius = size.width / 2

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(radarDarkGreen, Color(0xFF001800))
                            ),
                            radius = maxRadius,
                            center = center
                        )

                        // Concentric distance rings
                        val ringCount = 4
                        for (i in 1..ringCount) {
                            drawCircle(
                                color = radarGreen.copy(alpha = 0.3f),
                                radius = maxRadius * i / ringCount,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        // Cross axes
                        drawLine(
                            color = radarGreen.copy(alpha = 0.2f),
                            start = Offset(center.x, 0f),
                            end = Offset(center.x, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = radarGreen.copy(alpha = 0.2f),
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 1.dp.toPx()
                        )

                        if (isScanning) {
                            // Sweep trail (gradient fan)
                            for (trail in 0..30) {
                                val angleRad = Math.toRadians((sweepAngle - trail * 2).toDouble())
                                val end = Offset(
                                    (center.x + maxRadius * kotlin.math.cos(angleRad)).toFloat(),
                                    (center.y + maxRadius * kotlin.math.sin(angleRad)).toFloat()
                                )
                                drawLine(
                                    color = radarGreen.copy(
                                        alpha = (0.4f - trail * 0.012f).coerceAtLeast(0f)
                                    ),
                                    start = center,
                                    end = end,
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(pulseScale)
                            .alpha(pulseAlpha)
                            .border(2.dp, radarGreen, CircleShape)
                    )
                }

                val centerIconSize = (radarSize.value * 0.12f).coerceIn(32f, 60f).dp
                Box(
                    modifier = Modifier
                        .size(centerIconSize)
                        .background(radarGreen.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(centerIconSize * 0.6f)
                    )
                }

                val markerOffsetMultiplier = (radarSize.value * 0.4f).coerceIn(80f, 200f)
                devices.forEach { device ->
                    val position = calculateDevicePosition(device.rssi, device.address)
                    DeviceMarker(
                        position = position,
                        offsetMultiplier = markerOffsetMultiplier,
                        radarSize = radarSize.value,
                        onClick = { onDeviceClick(device) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceMarker(
    position: Offset,
    offsetMultiplier: Float,
    radarSize: Float,
    onClick: () -> Unit
) {
    val radarGreen = Color(0xFF00FF00)
    val transition = rememberInfiniteTransition(label = "marker")
    val glow by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val markerSize = (radarSize * 0.1f).coerceIn(28f, 50f).dp
    val fontSize = (radarSize * 0.045f).coerceIn(12f, 20f).sp

    Box(
        modifier = Modifier
            .offset(
                x = (position.x * offsetMultiplier).dp,
                y = (position.y * offsetMultiplier).dp
            )
            .size(markerSize)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(radarGreen.copy(alpha = glow), radarGreen.copy(alpha = 0.3f))
                ),
                shape = CircleShape
            )
            .border(2.dp, radarGreen, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "P",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

/**
 * Maps RSSI to a normalized polar position. Stronger signal → closer
 * to center; the angle is derived deterministically from the MAC so
 * each device keeps a stable spot during a scan session.
 */
private fun calculateDevicePosition(rssi: Int, macAddress: String): Offset {
    val minRssi = -100f
    val maxRssi = -30f
    val minDistance = 0.12f
    val maxDistance = 0.88f

    val clamped = rssi.coerceIn(minRssi.toInt(), maxRssi.toInt()).toFloat()
    val rssiRatio = (clamped - minRssi) / (maxRssi - minRssi)
    val distance = maxDistance - (rssiRatio * (maxDistance - minDistance))

    val random = Random(macAddress.hashCode())
    val angle = Math.toRadians(random.nextDouble() * 360)
    val x = (distance * kotlin.math.cos(angle)).toFloat()
    val y = (distance * kotlin.math.sin(angle)).toFloat()
    return Offset(x, y)
}
