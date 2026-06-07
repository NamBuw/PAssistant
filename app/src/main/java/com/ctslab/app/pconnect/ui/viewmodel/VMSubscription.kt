package com.ctslab.app.pconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ctslab.app.pconnect.core.network.DashboardApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import javax.inject.Inject

/**
 * Resolves the current subscription tier for the "Gói đăng ký" screen.
 *
 * The tier is the DB source of truth — `users.subscription_tier` on the PARENT account,
 * fetched from /api/v1/profile (subscriptionTier / isSuperuser). The Authentik JWT carries
 * NO subscription_tier claim, so it is never decoded here; if the profile call fails we
 * fall back to "basic" (fail-safe, never shows a paid tier the user doesn't have).
 */
@HiltViewModel
class VMSubscription @Inject constructor(
    private val dashboardApi: DashboardApi
) : ViewModel() {

    companion object { private const val TAG = "VMSubscription" }

    /** Current tier from the DB; "basic" until resolved (and on any failure). */
    private val _currentTier = MutableStateFlow("basic")
    val currentTier: StateFlow<String> = _currentTier.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            try {
                val profile = dashboardApi.getProfile().profile
                _currentTier.value = when {
                    profile == null -> "basic"
                    profile.isSuperuser -> "admin"
                    else -> profile.subscriptionTier?.ifBlank { null } ?: "basic"
                }
            } catch (e: Exception) {
                ILog.e(TAG, "refresh", e.message)
                _currentTier.value = "basic"   // guest/offline fallback
            }
        }
    }
}
