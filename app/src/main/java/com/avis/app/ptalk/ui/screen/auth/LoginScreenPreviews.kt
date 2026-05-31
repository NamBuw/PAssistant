package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewTheme

@Preview(name = "Login · Light", showBackground = true, heightDp = 800)
@Composable
private fun PreviewLoginLight() {
    PreviewTheme(dark = false) {
        LoginScreen(onNavigateToHome = {}, onNavigateToSignup = {}, onLaunchSSO = {})
    }
}

@Preview(name = "Login · Dark", showBackground = true, heightDp = 800)
@Composable
private fun PreviewLoginDark() {
    PreviewTheme(dark = true) {
        LoginScreen(onNavigateToHome = {}, onNavigateToSignup = {}, onLaunchSSO = {})
    }
}
