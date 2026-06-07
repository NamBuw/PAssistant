package com.ctslab.app.pconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctslab.app.pconnect.core.network.ChildProfileDto
import com.ctslab.app.pconnect.core.network.DashboardApi
import com.ctslab.app.pconnect.core.network.ParentProfileDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import javax.inject.Inject

/**
 * Read-only "Thông tin cá nhân": fetches the parent profile (/api/v1/profile) and the
 * parent's children (/api/v1/children) via the shared Dashboard Bearer client. Display only —
 * no editing happens here.
 */
@HiltViewModel
class VMProfileInfo @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    companion object { private const val TAG = "VMProfileInfo" }

    data class UiState(
        val isLoading: Boolean = false,
        val profile: ParentProfileDto? = null,
        val children: List<ChildProfileDto> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val profile = dashboardApi.getProfile().profile
                val children = dashboardApi.getChildren().children
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    children = children
                )
            } catch (e: Exception) {
                ILog.e(TAG, "refresh", e.message)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không tải được thông tin"
                )
            }
        }
    }
}
