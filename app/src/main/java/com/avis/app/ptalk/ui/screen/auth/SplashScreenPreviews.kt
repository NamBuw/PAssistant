package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewTheme

@Preview(name = "Splash · Light", showBackground = true)
@Composable
private fun PreviewSplashLight() {
    PreviewTheme(dark = false) { SplashScreen(onSplashComplete = {}) }
}

@Preview(name = "Splash · Dark", showBackground = true)
@Composable
private fun PreviewSplashDark() {
    PreviewTheme(dark = true) { SplashScreen(onSplashComplete = {}) }
}
