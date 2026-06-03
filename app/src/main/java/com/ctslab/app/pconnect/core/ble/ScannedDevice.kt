package com.ctslab.app.pconnect.core.ble

data class ScannedDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
    val hasConfigService: Boolean
)