package com.avis.app.ptalk.core.ble

data class ScannedDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
    val hasConfigService: Boolean
)