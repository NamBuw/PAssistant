package com.avis.app.ptalk.domain.control

import com.avis.app.ptalk.core.websocket.ControlResponse
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import com.avis.app.ptalk.core.websocket.DeviceStatusResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.thingai.base.log.ILog

/**
 * WebSocket-backed ControlGateway for real-time device control.
 * This communicates through the server which forwards commands to the device.
 *
 * Protocol matches simulation.py:
 * - request_status: Get device status (battery, volume, version, etc.)
 * - set_volume: Set volume (0-100)
 * - set_brightness: Set brightness (0-100)
 * - set_device_name: Change device name
 * - reboot: Restart device
 * - request_ble_config: Factory reset / enter BLE config mode
 * - request_ota: Request OTA update
 */
class WebSocketControlGateway(
    private val webSocket: DeviceControlWebSocket,
    private val serverUrl: String
) : ControlGateway {
    private val TAG = "WebSocketControlGateway"

    private var currentDeviceId: String? = null

    override val isConnected: Flow<Boolean> = webSocket.connectionState.map {
        it == DeviceControlWebSocket.ConnectionState.CONNECTED
    }

    override suspend fun connect(address: String) {
        // address here is the device MAC/ID, not server URL
        currentDeviceId = address

        // Connect to server if not already connected
        if (webSocket.connectionState.value != DeviceControlWebSocket.ConnectionState.CONNECTED) {
            webSocket.connect(serverUrl)

            // Wait for connection to be established (max 5 seconds)
            ILog.d(TAG, "Waiting for WebSocket connection...")
            var attempts = 0
            while (webSocket.connectionState.value != DeviceControlWebSocket.ConnectionState.CONNECTED && attempts < 50) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            if (webSocket.connectionState.value == DeviceControlWebSocket.ConnectionState.CONNECTED) {
                ILog.d(TAG, "WebSocket connected successfully")
            } else {
                ILog.e(TAG, "WebSocket connection timeout", null)
            }
        }
    }

    override suspend fun disconnect() {
        currentDeviceId = null
        // Don't disconnect WebSocket here as it may be shared
    }

    private fun requireDeviceId(): String =
        currentDeviceId ?: error("No device ID set. Call connect() first.")

    // ============ STATUS ============

    /**
     * Request full device status
     */
    suspend fun requestStatus(): DeviceStatusResponse? {
        ILog.d(TAG, "requestStatus() called for device: ${requireDeviceId()}")
        val response = webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "request_status"
        )
        ILog.d(
            TAG,
            "requestStatus response: isSuccess=${response.isSuccess}, status=${response.status}, deviceResponse=${response.deviceResponse}"
        )
        return if (response.isSuccess) response.deviceResponse else null
    }

    // ============ VOLUME ============

    override suspend fun readVolume(): Int {
        val status = requestStatus()
        return status?.volume ?: 50
    }

    override suspend fun writeVolume(value: Int) {
        val clampedValue = value.coerceIn(0, 100)
        val response = webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "set_volume",
            params = mapOf("volume" to clampedValue)
        )
        if (!response.isSuccess) {
            ILog.e(TAG, "writeVolume failed", response.message)
        }
    }

    // ============ BRIGHTNESS ============

    override suspend fun readBrightness(): Int {
        val status = requestStatus()
        return status?.brightness ?: 50
    }

    override suspend fun writeBrightness(value: Int) {
        val clampedValue = value.coerceIn(0, 100)
        val response = webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "set_brightness",
            params = mapOf("brightness" to clampedValue)
        )
        if (!response.isSuccess) {
            ILog.e(TAG, "writeBrightness failed", response.message)
        }
    }

    // ============ DEVICE NAME ============

    override suspend fun readName(): String {
        val status = requestStatus()
        return status?.deviceName ?: "PTalk Device"
    }

    override suspend fun writeName(value: String) {
        val response = webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "set_device_name",
            params = mapOf("device_name" to value)
        )
        if (!response.isSuccess) {
            ILog.e(TAG, "writeName failed", response.message)
        }
    }

    // ============ WIFI (write-only, config via BLE) ============

    override suspend fun writeWifiSsid(value: String) {
        // WiFi config should be done via BLE, not WebSocket (security)
        ILog.w(TAG, "writeWifiSsid not supported via WebSocket")
    }

    override suspend fun writeWifiPass(value: String) {
        // WiFi config should be done via BLE, not WebSocket (security)
        ILog.w(TAG, "writeWifiPass not supported via WebSocket")
    }

    override suspend fun readWifiList(): List<WifiNetwork> {
        // WiFi scanning should be done via BLE
        return emptyList()
    }

    // ============ METADATA ============

    override suspend fun readAppVersion(): String {
        val status = requestStatus()
        return status?.firmwareVersion ?: "Unknown"
    }

    override suspend fun readBuildInfo(): String {
        return "WebSocket Gateway v1.0"
    }

    override suspend fun saveConfig() {
        // Not applicable for WebSocket - config saved on device side
    }

    override suspend fun readDeviceId(): String {
        return requireDeviceId()
    }

    // ============ DEVICE CONTROL COMMANDS ============

    /**
     * Reboot the device
     */
    suspend fun reboot(): ControlResponse {
        return webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "reboot"
        )
    }

    /**
     * Request device to enter BLE config mode (factory reset WiFi)
     */
    suspend fun requestBleConfig(): ControlResponse {
        return webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "request_ble_config"
        )
    }

    /**
     * Request OTA firmware update
     */
    suspend fun requestOta(version: String? = null, size: Int? = null): ControlResponse {
        val params = mutableMapOf<String, Any>()
        version?.let { params["version"] = it }
        size?.let { params["size"] = it }

        return webSocket.sendControlCommand(
            deviceId = requireDeviceId(),
            command = "request_ota",
            params = params
        )
    }

    /**
     * Get battery level
     */
    suspend fun getBatteryLevel(): Int? {
        val status = requestStatus()
        return status?.batteryLevel
    }
}