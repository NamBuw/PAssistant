package com.avis.app.ptalk.domain.control

import kotlinx.coroutines.flow.Flow

interface ControlGateway {
    val isConnected: Flow<Boolean>
    suspend fun connect(address: String)
    suspend fun disconnect()
    suspend fun readName(): String
    suspend fun writeName(value: String)
    suspend fun readVolume(): Int
    suspend fun writeVolume(value: Int)
    suspend fun readBrightness(): Int
    suspend fun writeBrightness(value: Int)

    suspend fun writeWifiSsid(value: String)
    suspend fun writeWifiPass(value: String)
    suspend fun readAppVersion(): String
    suspend fun readBuildInfo(): String
    suspend fun saveConfig()
    suspend fun readDeviceId(): String
    suspend fun readWifiList(): List<WifiNetwork>
}

data class WifiNetwork(
    val ssid: String,
    val rssi: Int
)