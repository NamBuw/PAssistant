package com.avis.app.ptalk.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.ui.theme.AndroidPTalkTheme
import com.avis.app.ptalk.ui.theme.appColors

/**
 * Debug-only host that lets developers browse every UI scenario
 * (and Foundation components) on a real device/emulator without
 * needing real BLE/MQTT/auth state.
 *
 * Lives in the `debug/` source set, so it is **never** compiled into
 * release builds. Reachable via long-pressing the logo on Splash
 * (see SplashDebugHook.kt) or by adb:
 *   adb shell am start -n com.avis.app.ptalk/.debug.UIGalleryActivity
 */
class UIGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkOverride by remember { mutableStateOf<Boolean?>(null) }
            val systemDark = isSystemInDarkTheme()
            val isDark = darkOverride ?: systemDark
            val colors = appColors(isDark)

            AndroidPTalkTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalAppColors provides colors) {
                    Surface(
                        modifier = Modifier,
                        color = colors.background
                    ) {
                        UIGalleryNavigation(
                            onClose = { finish() },
                            isDark = isDark,
                            onToggleDark = { darkOverride = !isDark }
                        )
                    }
                }
            }
        }
    }
}
