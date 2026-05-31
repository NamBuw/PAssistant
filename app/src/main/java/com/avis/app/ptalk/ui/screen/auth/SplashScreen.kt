package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.R
import com.avis.app.ptalk.ui.preview.openDebugGalleryIfAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash — branded intro with a fast logo reveal, name fade and footer
 * pop-in. Total runtime ~2.5s before navigating to start destination.
 *
 * Long-press on the PTIT logo opens the debug-only UI Gallery (no-op
 * in release builds).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val colors = LocalAppColors.current
    val context = androidx.compose.ui.platform.LocalContext.current

    val scale = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }
    val nameAlpha = remember { Animatable(0f) }
    val footerAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1f, animationSpec = tween(durationMillis = 900))
        }
        rotationY.animateTo(360f, animationSpec = tween(durationMillis = 1200))
        delay(120)
        nameAlpha.animateTo(1f, animationSpec = tween(durationMillis = 500))
        delay(120)
        footerAlpha.animateTo(1f, animationSpec = tween(durationMillis = 500))
        delay(700)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (colors.isDark) {
                        listOf(colors.background, Color(0xFF15181C))
                    } else {
                        listOf(Color(0xFFFFF6F6), colors.background)
                    }
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            // Halo behind logo
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colors.primary.copy(alpha = if (colors.isDark) 0.20f else 0.12f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_ptit),
                    contentDescription = "Logo PTIT",
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                            this.rotationY = rotationY.value
                            cameraDistance = 12f * density
                        }
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { openDebugGalleryIfAvailable(context) }
                        ),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "PASSISTANT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = colors.primary,
                modifier = Modifier.alpha(nameAlpha.value)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Trợ lý thiết bị PTalk",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.alpha(nameAlpha.value)
            )

            Spacer(Modifier.weight(1f))

            // Footer — Made by + CTS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .alpha(footerAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Made by",
                    color = colors.textMuted,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.height(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo_cts_flashscreen),
                    contentDescription = "Logo CTS",
                    modifier = Modifier
                        .width(140.dp)
                        .height(80.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(colors.outlineVariant)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Học viện Công nghệ Bưu chính Viễn thông",
                    color = colors.textMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
