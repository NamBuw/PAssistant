package com.ctslab.app.pconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctslab.app.pconnect.core.network.AddWordRequest
import com.ctslab.app.pconnect.core.network.BannedTopicDto
import com.ctslab.app.pconnect.core.network.BannedWordDto
import com.ctslab.app.pconnect.core.network.DashboardApi
import com.ctslab.app.pconnect.core.network.SuggestTopicRequest
import com.ctslab.app.pconnect.core.network.ToggleTopicRequest
import com.ctslab.app.pconnect.core.network.UpdateWordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import javax.inject.Inject

/**
 * Banned words + topics management (calls the Dashboard /api/v1 Bearer endpoints).
 * A topic is Gemma-expanded into words server-side; the app just manages them.
 */
@HiltViewModel
class VMBannedWords @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    companion object { private const val TAG = "VMBannedWords" }

    data class UiState(
        val isLoading: Boolean = false,
        val suggesting: Boolean = false,
        val words: List<BannedWordDto> = emptyList(),   // standalone words (topicId == null)
        val topics: List<BannedTopicDto> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val words = dashboardApi.getBannedWords().words.filter { it.topicId == null }
                val topics = dashboardApi.getBannedTopics().topics
                _uiState.value = _uiState.value.copy(isLoading = false, words = words, topics = topics)
            } catch (e: Exception) {
                ILog.e(TAG, "refresh", e.message)
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Không tải được dữ liệu")
            }
        }
    }

    fun addWord(word: String, category: String = "general", topicId: String? = null) {
        if (word.isBlank()) return
        viewModelScope.launch {
            try {
                dashboardApi.addBannedWord(AddWordRequest(word = word.trim(), category = category, topicId = topicId))
                refresh()
            } catch (e: Exception) { setError(e) }
        }
    }

    fun toggleWord(id: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                dashboardApi.updateBannedWord(UpdateWordRequest(id = id, isActive = !isActive))
                refresh()
            } catch (e: Exception) { setError(e) }
        }
    }

    fun deleteWord(id: String) {
        viewModelScope.launch {
            try { dashboardApi.deleteBannedWord(id); refresh() } catch (e: Exception) { setError(e) }
        }
    }

    fun suggestTopic(topic: String) {
        if (topic.isBlank()) return
        _uiState.value = _uiState.value.copy(suggesting = true, error = null)
        viewModelScope.launch {
            try {
                dashboardApi.suggestBannedTopic(request = SuggestTopicRequest(topic = topic.trim()))
                _uiState.value = _uiState.value.copy(suggesting = false)
                refresh()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(suggesting = false)
                setError(e)
            }
        }
    }

    fun toggleTopic(id: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                dashboardApi.toggleBannedTopic(ToggleTopicRequest(id = id, isActive = !isActive))
                refresh()
            } catch (e: Exception) { setError(e) }
        }
    }

    fun deleteTopic(id: String) {
        viewModelScope.launch {
            try { dashboardApi.deleteBannedTopic(id); refresh() } catch (e: Exception) { setError(e) }
        }
    }

    private fun setError(e: Exception) {
        ILog.e(TAG, "error", e.message)
        _uiState.value = _uiState.value.copy(error = e.message ?: "Có lỗi xảy ra")
    }
}
