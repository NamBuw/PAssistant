package com.avis.app.ptalk.ui.viewmodel.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.domain.data.local.repo.OIDCAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMOIDCLogin @Inject constructor(
    private val oidcRepo: OIDCAuthRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val authPending: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Check if there's an existing valid session on app start.
     * If there's a refresh token but the access token expired, try to refresh.
     */
    fun checkExistingSession() {
        if (oidcRepo.isAuthorized()) {
            _uiState.value = UiState(success = true)
            return
        }

        // Try refresh if we have a refresh token
        if (oidcRepo.hasRefreshToken()) {
            _uiState.value = UiState(isLoading = true)
            viewModelScope.launch {
                val refreshed = oidcRepo.refreshToken()
                if (refreshed) {
                    _uiState.value = UiState(success = true)
                } else {
                    oidcRepo.logout()
                    _uiState.value = UiState()
                }
            }
        }
    }

    /**
     * Launch the Authentik browser login flow.
     */
    fun initiateLogin(activity: Activity) {
        _uiState.value = UiState(isLoading = true, authPending = true)

        viewModelScope.launch {
            try {
                android.util.Log.d("VMOIDCLogin", "Initiating SSO login...")
                val intent = oidcRepo.createLoginIntent(activity)
                android.util.Log.d("VMOIDCLogin", "Auth intent created successfully")
                _pendingAuthIntent.value = intent
            } catch (e: Exception) {
                android.util.Log.e("VMOIDCLogin", "Failed to initiate login", e)
                _uiState.value = UiState(
                    error = e.message ?: "Không thể khởi tạo đăng nhập"
                )
            }
        }
    }

    // Pending auth intent for the UI to launch
    private val _pendingAuthIntent = MutableStateFlow<Intent?>(null)
    val pendingAuthIntent: StateFlow<Intent?> = _pendingAuthIntent.asStateFlow()

    /**
     * Called when the pending auth intent has been consumed (launched).
     */
    fun onAuthIntentConsumed() {
        _pendingAuthIntent.value = null
    }

    /**
     * Handle the authorization callback from the browser.
     * This is called when the browser redirects back to the app.
     */
    fun handleAuthCallback(intent: Intent) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val authResponse = oidcRepo.handleAuthorizationResponse(intent)

                if (authResponse == null) {
                    _uiState.value = UiState(
                        error = "Đăng nhập thất bại hoặc đã bị hủy"
                    )
                    return@launch
                }

                // Exchange authorization code for tokens
                val success = oidcRepo.exchangeToken(authResponse)

                if (success) {
                    _uiState.value = UiState(success = true)
                } else {
                    _uiState.value = UiState(
                        error = "Không thể lấy token xác thực"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState(
                    error = e.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
