package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.network.ChatMessageResponse
import com.avis.app.ptalk.core.network.ChatSessionResponse
import com.avis.app.ptalk.core.network.DashboardApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import javax.inject.Inject

@HiltViewModel
class VMDeviceDetail @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    companion object {
        private const val TAG = "VMDeviceDetail"
    }

    enum class ChatTab(val label: String, val productSource: String) {
        PTALK("PTalk", "ptalk"),
        KID_MENTOR("KidMentor", "kid_mentor")
    }

    data class UiState(
        val isLoading: Boolean = false,
        val allSessions: List<ChatSessionResponse> = emptyList(),
        val selectedTab: ChatTab = ChatTab.PTALK,
        val selectedSession: ChatSessionResponse? = null,
        val messages: List<ChatMessageResponse> = emptyList(),
        val isLoadingMessages: Boolean = false,
        val error: String? = null
    ) {
        val sessions: List<ChatSessionResponse>
            get() = allSessions.filter { it.productSource == selectedTab.productSource }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Load chat sessions for a specific device
     */
    fun loadSessions(deviceId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = dashboardApi.getChatSessions(
                    deviceId = deviceId,
                    productSource = null,
                    limit = 50
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allSessions = response.sessions
                )
                ILog.d(TAG, "loadSessions", "Loaded ${response.sessions.size} sessions for device $deviceId")
            } catch (e: Exception) {
                ILog.e(TAG, "loadSessions", e.message)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không thể tải lịch sử chat"
                )
            }
        }
    }

    /**
     * Load messages for a specific chat session
     */
    fun loadMessages(sessionId: String) {
        _uiState.value = _uiState.value.copy(isLoadingMessages = true, error = null)
        viewModelScope.launch {
            try {
                val response = dashboardApi.getChatMessages(
                    sessionId = sessionId,
                    limit = 100
                )
                val session = _uiState.value.sessions.find { it.id == sessionId }
                _uiState.value = _uiState.value.copy(
                    isLoadingMessages = false,
                    selectedSession = session,
                    messages = response.messages
                )
                ILog.d(TAG, "loadMessages", "Loaded ${response.messages.size} messages for session $sessionId")
            } catch (e: Exception) {
                ILog.e(TAG, "loadMessages", e.message)
                _uiState.value = _uiState.value.copy(
                    isLoadingMessages = false,
                    error = e.message ?: "Không thể tải tin nhắn"
                )
            }
        }
    }

    fun selectTab(tab: ChatTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    /**
     * Go back to session list
     */
    fun clearSelectedSession() {
        _uiState.value = _uiState.value.copy(
            selectedSession = null,
            messages = emptyList()
        )
    }
}
