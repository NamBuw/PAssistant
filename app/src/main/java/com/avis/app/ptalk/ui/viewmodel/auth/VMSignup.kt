package com.avis.app.ptalk.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.network.DashboardApi
import com.avis.app.ptalk.core.network.SignupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMSignup @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = UiState(error = "Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (password.length < 8) {
            _uiState.value = UiState(error = "Mật khẩu phải có ít nhất 8 ký tự")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = UiState(error = "Mật khẩu không khớp")
            return
        }

        _uiState.value = UiState(isLoading = true)

        viewModelScope.launch {
            try {
                val response = dashboardApi.signup(
                    SignupRequest(username, email, password, confirmPassword)
                )
                if (response.success) {
                    _uiState.value = UiState(success = true)
                } else {
                    _uiState.value = UiState(error = response.error ?: "Đăng ký thất bại")
                }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("409") == true -> "Tên đăng nhập hoặc email đã tồn tại"
                    e.message?.contains("400") == true -> "Thông tin không hợp lệ"
                    else -> "Không kết nối được server. Kiểm tra mạng."
                }
                _uiState.value = UiState(error = msg)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
