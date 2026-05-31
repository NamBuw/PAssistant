package com.avis.app.ptalk.ui.screen.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewSeeds
import com.avis.app.ptalk.ui.preview.PreviewTheme

@Preview(name = "Control · Online (Light)", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlOnlineLight() {
    PreviewTheme(dark = false) {
        ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.Online),
            isConnected = true,
            isLoading = false,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}

@Preview(name = "Control · Online (Dark)", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlOnlineDark() {
    PreviewTheme(dark = true) {
        ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.Online),
            isConnected = true,
            isLoading = false,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}

@Preview(name = "Control · Low battery", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlLowBattery() {
    PreviewTheme(dark = false) {
        ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.LowBattery),
            isConnected = true,
            isLoading = false,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}

@Preview(name = "Control · Offline", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlOffline() {
    PreviewTheme(dark = false) {
        ControlContent(
            deviceName = "Loa khách phòng họp",
            localDeviceName = "Loa khách phòng họp",
            status = PreviewSeeds.previewDeviceStatus(PreviewSeeds.StatusScenario.Offline),
            isConnected = true,
            isLoading = false,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}

@Preview(name = "Control · Waiting device", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlWaiting() {
    PreviewTheme(dark = false) {
        ControlContent(
            deviceName = "Loa phòng bé Bin",
            localDeviceName = "Loa phòng bé Bin",
            status = null,
            isConnected = true,
            isLoading = false,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}

@Preview(name = "Control · Connecting MQTT", showBackground = true, heightDp = 1000)
@Composable
private fun PreviewControlConnecting() {
    PreviewTheme(dark = false) {
        ControlContent(
            deviceName = "Loa phòng bé Bin",
            localDeviceName = null,
            status = null,
            isConnected = false,
            isLoading = true,
            lastError = null,
            onBack = {},
            onClearError = {},
            onSetVolume = {},
            onSetBrightness = {},
            onSetDeviceName = {},
            onResetWifi = {},
            onReboot = {}
        )
    }
}
