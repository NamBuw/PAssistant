package com.ctslab.app.pconnect.core.network.authentik

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import org.json.JSONObject

/**
 * Manages the OIDC session (AuthState) backed by SharedPreferences.
 *
 * This replaces the old TokenManager for OIDC-based authentication.
 * Stores the full AuthState JSON (access_token, refresh_token, id_token, metadata)
 * and exposes a reactive isLoggedIn StateFlow for the UI.
 */
class OIDCSessionManager(context: Context) {

    companion object {
        private const val TAG = "OIDCSession"
        private const val PREFS_NAME = "ptalk_oidc_session"
        private const val KEY_AUTH_STATE = "auth_state_json"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow(loadAuthState())
    private val _isLoggedIn = MutableStateFlow(_authState.value.isAuthorized)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // ── persistence helpers ──────────────────────────────────────────────

    private fun loadAuthState(): AuthState {
        val json = prefs.getString(KEY_AUTH_STATE, null)
        return if (json != null) {
            try {
                AuthState.jsonDeserialize(json)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to deserialize AuthState, creating empty", e)
                AuthState()
            }
        } else {
            AuthState()
        }
    }

    private fun persistAuthState(state: AuthState) {
        prefs.edit { putString(KEY_AUTH_STATE, state.jsonSerializeString()) }
        _authState.value = state
        _isLoggedIn.value = state.isAuthorized
    }

    // ── public API ──────────────────────────────────────────────────────

    /** Update the stored state after an authorization flow completes. */
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        exception: AuthorizationException?
    ) {
        val current = _authState.value
        current.update(response, exception)
        persistAuthState(current)
        Log.d(TAG, "Auth state updated after authorization: authorized=${current.isAuthorized}")
    }

    /** Update the stored state after a token exchange completes. */
    fun updateAfterTokenResponse(
        response: TokenResponse?,
        exception: AuthorizationException?
    ) {
        val current = _authState.value
        current.update(response, exception)
        persistAuthState(current)
        Log.d(TAG, "Auth state updated after token response: authorized=${current.isAuthorized}")
    }

    /** Get the current access token, or null if not available / expired. */
    fun getAccessToken(): String? {
        return _authState.value.accessToken
    }

    /**
     * Return a VALID access token, transparently refreshing it via the refresh token when
     * the cached one has expired (Authentik access tokens live ~1h). BLOCKS on the refresh
     * network call, so call only off the main thread (it is used from an OkHttp interceptor).
     * Returns null if there is no authorized session or the refresh fails (e.g. no refresh
     * token — the session must request the `offline_access` scope to get one).
     */
    fun getFreshAccessTokenBlocking(service: AuthorizationService): String? {
        val state = _authState.value
        if (!state.isAuthorized) return null
        val latch = java.util.concurrent.CountDownLatch(1)
        val tokenRef = arrayOfNulls<String>(1)
        state.performActionWithFreshTokens(service) { accessToken, _, ex ->
            if (ex != null) Log.e(TAG, "Fresh-token refresh failed", ex)
            tokenRef[0] = accessToken
            latch.countDown()
        }
        val completed = try {
            latch.await(20, java.util.concurrent.TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }
        // performActionWithFreshTokens mutates `state` in place on a successful refresh;
        // persist so the new access/refresh tokens survive a process restart.
        if (completed) persistAuthState(state)
        return tokenRef[0]
    }

    /** Get the current refresh token, or null if not available. */
    fun getRefreshToken(): String? {
        return _authState.value.refreshToken
    }

    /** Get the raw ID token string. */
    fun getIdToken(): String? {
        return _authState.value.idToken
    }

    /**
     * Parse the ID token claims without cryptographic verification.
     * For production, consider verifying the signature via JWKS.
     */
    fun getIdTokenClaims(): UserProfile? {
        val idToken = _authState.value.idToken ?: return null
        return try {
            val parts = idToken.split(".")
            if (parts.size < 2) return null
            val payload = android.util.Base64.decode(
                parts[1],
                android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING
            )
            val json = JSONObject(String(payload, Charsets.UTF_8))

            val roles = mutableListOf<String>()
            json.optJSONArray("roles")?.let { arr ->
                for (i in 0 until arr.length()) {
                    arr.optString(i)?.let { roles.add(it) }
                }
            }

            val assignedProducts = mutableListOf<String>()
            json.optJSONArray("assigned_products")?.let { arr ->
                for (i in 0 until arr.length()) {
                    arr.optString(i)?.let { assignedProducts.add(it) }
                }
            }

            UserProfile(
                sub = json.optString("sub", ""),
                email = json.optString("email", null),
                name = json.optString("name", null),
                preferredUsername = json.optString("preferred_username", null),
                roles = roles,
                userType = json.optString("user_type", null),
                assignedProducts = assignedProducts
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse ID token", e)
            null
        }
    }

    /**
     * Perform a token refresh using the current refresh token.
     * Returns true if the refresh was successful.
     */
    suspend fun performTokenRefresh(
        service: AuthorizationService
    ): Boolean {
        val state = _authState.value
        val refreshRequest = state.createTokenRefreshRequest() ?: run {
            Log.w(TAG, "No refresh token available")
            return false
        }

        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            service.performTokenRequest(refreshRequest) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    updateAfterTokenResponse(tokenResponse, ex)
                    Log.d(TAG, "Token refresh successful")
                    cont.resumeWith(Result.success(true))
                } else {
                    Log.e(TAG, "Token refresh failed", ex)
                    cont.resumeWith(Result.success(false))
                }
            }
        }
    }

    /** Whether the current AuthState has a valid authorized session. */
    fun isAuthorized(): Boolean = _authState.value.isAuthorized

    /** Whether a refresh token is available for token refresh. */
    fun hasRefreshToken(): Boolean = !_authState.value.refreshToken.isNullOrBlank()

    /** Clear the entire OIDC session (logout). */
    fun clearSession() {
        persistAuthState(AuthState())
        Log.d(TAG, "Session cleared")
    }

    /** Get the current AuthState (for advanced use). */
    fun getAuthState(): AuthState = _authState.value
}

/**
 * Parsed user profile from the ID token claims.
 */
data class UserProfile(
    val sub: String,
    val email: String?,
    val name: String?,
    val preferredUsername: String?,
    val roles: List<String>,
    val userType: String?,
    val assignedProducts: List<String>
)
