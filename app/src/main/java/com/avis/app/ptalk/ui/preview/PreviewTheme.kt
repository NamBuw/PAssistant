package com.avis.app.ptalk.ui.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.ui.theme.AndroidPTalkTheme
import com.avis.app.ptalk.ui.theme.appColors

/**
 * Wrapper used by every `@Preview`-annotated function. Sets up the
 * full PAssistant theme (Light or Dark) plus the `LocalAppColors`
 * composition local that the screens read from.
 */
@Composable
internal fun PreviewTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    val colors = appColors(dark)
    AndroidPTalkTheme(darkTheme = dark) {
        CompositionLocalProvider(LocalAppColors provides colors) {
            Surface(color = colors.background, content = content)
        }
    }
}
