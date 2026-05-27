package com.avis.app.ptalk.domain.data.local.repo

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.avis.app.ptalk.core.network.authentik.AuthentikConfig
import com.avis.app.ptalk.core.network.authentik.OIDCSessionManager
import com.avis.app.ptalk.core.network.authentik.UserProfile
import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that manages authentication via Authentik's OIDC flow.
 *
 * This replaces the legacy AuthRepository for the login flow.
 * Registration still uses the legacy AuthApi.
 */
@Singleton
class OIDCAuthRepository @Inject constructor(
    private val authService: AuthorizationService,
    private val sessionManager: OIDCSessionManager
) {
    companion object {
        private const val TAG = "OIDCAuthRepo"
    }

    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn

    /**
     * Launch the Authentik browser login flow.
     * Returns an Intent that should be launched with startActivityForResult or the
     * Activity Result API.
     */
    suspend fun createLoginIntent(activity: Activity): Intent {
        val config = AuthentikConfig.fetchServiceConfig(activity)
        val authRequest = AuthentikConfig.buildAuthorizationRequest(config)
        Log.d(TAG, "Launching auth request to: ${authRequest.toUri()}")
        return authService.getAuthorizationRequestIntent(authRequest)
    }

    /**
     * Handle the authorization callback from the browser.
     * Returns the authorization response if successful, null otherwise.
     */
    fun handleAuthorizationResponse(intent: Intent): AuthorizationResponse? {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        sessionManager.updateAfterAuthorization(response, exception)

        if (exception != null) {
            Log.e(TAG, "Authorization error: ${exception.error} - ${exception.errorDescription}")
            return null
        }

        Log.d(TAG, "Authorization successful, code received")
        return response
    }

    /**
     * Exchange the authorization code for tokens.
     * Returns true if the exchange was successful.
     */
    suspend fun exchangeToken(authResponse: AuthorizationResponse): Boolean {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            val tokenRequest = net.openid.appauth.TokenRequest.Builder(
                authResponse.request.configuration,
                authResponse.request.clientId
            )
                .setGrantType(net.openid.appauth.GrantTypeValues.AUTHORIZATION_CODE)
                .setAuthorizationCode(authResponse.authorizationCode)
                .setRedirectUri(authResponse.request.redirectUri)
                .setCodeVerifier(authResponse.request.codeVerifier)
                .setAdditionalParameters(mapOf("client_secret" to AuthentikConfig.CLIENT_SECRET))
                .build()

            authService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    sessionManager.updateAfterTokenResponse(tokenResponse, ex)
                    Log.d(TAG, "Token exchange successful")
                    cont.resumeWith(Result.success(true))
                } else {
                    Log.e(TAG, "Token exchange failed: ${ex?.error} - ${ex?.errorDescription}", ex)
                    cont.resumeWith(Result.success(false))
                }
            }
        }
    }

    /**
     * Refresh the access token using the stored refresh token.
     * Returns true if the refresh was successful.
     */
    suspend fun refreshToken(): Boolean {
        return sessionManager.performTokenRefresh(authService)
    }

    /**
     * Get the current access token. Returns null if not authorized.
     */
    fun getAccessToken(): String? = sessionManager.getAccessToken()

    /**
     * Get the parsed user profile from the ID token.
     */
    fun getUserProfile(): UserProfile? = sessionManager.getIdTokenClaims()

    /**
     * Check if the user has a valid session.
     */
    fun isAuthorized(): Boolean = sessionManager.isAuthorized()

    /**
     * Check if a refresh token is available.
     */
    fun hasRefreshToken(): Boolean = sessionManager.hasRefreshToken()

    /**
     * Logout — clear the local session.
     * Optionally, you could also call the Authentik end-session endpoint.
     */
    fun logout() {
        sessionManager.clearSession()
        Log.d(TAG, "User logged out")
    }
}
