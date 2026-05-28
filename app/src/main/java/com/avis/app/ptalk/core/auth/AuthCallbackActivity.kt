package com.avis.app.ptalk.core.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avis.app.ptalk.MainActivity
import com.avis.app.ptalk.core.network.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Handles the OAuth2 redirect from Authentik.
 *
 * When Authentik redirects to app://passistant/callback?code=xxx&state=yyy,
 * Android routes the intent to this activity. We pass the intent data
 * to AuthentikAuthManager to exchange the code for tokens.
 *
 * This activity is transparent - user never sees it.
 */
@AndroidEntryPoint
class AuthCallbackActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authManager = AuthentikAuthManager(this)

        authManager.handleAuthorizationResponse(
            data = intent,
            onSuccess = { result ->
                // Save tokens using existing TokenManager
                tokenManager.saveToken(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    userId = result.userId
                )
                tokenManager.saveUserInfo(username = result.name, email = result.email, phone = null)

                // Navigate to main screen
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(mainIntent)
                finish()
            },
            onError = { error ->
                // Go back to login with error
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("auth_error", error)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(mainIntent)
                finish()
            }
        )
    }
}
