package com.avis.app.ptalk.ui.screen.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.avis.app.ptalk.ui.preview.PreviewSeeds
import com.avis.app.ptalk.ui.preview.PreviewTheme
import com.avis.app.ptalk.ui.viewmodel.VMConfigDevice

@Preview(name = "Scan · Permission missing", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanPermissionMissing() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = false,
            isBluetoothEnabled = false,
            isLocationEnabled = false,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · Bluetooth off", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanBluetoothOff() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = true,
            isBluetoothEnabled = false,
            isLocationEnabled = true,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · Location off", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanLocationOff() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(),
            permissionsGranted = true,
            isBluetoothEnabled = true,
            isLocationEnabled = false,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · Radar empty", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanRadarEmpty() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(scanning = true),
            permissionsGranted = true,
            isBluetoothEnabled = true,
            isLocationEnabled = true,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · Radar with devices", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanRadarWithDevices() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(
                scanning = true,
                devices = PreviewSeeds.previewScannedDevices(5)
            ),
            permissionsGranted = true,
            isBluetoothEnabled = true,
            isLocationEnabled = true,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · List view", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanList() {
    PreviewTheme(dark = false) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(
                scanning = false,
                devices = PreviewSeeds.previewScannedDevices(5)
            ),
            permissionsGranted = true,
            isBluetoothEnabled = true,
            isLocationEnabled = true,
            showAsList = true,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}

@Preview(name = "Scan · Radar (Dark)", showBackground = true, heightDp = 900)
@Composable
private fun PreviewScanRadarDark() {
    PreviewTheme(dark = true) {
        ScanDeviceContent(
            uiState = VMConfigDevice.UiState(
                scanning = true,
                devices = PreviewSeeds.previewScannedDevices(3)
            ),
            permissionsGranted = true,
            isBluetoothEnabled = true,
            isLocationEnabled = true,
            showAsList = false,
            onToggleView = {},
            onBack = {},
            onRequestPermission = {},
            onOpenBluetoothSettings = {},
            onOpenLocationSettings = {},
            onScanToggle = {},
            onDeviceClick = {}
        )
    }
}
