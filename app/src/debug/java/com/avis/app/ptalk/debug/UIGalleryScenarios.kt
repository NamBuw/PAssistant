package com.avis.app.ptalk.debug

import androidx.compose.runtime.Composable
import com.avis.app.ptalk.ui.preview.PreviewSeeds
import com.avis.app.ptalk.ui.screen.auth.LoginScreen
import com.avis.app.ptalk.ui.screen.auth.SignupScreen
import com.avis.app.ptalk.ui.screen.auth.SplashScreen
import com.avis.app.ptalk.ui.screen.config.ControlContent
import com.avis.app.ptalk.ui.screen.config.DeviceDetailContent
import com.avis.app.ptalk.ui.screen.config.HomeContent
import com.avis.app.ptalk.ui.screen.config.ScanDeviceContent
import com.avis.app.ptalk.ui.viewmodel.VMConfigDevice
import com.avis.app.ptalk.ui.viewmodel.VMDeviceDetail
import com.avis.app.ptalk.ui.viewmodel.VMHome
import com.avis.app.ptalk.ui.viewmodel.auth.VMSignup

/** Enumerates every UI scenario that the debug Gallery can render. */
internal enum class GalleryScenario {
    Splash,
    Login,
    SignupEmpty, SignupError, SignupSuccess,
    HomeDevices, HomeEmpty, HomeLoading, HomeError,
    ControlOnline, ControlLowBattery, ControlOffline, ControlWaiting,
    DetailSessionsPTalk, DetailSessionsKidMentor, DetailSessionsEmpty,
    DetailMessages, DetailNoDeviceId,
    ScanPermission, ScanBtOff, ScanLocationOff,
    ScanRadarEmpty, ScanRadarFull, ScanList,
    Foundations
}

/**
 * Render a chosen scenario by feeding mock state into the real
 * stateless `*Content` composables (Cách 1 of UI_PREVIEW spec).
 * No ViewModel/Hilt graph is touched.
 */
@Composable
internal fun renderScenario(scenario: GalleryScenario) {
    when (scenario) {
        // ── Auth ────────────────────────────────────────────────
        GalleryScenario.Splash -> SplashScreen(onSplashComplete = {})
        GalleryScenario.Login -> LoginScreen(
            onNavigateToHome = {}, onNavigateToSignup = {}, onLaunchSSO = {}
        )
        GalleryScenario.SignupEmpty -> SignupScreen(
            uiState = VMSignup.UiState(),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
        GalleryScenario.SignupError -> SignupScreen(
            uiState = VMSignup.UiState(error = "Email đã được sử dụng. Vui lòng dùng email khác."),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )
        GalleryScenario.SignupSuccess -> SignupScreen(
            uiState = VMSignup.UiState(success = true),
            onRegister = { _, _, _, _ -> },
            onNavigateToLogin = {},
            onClearError = {}
        )

        // ── Home ────────────────────────────────────────────────
        GalleryScenario.HomeDevices -> HomeContent(
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
        GalleryScenario.HomeEmpty -> HomeContent(
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
        GalleryScenario.HomeLoading -> HomeContent(
            uiState = VMHome.UiState(isLoading = true),
            username = "Bùi Vân", email = "buivan@ptit.edu.vn",
            phone = null, userId = null,
            onNavigateToScan = {}, onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {}, onDeleteDevice = {}
        )
        GalleryScenario.HomeError -> HomeContent(
            uiState = VMHome.UiState(error = "Không thể kết nối đến máy chủ."),
            username = "Bùi Vân", email = null, phone = null, userId = null,
            onNavigateToScan = {}, onNavigateToControl = { _, _ -> },
            onNavigateToDeviceDetail = { _, _, _ -> },
            onSignOut = {}, onDeleteDevice = {}
        )

        // ── Control ─────────────────────────────────────────────
        GalleryScenario.ControlOnline -> ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.Online),
            isConnected = true, isLoading = false, lastError = null,
            onBack = {}, onClearError = {},
            onSetVolume = {}, onSetBrightness = {}, onSetDeviceName = {},
            onResetWifi = {}, onReboot = {}
        )
        GalleryScenario.ControlLowBattery -> ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.LowBattery),
            isConnected = true, isLoading = false, lastError = null,
            onBack = {}, onClearError = {},
            onSetVolume = {}, onSetBrightness = {}, onSetDeviceName = {},
            onResetWifi = {}, onReboot = {}
        )
        GalleryScenario.ControlOffline -> ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.Offline),
            isConnected = true, isLoading = false, lastError = null,
            onBack = {}, onClearError = {},
            onSetVolume = {}, onSetBrightness = {}, onSetDeviceName = {},
            onResetWifi = {}, onReboot = {}
        )
        GalleryScenario.ControlWaiting -> ControlContent(
            deviceName = "Loa phòng bé Bin",
            localDeviceName = "Loa phòng bé Bin",
            status = null,
            isConnected = true, isLoading = false, lastError = null,
            onBack = {}, onClearError = {},
            onSetVolume = {}, onSetBrightness = {}, onSetDeviceName = {},
            onResetWifi = {}, onReboot = {}
        )

        // ── Detail (chat history) ──────────────────────────────
        GalleryScenario.DetailSessionsPTalk -> DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = PreviewSeeds.previewChatSessions(),
                selectedTab = VMDeviceDetail.ChatTab.PTALK
            ),
            onBack = {}, onSelectTab = {}, onSelectSession = {}, onClearSelectedSession = {}
        )
        GalleryScenario.DetailSessionsKidMentor -> DeviceDetailContent(
            deviceName = "Loa phòng bé Bin",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = PreviewSeeds.previewChatSessions(),
                selectedTab = VMDeviceDetail.ChatTab.KID_MENTOR
            ),
            onBack = {}, onSelectTab = {}, onSelectSession = {}, onClearSelectedSession = {}
        )
        GalleryScenario.DetailSessionsEmpty -> DeviceDetailContent(
            deviceName = "Loa mới cấu hình",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(allSessions = emptyList()),
            onBack = {}, onSelectTab = {}, onSelectSession = {}, onClearSelectedSession = {}
        )
        GalleryScenario.DetailMessages -> {
            val sessions = PreviewSeeds.previewChatSessions()
            DeviceDetailContent(
                deviceName = "Loa khách phòng họp",
                deviceId = "1F7E9D10",
                uiState = VMDeviceDetail.UiState(
                    allSessions = sessions,
                    selectedSession = sessions.first(),
                    messages = PreviewSeeds.previewChatMessages()
                ),
                onBack = {}, onSelectTab = {}, onSelectSession = {}, onClearSelectedSession = {}
            )
        }
        GalleryScenario.DetailNoDeviceId -> DeviceDetailContent(
            deviceName = "Thiết bị không rõ",
            deviceId = null,
            uiState = VMDeviceDetail.UiState(),
            onBack = {}, onSelectTab = {}, onSelectSession = {}, onClearSelectedSession = {}
        )

        // ── Scan ───────────────────────────────────────────────
        GalleryScenario.ScanPermission -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = false,
            isBluetoothEnabled = false, isLocationEnabled = false,
            showAsList = false, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )
        GalleryScenario.ScanBtOff -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = true,
            isBluetoothEnabled = false, isLocationEnabled = true,
            showAsList = false, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )
        GalleryScenario.ScanLocationOff -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = true,
            isBluetoothEnabled = true, isLocationEnabled = false,
            showAsList = false, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )
        GalleryScenario.ScanRadarEmpty -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(scanning = true),
            permissionsGranted = true,
            isBluetoothEnabled = true, isLocationEnabled = true,
            showAsList = false, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )
        GalleryScenario.ScanRadarFull -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(
                scanning = true,
                devices = PreviewSeeds.previewScannedDevices(5)
            ),
            permissionsGranted = true,
            isBluetoothEnabled = true, isLocationEnabled = true,
            showAsList = false, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )
        GalleryScenario.ScanList -> ScanDeviceContent(
            uiState = VMConfigDevice.UiState(
                scanning = false,
                devices = PreviewSeeds.previewScannedDevices(5)
            ),
            permissionsGranted = true,
            isBluetoothEnabled = true, isLocationEnabled = true,
            showAsList = true, onToggleView = {}, onBack = {},
            onRequestPermission = {}, onOpenBluetoothSettings = {},
            onOpenLocationSettings = {}, onScanToggle = {}, onDeviceClick = {}
        )

        // ── Foundation showcase ────────────────────────────────
        GalleryScenario.Foundations -> FoundationsGalleryContent()
    }
}
