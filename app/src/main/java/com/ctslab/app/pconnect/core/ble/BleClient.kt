package com.avis.app.ptalk.core.ble

import kotlinx.coroutines.flow.Flow

interface BleClient {
    fun scanForConfigDevices(): Flow<List<ScannedDevice>>
    suspend fun connect(address: String): BleSession
    suspend fun disconnect(address: String)
}