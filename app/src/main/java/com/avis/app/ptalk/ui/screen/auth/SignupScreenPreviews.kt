package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewTheme
import com.avis.app.ptalk.ui.viewmodel.auth.VMSignup

@Preview(name = "Signup · Empty (Light)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewSignupEmpty() {
    PreviewTheme(dark = false) {
        SignupScreen(
            uiState = VMSignup.UiState(),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
    }
}

@Preview(name = "Signup · Loading", showBackground = true, heightDp = 900)
@Composable
private fun PreviewSignupLoading() {
    PreviewTheme(dark = false) {
        SignupScreen(
            uiState = VMSignup.UiState(isLoading = true),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
    }
}

@Preview(name = "Signup · Error", showBackground = true, heightDp = 900)
@Composable
private fun PreviewSignupError() {
    PreviewTheme(dark = false) {
        SignupScreen(
            uiState = VMSignup.UiState(error = "Email đã được sử dụng. Vui lòng dùng email khác."),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
    }
}

@Preview(name = "Signup · Success", showBackground = true, heightDp = 900)
@Composable
private fun PreviewSignupSuccess() {
    PreviewTheme(dark = false) {
        SignupScreen(
            uiState = VMSignup.UiState(success = true),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
    }
}

@Preview(name = "Signup · Dark", showBackground = true, heightDp = 900)
@Composable
private fun PreviewSignupDark() {
    PreviewTheme(dark = true) {
        SignupScreen(
            uiState = VMSignup.UiState(),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
    }
}
