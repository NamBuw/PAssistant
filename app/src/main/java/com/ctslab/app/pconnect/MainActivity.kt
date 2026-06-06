package com.ctslab.app.pconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ctslab.app.pconnect.core.network.TokenManager
import com.ctslab.app.pconnect.core.network.authentik.OIDCSessionManager
import com.ctslab.app.pconnect.navigation.ConfigAppNavGraph
import com.ctslab.app.pconnect.navigation.Route
import com.ctslab.app.pconnect.ui.theme.AppColors
import com.ctslab.app.pconnect.ui.theme.AndroidPTalkTheme
import com.ctslab.app.pconnect.ui.theme.appColors
import dagger.hilt.android.AndroidEntryPoint
import org.thingai.android.module.meo.MeoSdk
import org.thingai.base.log.ILog
import javax.inject.Inject

// Global composition local for theme colors
val LocalAppColors = compositionLocalOf<AppColors> { error("No AppColors provided") }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var oidcSessionManager: OIDCSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ILog.logLevel = ILog.DEBUG
        ILog.ENABLE_LOGGING = true

        MeoSdk.init(this.applicationContext)

        // Simple splash screen
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }

        val startDest = if (oidcSessionManager.isAuthorized()) Route.HOME else Route.LOGIN

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            
            // Always-light brand palette (appColors ignores dark mode)
            val colors = appColors()

            CompositionLocalProvider(LocalAppColors provides colors) {
                // Force light: the token system (appColors) is always light, so the
                // Material scheme must match to avoid a dark/light split on dark devices.
                AndroidPTalkTheme(darkTheme = false) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            },
                        color = colors.background
                    ) {
                        ConfigAppNavGraph(
                            navController = navController,
                            startDestination = Route.SPLASH,
                            nextDestination = startDest,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}