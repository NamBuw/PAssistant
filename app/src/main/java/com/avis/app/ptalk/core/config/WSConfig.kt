package com.avis.app.ptalk.core.config

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit

class WSConfig(context: Context) {
    companion object {
        private const val PREFS_NAME = "ptalk_server_config"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_SERVER_HOST = "server_host"
        private const val KEY_SERVER_PORT = "server_port"

        // Default values
        const val DEFAULT_HOST = "auth.ctslab.net"
        const val DEFAULT_PORT = 443
        const val DEFAULT_WS_PATH = "/v2/ws"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _serverUrl = MutableStateFlow(getServerUrl())
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    fun getServerHost(): String {
        return prefs.getString(KEY_SERVER_HOST, DEFAULT_HOST) ?: DEFAULT_HOST
    }

    fun setServerHost(host: String) {
        prefs.edit { putString(KEY_SERVER_HOST, host) }
        updateServerUrl()
    }

    fun getServerPort(): Int {
        return prefs.getInt(KEY_SERVER_PORT, DEFAULT_PORT)
    }

    fun setServerPort(port: Int) {
        prefs.edit { putInt(KEY_SERVER_PORT, port) }
        updateServerUrl()
    }

    fun getServerUrl(): String {
        val customUrl = prefs.getString(KEY_SERVER_URL, null)
        if (!customUrl.isNullOrBlank()) {
            return customUrl
        }
        return buildServerUrl()
    }

    fun setServerUrl(url: String) {
        prefs.edit { putString(KEY_SERVER_URL, url) }
        _serverUrl.value = url
    }

    private fun buildServerUrl(): String {
        val host = getServerHost()
        val port = getServerPort()
        return if (port == 443) "wss://$host$DEFAULT_WS_PATH" else "ws://$host:$port$DEFAULT_WS_PATH"
    }

    private fun updateServerUrl() {
        val url = buildServerUrl()
        prefs.edit { putString(KEY_SERVER_URL, url) }
        _serverUrl.value = url
    }

    fun resetToDefaults() {
        prefs.edit {
            putString(KEY_SERVER_HOST, DEFAULT_HOST)
                .putInt(KEY_SERVER_PORT, DEFAULT_PORT)
                .remove(KEY_SERVER_URL)
        }
        _serverUrl.value = buildServerUrl()
    }
}