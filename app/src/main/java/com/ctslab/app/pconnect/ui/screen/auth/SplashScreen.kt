package com.ctslab.app.pconnect.ui.screen.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.ctslab.app.pconnect.R
import com.ctslab.app.pconnect.ui.theme.PTalkTokens
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    // Animation states
    val scale = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }
    val appNameAlpha = remember { Animatable(0f) }
    val footerAlpha = remember { Animatable(0f) }
    val footerScale = remember { Animatable(0.5f) }

    LaunchedEffect(key1 = true) {
        // Phase 1: PTIT Logo scales up and rotates 360 degrees horizontally (slowed down to 2000ms)
        launch {
            scale.animateTo(
                targetValue = 1.3f, // Phóng to logo PTIT
                animationSpec = tween(durationMillis = 2000)
            )
        }
        rotationY.animateTo(
            targetValue = 360f, // Xoay tròn 360 độ theo chiều ngang
            animationSpec = tween(durationMillis = 2000)
        )
        
        delay(200L) // Một chút khoảng nghỉ ngắn sau khi xoay xong

        // Phase 2: App Name "PASISTANT" fades in
        appNameAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )

        delay(200L)

        // Phase 3: "made by" section pops out (scales up & fades in)
        launch {
            footerScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        footerAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )

        delay(1500L) // Hold before completing splash screen
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PTalkTokens.Colors.SplashBg)
    ) {
        // Top promotional bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PTalkTokens.SplashDimens.TopBarHeight)
                .background(PTalkTokens.Colors.SplashTopBarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P-Connect",
                color = PTalkTokens.Colors.SplashTopBarText,
                fontSize = PTalkTokens.FontSizes.SplashTopbar,
                fontWeight = FontWeight.Medium,
                letterSpacing = PTalkTokens.FontSizes.SplashTopbar * 0.15f
            )
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // PTIT Logo with scale and rotation animations
            Image(
                painter = painterResource(id = R.drawable.logo_p_connect),
                contentDescription = "Logo PTIT",
                modifier = Modifier
                    .size(PTalkTokens.SplashDimens.LogoSize)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.rotationY = rotationY.value
                        cameraDistance = 12f * density
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL * 1.5f))

            // App Name — use displayMedium (32sp, lineHeight 41.6sp, letterSpacing 0.25sp)
            Text(
                text = "P-Connect",
                style = MaterialTheme.typography.displayMedium,
                color = PTalkTokens.Colors.SplashTitle,
                modifier = Modifier.alpha(appNameAlpha.value)
            )
        }

        // Bottom Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .alpha(footerAlpha.value)
                .graphicsLayer {
                    scaleX = footerScale.value
                    scaleY = footerScale.value
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Made by",
                color = PTalkTokens.Colors.SplashSubtitle,
                fontSize = PTalkTokens.FontSizes.SplashSubtitle,
                letterSpacing = PTalkTokens.FontSizes.SplashSubtitle * 0.08f
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.S))

            Image(
                painter = painterResource(id = R.drawable.logo_cts_flashscreen),
                contentDescription = "Logo CTS",
                modifier = Modifier
                    .width(PTalkTokens.SplashDimens.CtsLogoWidth)
                    .height(PTalkTokens.SplashDimens.CtsLogoHeight),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // Bottom Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(PTalkTokens.Colors.SplashDivider)
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            Text(
                text = "Học viện Công nghệ Bưu chính Viễn thông",
                style = MaterialTheme.typography.labelSmall, // 11sp, lineHeight 16.5sp
                color = PTalkTokens.Colors.SplashFooterText,
                modifier = Modifier.padding(bottom = PTalkTokens.Spacing.XL)
            )
        }
    }
}
