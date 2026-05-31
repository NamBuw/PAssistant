package com.avis.app.ptalk.ui.screen.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewSeeds
import com.avis.app.ptalk.ui.preview.PreviewTheme
import com.avis.app.ptalk.ui.viewmodel.VMDeviceDetail

@Preview(name = "Detail · PTalk sessions (Light)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailSessionsPTalk() {
    PreviewTheme(dark = false) {
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = PreviewSeeds.previewChatSessions(),
                selectedTab = VMDeviceDetail.ChatTab.PTALK
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · KidMentor sessions", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailSessionsKidMentor() {
    PreviewTheme(dark = false) {
        DeviceDetailContent(
            deviceName = "Loa phòng bé Bin",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = PreviewSeeds.previewChatSessions(),
                selectedTab = VMDeviceDetail.ChatTab.KID_MENTOR
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Sessions (Dark)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailSessionsDark() {
    PreviewTheme(dark = true) {
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = PreviewSeeds.previewChatSessions(),
                selectedTab = VMDeviceDetail.ChatTab.PTALK
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Sessions empty", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailSessionsEmpty() {
    PreviewTheme(dark = false) {
        DeviceDetailContent(
            deviceName = "Loa mới cấu hình",
            deviceId = "ABC12345",
            uiState = VMDeviceDetail.UiState(allSessions = emptyList()),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Loading sessions", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailSessionsLoading() {
    PreviewTheme(dark = false) {
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(isLoading = true),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Messages", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailMessages() {
    PreviewTheme(dark = false) {
        val sessions = PreviewSeeds.previewChatSessions()
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = sessions,
                selectedSession = sessions.first(),
                messages = PreviewSeeds.previewChatMessages()
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Messages (Dark)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailMessagesDark() {
    PreviewTheme(dark = true) {
        val sessions = PreviewSeeds.previewChatSessions()
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = sessions,
                selectedSession = sessions.first(),
                messages = PreviewSeeds.previewChatMessages()
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · Messages empty", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailMessagesEmpty() {
    PreviewTheme(dark = false) {
        val sessions = PreviewSeeds.previewChatSessions()
        DeviceDetailContent(
            deviceName = "Loa khách phòng họp",
            deviceId = "1F7E9D10",
            uiState = VMDeviceDetail.UiState(
                allSessions = sessions,
                selectedSession = sessions.first(),
                messages = emptyList()
            ),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}

@Preview(name = "Detail · No deviceId", showBackground = true, heightDp = 900)
@Composable
private fun PreviewDetailNoDeviceId() {
    PreviewTheme(dark = false) {
        DeviceDetailContent(
            deviceName = "Thiết bị không rõ",
            deviceId = null,
            uiState = VMDeviceDetail.UiState(),
            onBack = {},
            onSelectTab = {},
            onSelectSession = {},
            onClearSelectedSession = {}
        )
    }
}
