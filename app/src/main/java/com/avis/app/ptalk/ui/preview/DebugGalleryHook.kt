package com.avis.app.ptalk.ui.preview

import android.content.Context
import android.content.Intent
import com.avis.app.ptalk.BuildConfig

/**
 * Open the debug-only UI Gallery if it is on the classpath. The
 * Gallery activity lives in `app/src/debug/`, so this lookup is
 * intentionally reflective — release builds simply no-op.
 *
 * Triggered from [SplashScreen] via long-pressing the PTIT logo.
 */
fun openDebugGalleryIfAvailable(context: Context) {
    if (!BuildConfig.DEBUG) return
    runCatching {
        val cls = Class.forName("com.avis.app.ptalk.debug.UIGalleryActivity")
        context.startActivity(Intent(context, cls))
    }
}
