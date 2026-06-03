package com.avis.app.ptalk.core.network.authentik

import android.content.Context
import android.net.Uri
import com.avis.app.ptalk.BuildConfig
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.util.concurrent.atomic.AtomicReference

/**
 * Centralized OIDC configuration for Authentik.
 *
 * On first use, fetches the OIDC discovery document from the issuer's
 * .well-known/openid-configuration endpoint and caches the service config.
 */
object AuthentikConfig {

    val ISSUER: String = BuildConfig.AUTHENTIK_ISSUER
    val CLIENT_ID: String = BuildConfig.AUTHENTIK_CLIENT_ID
    val CLIENT_SECRET: String = BuildConfig.AUTHENTIK_CLIENT_SECRET
    val REDIRECT_URI: Uri = Uri.parse(BuildConfig.AUTHENTIK_REDIRECT_URI)
    val SCOPES: List<String> = BuildConfig.AUTHENTIK_SCOPES.split(" ")

    private val cachedConfig = AtomicReference<AuthorizationServiceConfiguration?>()

    /**
     * Fetch the OIDC service configuration from the issuer's well-known endpoint.
     * Caches the result for subsequent calls.
     */
    suspend fun fetchServiceConfig(
        context: Context
    ): AuthorizationServiceConfiguration {
        cachedConfig.get()?.let { return it }

        android.util.Log.d("AuthentikConfig", "Fetching OIDC config from issuer: $ISSUER")

        val config = kotlinx.coroutines.suspendCancellableCoroutine<AuthorizationServiceConfiguration> { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(ISSUER)) { serviceConfig, ex ->
                if (ex != null || serviceConfig == null) {
                    android.util.Log.e("AuthentikConfig", "Failed to fetch OIDC config from $ISSUER", ex)
                    val detailedError = when {
                        ex?.message?.contains("Unable to resolve host") == true -> "Không thể kết nối đến máy chủ Authentik (DNS). Kiểm tra kết nối mạng."
                        ex?.message?.contains("timeout") == true -> "Hết thời gian kết nối đến Authentik."
                        ex?.message?.contains("SSLHandshake") == true -> "Lỗi chứng chỉ SSL khi kết nối đến Authentik."
                        ex != null -> "Lỗi mạng: ${ex.message}"
                        else -> "Không thể tải cấu hình OIDC từ $ISSUER"
                    }
                    cont.resumeWith(Result.failure(IllegalStateException(detailedError, ex)))
                } else {
                    android.util.Log.d("AuthentikConfig", "OIDC config fetched successfully: ${serviceConfig.authorizationEndpoint}")
                    cachedConfig.set(serviceConfig)
                    cont.resumeWith(Result.success(serviceConfig))
                }
            }
        }
        return config
    }

    /**
     * Build an authorization request for the given service configuration.
     * Uses PKCE (S256) for security.
     */
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
