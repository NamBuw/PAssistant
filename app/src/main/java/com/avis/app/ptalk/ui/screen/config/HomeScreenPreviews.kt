package com.avis.app.ptalk.ui.screen.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewSeeds
import com.avis.app.ptalk.ui.preview.PreviewTheme
import com.avis.app.ptalk.ui.viewmodel.VMHome

@Preview(name = "Home · 3 devices (Light)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeThreeDevicesLight() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(devices = PreviewSeeds.previewDevices(3)),
            username = "Bùi Vân",
            email = "buivan@ptit.edu.vn",
            phone = null,
            userId = "u-1f7e9d10-2c3a-4b8e-9f12-aa11bb22cc33",
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}

@Preview(name = "Home · 3 devices (Dark)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeThreeDevicesDark() {
    PreviewTheme(dark = true) {
        HomeContent(
            uiState = VMHome.UiState(devices = PreviewSeeds.previewDevices(3)),
            username = "Bùi Vân",
            email = "buivan@ptit.edu.vn",
            phone = null,
            userId = "u-1f7e9d10",
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}

@Preview(name = "Home · Empty", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeEmpty() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(devices = emptyList()),
            username = "Bùi Vân",
            email = "buivan@ptit.edu.vn",
            phone = null,
            userId = null,
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}

@Preview(name = "Home · Loading", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeLoading() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(isLoading = true),
            username = "Bùi Vân",
            email = "buivan@ptit.edu.vn",
            phone = null,
            userId = null,
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}

@Preview(name = "Home · Error", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeError() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(error = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng."),
            username = "Bùi Vân",
            email = null,
            phone = null,
            userId = null,
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}

@Preview(name = "Home · Long Vietnamese username", showBackground = true, heightDp = 900)
@Composable
private fun PreviewHomeLongName() {
    PreviewTheme(dark = false) {
        HomeContent(
            uiState = VMHome.UiState(devices = PreviewSeeds.previewDevices(2)),
            username = "Nguyễn Phạm Bùi Hoàng Anh Tuấn Vũ",
            email = "tuan.vu.hoang.anh@ptit.edu.vn",
            phone = "0987654321",
            userId = "u-test",
            onNavigateToScan = {},
            onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {},
            onDeleteDevice = {}
        )
    }
}
