# Authentik SSO Integration — Changelog

> This document records all changes made to the PAssistant Android project to integrate Authentik OIDC/SSO authentication.

---

## Overview

The PAssistant app previously used a custom username/password REST flow (`POST /auth/login` to `https://auth.ctslab.net/`). This change integrates **Authentik's OAuth2/OIDC Authorization Code + PKCE flow** as the primary login method, while preserving the legacy login as a fallback during migration.

**Authentik OIDC App Config** (pre-configured in `D:\AppDev\Dashboard\authentik\blueprints\system-setup.yaml`):

| Field | Value |
|-------|-------|
| Issuer URL | `https://auth.ctslab.net/application/o/p-assistant/` |
| Client ID | `p-assistant-client` |
| Client Secret | `p-assistant-secret-key` |
| Redirect URI | `app://passistant/callback` |
| Scopes | `openid email profile roles user_type assigned_products` |
| Access Token TTL | 5 minutes |
| Refresh Token TTL | 7 days |

---

## Files Created (4)

### 1. `app/src/main/java/com/avis/app/ptalk/core/network/authentik/AuthentikConfig.kt`

Centralized OIDC configuration. Fetches the `.well-known/openid-configuration` discovery document from the Authentik issuer, caches it, and builds `AuthorizationRequest` objects with PKCE.

```kotlin
package com.avis.app.ptalk.core.network.authentik

import android.content.Context
import android.net.Uri
import com.avis.app.ptalk.BuildConfig
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.util.concurrent.atomic.AtomicReference

object AuthentikConfig {

    val ISSUER: String = BuildConfig.AUTHENTIK_ISSUER
    val CLIENT_ID: String = BuildConfig.AUTHENTIK_CLIENT_ID
    val CLIENT_SECRET: String = BuildConfig.AUTHENTIK_CLIENT_SECRET
    val REDIRECT_URI: Uri = Uri.parse(BuildConfig.AUTHENTIK_REDIRECT_URI)
    val SCOPES: List<String> = BuildConfig.AUTHENTIK_SCOPES.split(" ")

    private val cachedConfig = AtomicReference<AuthorizationServiceConfiguration?>()

    suspend fun fetchServiceConfig(
        context: Context
    ): AuthorizationServiceConfiguration {
        cachedConfig.get()?.let { return it }

        val config = kotlinx.coroutines.suspendCancellableCoroutine<AuthorizationServiceConfiguration> { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(ISSUER)) { serviceConfig, ex ->
                if (ex != null || serviceConfig == null) {
                    cont.resumeWith(Result.failure(
                        ex ?: IllegalStateException("Failed to fetch OIDC configuration from $ISSUER")
                    ))
                } else {
                    cachedConfig.set(serviceConfig)
                    cont.resumeWith(Result.success(serviceConfig))
                }
            }
        }
        return config
    }

    fun buildAuthorizationRequest(
        serviceConfig: AuthorizationServiceConfiguration
    ): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            serviceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI
        )
            .setScopes(SCOPES)
            .build()
    }
}
```

---

### 2. `app/src/main/java/com/avis/app/ptalk/core/network/authentik/OIDCSessionManager.kt`

Manages the OIDC session using AppAuth's `AuthState`, persisted in `SharedPreferences`. Exposes a reactive `isLoggedIn` StateFlow for the UI, handles token refresh, and parses ID token claims into a `UserProfile` data class.

```kotlin
package com.avis.app.ptalk.core.network.authentik

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

    private fun loadAuthState(): AuthState {
        val json = prefs.getString(KEY_AUTH_STATE, null)
        return if (json != null) {
            try { AuthState.jsonDeserialize(json) }
            catch (e: Exception) { AuthState() }
        } else AuthState()
    }

    private fun persistAuthState(state: AuthState) {
        prefs.edit { putString(KEY_AUTH_STATE, state.jsonSerializeString()) }
        _authState.value = state
        _isLoggedIn.value = state.isAuthorized
    }

    fun updateAfterAuthorization(response: AuthorizationResponse?, exception: AuthorizationException?) {
        val current = _authState.value
        current.update(response, exception)
        persistAuthState(current)
    }

    fun updateAfterTokenResponse(response: TokenResponse?, exception: AuthorizationException?) {
        val current = _authState.value
        current.update(response, exception)
        persistAuthState(current)
    }

    fun getAccessToken(): String? = _authState.value.accessToken
    fun getRefreshToken(): String? = _authState.value.refreshToken
    fun getIdToken(): String? = _authState.value.idToken

    fun getIdTokenClaims(): UserProfile? {
        val idToken = _authState.value.idToken ?: return null
        return try {
            val parts = idToken.split(".")
            if (parts.size < 2) return null
            val payload = android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING)
            val json = JSONObject(String(payload, Charsets.UTF_8))

            val roles = mutableListOf<String>()
            json.optJSONArray("roles")?.let { arr -> for (i in 0 until arr.length()) { roles.add(arr.optString(i)) } }

            val assignedProducts = mutableListOf<String>()
            json.optJSONArray("assigned_products")?.let { arr -> for (i in 0 until arr.length()) { assignedProducts.add(arr.optString(i)) } }

            UserProfile(
                sub = json.optString("sub", ""),
                email = json.optString("email", null),
                name = json.optString("name", null),
                preferredUsername = json.optString("preferred_username", null),
                roles = roles,
                userType = json.optString("user_type", null),
                assignedProducts = assignedProducts
            )
        } catch (e: Exception) { null }
    }

    suspend fun performTokenRefresh(service: AuthorizationService): Boolean {
        val state = _authState.value
        val refreshRequest = state.createTokenRefreshRequest() ?: return false
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            service.performTokenRequest(refreshRequest) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    updateAfterTokenResponse(tokenResponse, ex)
                    cont.resumeWith(Result.success(true))
                } else {
                    cont.resumeWith(Result.success(false))
                }
            }
        }
    }

    fun isAuthorized(): Boolean = _authState.value.isAuthorized
    fun hasRefreshToken(): Boolean = !_authState.value.refreshToken.isNullOrBlank()
    fun clearSession() { persistAuthState(AuthState()) }
    fun getAuthState(): AuthState = _authState.value
}

data class UserProfile(
    val sub: String,
    val email: String?,
    val name: String?,
    val preferredUsername: String?,
    val roles: List<String>,
    val userType: String?,
    val assignedProducts: List<String>
)
```

---

### 3. `app/src/main/java/com/avis/app/ptalk/domain/data/local/repo/OIDCAuthRepository.kt`

Orchestrates the full OIDC authentication lifecycle: creates the login intent, handles the browser callback, exchanges the authorization code for tokens, and manages refresh/logout.

```kotlin
package com.avis.app.ptalk.domain.data.local.repo

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.avis.app.ptalk.core.network.authentik.AuthentikConfig
import com.avis.app.ptalk.core.network.authentik.OIDCSessionManager
import com.avis.app.ptalk.core.network.authentik.UserProfile
import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OIDCAuthRepository @Inject constructor(
    private val authService: AuthorizationService,
    private val sessionManager: OIDCSessionManager
) {
    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn

    suspend fun createLoginIntent(activity: Activity): Intent {
        val config = AuthentikConfig.fetchServiceConfig(activity)
        val authRequest = AuthentikConfig.buildAuthorizationRequest(config)
        return authService.getAuthorizationRequestIntent(authRequest)
    }

    fun handleAuthorizationResponse(intent: Intent): AuthorizationResponse? {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = net.openid.appauth.AuthorizationException.fromIntent(intent)
        sessionManager.updateAfterAuthorization(response, exception)
        if (exception != null) return null
        return response
    }

    suspend fun exchangeToken(authResponse: AuthorizationResponse): Boolean {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            authService.performTokenRequest(authResponse.createTokenExchangeRequest()) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    sessionManager.updateAfterTokenResponse(tokenResponse, ex)
                    cont.resumeWith(Result.success(true))
                } else {
                    cont.resumeWith(Result.success(false))
                }
            }
        }
    }

    suspend fun refreshToken(): Boolean = sessionManager.performTokenRefresh(authService)
    fun getAccessToken(): String? = sessionManager.getAccessToken()
    fun getUserProfile(): UserProfile? = sessionManager.getIdTokenClaims()
    fun isAuthorized(): Boolean = sessionManager.isAuthorized()
    fun hasRefreshToken(): Boolean = sessionManager.hasRefreshToken()
    fun logout() { sessionManager.clearSession() }
}
```

---

### 4. `app/src/main/java/com/avis/app/ptalk/ui/viewmodel/auth/VMOIDCLogin.kt`

Hilt ViewModel for the OIDC login screen. Manages the pending auth intent, handles browser callbacks, and exposes UI state (loading, error, success).

```kotlin
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

    private val _pendingAuthIntent = MutableStateFlow<Intent?>(null)
    val pendingAuthIntent: StateFlow<Intent?> = _pendingAuthIntent.asStateFlow()

    fun checkExistingSession() {
        if (oidcRepo.isAuthorized()) {
            _uiState.value = UiState(success = true)
            return
        }
        if (oidcRepo.hasRefreshToken()) {
            _uiState.value = UiState(isLoading = true)
            viewModelScope.launch {
                val refreshed = oidcRepo.refreshToken()
                _uiState.value = if (refreshed) UiState(success = true) else UiState()
            }
        }
    }

    fun initiateLogin(activity: Activity) {
        _uiState.value = UiState(isLoading = true, authPending = true)
        viewModelScope.launch {
            try {
                val intent = oidcRepo.createLoginIntent(activity)
                _pendingAuthIntent.value = intent
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message ?: "Không thể khởi tạo đăng nhập")
            }
        }
    }

    fun onAuthIntentConsumed() { _pendingAuthIntent.value = null }

    fun handleAuthCallback(intent: Intent) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val authResponse = oidcRepo.handleAuthorizationResponse(intent)
                if (authResponse == null) {
                    _uiState.value = UiState(error = "Đăng nhập thất bại hoặc đã bị hủy")
                    return@launch
                }
                val success = oidcRepo.exchangeToken(authResponse)
                _uiState.value = if (success) UiState(success = true) else UiState(error = "Không thể lấy token xác thực")
            } catch (e: Exception) {
                _uiState.value = UiState(error = e.message ?: "Đăng nhập thất bại")
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
}
```

---

## Files Modified (7)

### 5. `gradle/libs.versions.toml`

Added AppAuth library dependency.

```diff
 [versions]
+appauth = "0.11.1"
 ...
 
 [libraries]
+appauth = { module = "net.openid:appauth", version.ref = "appauth" }
```

---

### 6. `app/build.gradle.kts`

Added AppAuth dependency, Authentik BuildConfig fields, and redirect scheme.

```diff
     defaultConfig {
         buildConfigField("String", "API_BASE_URL", "\"http://171.226.10.121:8000/\"")
         buildConfigField("String", "MQTT_SERVER_URI", "\"tcp://171.226.10.121:8443\"")
+        buildConfigField("String", "AUTHENTIK_ISSUER", "\"https://auth.ctslab.net/application/o/p-assistant/\"")
+        buildConfigField("String", "AUTHENTIK_CLIENT_ID", "\"p-assistant-client\"")
+        buildConfigField("String", "AUTHENTIK_CLIENT_SECRET", "\"p-assistant-secret-key\"")
+        buildConfigField("String", "AUTHENTIK_REDIRECT_URI", "\"app://passistant/callback\"")
+        buildConfigField("String", "AUTHENTIK_SCOPES", "\"openid email profile roles user_type assigned_products\"")
     }
 
     defaultConfig {
+        manifestPlaceholders["appAuthRedirectScheme"] = "app"
     }
 
 dependencies {
     ...
     implementation(libs.org.eclipse.paho.mqttv5.client)
+    implementation(libs.appauth)
 }
```

---

### 7. `app/src/main/AndroidManifest.xml`

Added `singleTask` launch mode and deep link intent filter for the OIDC callback.

```diff
 <activity
     android:name=".MainActivity"
     android:exported="true"
+    android:launchMode="singleTask"
     android:theme="@style/Theme.AndroidPTalk">
     <intent-filter>
         <action android:name="android.intent.action.MAIN" />
         <category android:name="android.intent.category.LAUNCHER" />
     </intent-filter>
+    <!-- Deep link for Authentik OIDC callback -->
+    <intent-filter>
+        <action android:name="android.intent.action.VIEW" />
+        <category android:name="android.intent.category.DEFAULT" />
+        <category android:name="android.intent.category.BROWSABLE" />
+        <data android:scheme="app" android:host="passistant" android:path="/callback" />
+    </intent-filter>
 </activity>
```

---

### 8. `app/src/main/java/com/avis/app/ptalk/core/network/TokenManager.kt`

Added `OIDCAuthInterceptor` that reads tokens from `OIDCSessionManager` instead of `TokenManager`. Skips auth/OIDC endpoints to avoid leaking tokens.

```diff
+class OIDCAuthInterceptor(
+    private val sessionManager: com.avis.app.ptalk.core.network.authentik.OIDCSessionManager
+) : Interceptor {
+    override fun intercept(chain: Interceptor.Chain): Response {
+        val originalRequest = chain.request()
+
+        if (originalRequest.url.encodedPath.contains("/auth/login") ||
+            originalRequest.url.encodedPath.contains("/auth/signup") ||
+            originalRequest.url.encodedPath.contains("/application/o/")) {
+            return chain.proceed(originalRequest)
+        }
+
+        val token = sessionManager.getAccessToken()
+        if (token != null) {
+            val newRequest = originalRequest.newBuilder()
+                .header("Authorization", "Bearer $token")
+                .build()
+            return chain.proceed(newRequest)
+        }
+
+        return chain.proceed(originalRequest)
+    }
+}
```

---

### 9. `app/src/main/java/com/avis/app/ptalk/di/AppModule.kt`

Added Hilt DI providers for `OIDCSessionManager` and `AuthorizationService`. Updated `OkHttpClient` to use `OIDCAuthInterceptor`.

```diff
+import com.avis.app.ptalk.core.network.OIDCAuthInterceptor
+import com.avis.app.ptalk.core.network.authentik.OIDCSessionManager
+import net.openid.appauth.AuthorizationService
 
 @Provides
 @Singleton
-fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
+fun provideOkHttpClient(tokenManager: TokenManager, oidcSessionManager: OIDCSessionManager): OkHttpClient {
     return OkHttpClient.Builder()
-        .addInterceptor(AuthInterceptor(tokenManager))
+        .addInterceptor(OIDCAuthInterceptor(oidcSessionManager))
         .connectTimeout(30, TimeUnit.SECONDS)
         .readTimeout(30, TimeUnit.SECONDS)
         .build()
 }
 
+// ── Authentik OIDC providers ──────────────────────────────────────
+
+@Provides
+@Singleton
+fun provideOIDCSessionManager(@ApplicationContext ctx: Context): OIDCSessionManager {
+    return OIDCSessionManager(ctx)
+}
+
+@Provides
+@Singleton
+fun provideAuthorizationService(@ApplicationContext ctx: Context): AuthorizationService {
+    return AuthorizationService(ctx)
+}
```

---

### 10. `app/src/main/java/com/avis/app/ptalk/MainActivity.kt`

Injected `OIDCSessionManager` and updated the start destination to check the OIDC session first (with legacy `TokenManager` fallback).

```diff
+import com.avis.app.ptalk.core.network.authentik.OIDCSessionManager
 
 @Inject
 lateinit var tokenManager: TokenManager
+
+@Inject
+lateinit var oidcSessionManager: OIDCSessionManager
 
-val startDest = if (tokenManager.getToken() != null) Route.HOME else Route.LOGIN
+val startDest = if (oidcSessionManager.isAuthorized() || tokenManager.getToken() != null) Route.HOME else Route.LOGIN
```

---

### 11. `app/src/main/java/com/avis/app/ptalk/ui/screen/auth/LoginScreen.kt`

Added the SSO login flow: `ActivityResultLauncher`, pending intent observer, OIDC error display, and **"ĐĂNG NHẬP SSO"** button.

```diff
+import androidx.activity.compose.rememberLauncherForActivityResult
+import androidx.activity.result.contract.ActivityResultContracts
+import com.avis.app.ptalk.ui.viewmodel.auth.VMOIDCLogin
 
 @Composable
 fun LoginScreen(
     onNavigateToHome: () -> Unit,
     onNavigateToSignup: () -> Unit,
-    viewModel: VMLogin = hiltViewModel()
+    viewModel: VMLogin = hiltViewModel(),
+    oidcViewModel: VMOIDCLogin = hiltViewModel()
 ) {
     val uiState by viewModel.uiState.collectAsState()
+    val oidcState by oidcViewModel.uiState.collectAsState()
+    val context = androidx.compose.ui.platform.LocalContext.current
+
+    // OIDC Activity Result Launcher
+    val oidcLauncher = rememberLauncherForActivityResult(
+        contract = ActivityResultContracts.StartActivityForResult()
+    ) { result ->
+        result.data?.let { intent -> oidcViewModel.handleAuthCallback(intent) }
+    }
+
+    // Observe OIDC pending intent — launch when ready
+    LaunchedEffect(oidcViewModel.pendingAuthIntent.collectAsState().value) {
+        oidcViewModel.pendingAuthIntent.value?.let { intent ->
+            oidcLauncher.launch(intent)
+            oidcViewModel.onAuthIntentConsumed()
+        }
+    }
+
+    // Navigate on OIDC success
+    LaunchedEffect(oidcState.success) {
+        if (oidcState.success) { onNavigateToHome() }
+    }
 
     // ... existing form fields ...
 
+    // OIDC error display
+    if (!oidcState.error.isNullOrEmpty()) {
+        Text(text = oidcState.error!!, color = PTalkTokens.Colors.LoginError, ...)
+    }
+
+    // --- OR divider ---
+    HorizontalDivider(...)
+
+    // --- SSO Login Button ---
+    Button(
+        onClick = { oidcViewModel.initiateLogin(context as android.app.Activity) },
+        enabled = !oidcState.isLoading,
+        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5), ...)
+    ) {
+        if (oidcState.isLoading) CircularProgressIndicator(...)
+        else Text("ĐĂNG NHẬP SSO" / "SIGN IN WITH SSO")
+    }
```

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                        LoginScreen                           │
│  ┌─────────────────┐    ┌──────────────────────────────────┐ │
│  │ Legacy Form      │    │ "ĐĂNG NHẬP SSO" Button          │ │
│  │ (VMLogin)        │    │ (VMOIDCLogin)                    │ │
│  └────────┬─────────┘    └────────────┬─────────────────────┘ │
└───────────┼──────────────────────────┼────────────────────────┘
            │                          │
            ▼                          ▼
     ┌──────────────┐          ┌─────────────────┐
     │ AuthRepository│          │ OIDCAuthRepository│
     │ (Legacy REST) │          │ (OIDC/PKCE)      │
     └──────┬───────┘          └────────┬─────────┘
            │                           │
            ▼                           ▼
     ┌──────────────┐          ┌─────────────────┐
     │   AuthApi     │          │ AuthorizationService│
     │ Retrofit/REST │          │   (AppAuth)      │
     └──────┬───────┘          └────────┬─────────┘
            │                           │
            ▼                           ▼
     ┌──────────────┐          ┌─────────────────┐
     │  TokenManager │          │OIDCSessionManager│
     │ (SharedPreferences)      │ (AuthState + SP) │
     └──────┬───────┘          └────────┬─────────┘
            │                           │
            ▼                           ▼
     ┌──────────────┐          ┌─────────────────┐
     │ AuthInterceptor│         │OIDCAuthInterceptor│
     │ (OkHttp)      │         │ (OkHttp)          │
     └───────────────┘         └──────────────────┘
            │                           │
            └───────────┬───────────────┘
                        ▼
              ┌───────────────────┐
              │  auth.ctslab.net   │
              │  (Authentik)       │
              └───────────────────┘
```

---

## Token Flow

```
1. User taps "ĐĂNG NHẬP SSO"
2. VMOIDCLogin.initiateLogin(activity)
   └─ OIDCAuthRepository.createLoginIntent(activity)
      └─ AuthentikConfig.fetchServiceConfig() → OIDC discovery
      └─ AuthentikConfig.buildAuthorizationRequest() → PKCE auth request
      └─ AuthorizationService.getAuthorizationRequestIntent() → Chrome Custom Tab
3. User authenticates in Authentik browser
4. Browser redirects to app://passistant/callback
5. MainActivity receives intent → LoginScreen's ActivityResultLauncher
6. VMOIDCLogin.handleAuthCallback(intent)
   └─ OIDCAuthRepository.handleAuthorizationResponse() → parse auth code
   └─ OIDCAuthRepository.exchangeToken() → exchange code for tokens
   └─ OIDCSessionManager.updateAfterTokenResponse() → persist AuthState
7. Navigate to HOME
```

---

## Migration Notes

- **Legacy login is preserved** — the username/password form still works during the transition period.
- **IoT Platform compatibility** — the IoT Platform at `http://171.226.10.121:8000/` has its own auth endpoints. Verify whether it accepts Authentik JWT tokens or needs separate auth.
- **Token security** — tokens are currently stored in plain `SharedPreferences`. Consider migrating to `EncryptedSharedPreferences` for production.
- **End-session** — `logout()` only clears local state. For full server-side logout, call Authentik's end-session endpoint (`/application/o/p-assistant/end-session/`).
