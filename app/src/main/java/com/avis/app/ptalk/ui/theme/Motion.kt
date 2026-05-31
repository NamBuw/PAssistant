package com.avis.app.ptalk.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * PAssistant motion tokens — duration & easing curves following
 * Material 3 Expressive guidelines.
 *
 * Use these instead of ad-hoc tween() values to keep motion consistent
 * across the whole app.
 */
object PMotion {

    // ── Duration tokens (ms) ────────────────────────────────────────────
    object Duration {
        const val Short1 = 50
        const val Short2 = 100
        const val Short3 = 150
        const val Short4 = 200
        const val Medium1 = 250
        const val Medium2 = 300
        const val Medium3 = 350
        const val Medium4 = 400
        const val Long1 = 450
        const val Long2 = 500
        const val Long3 = 550
        const val Long4 = 600
        const val ExtraLong1 = 700
        const val ExtraLong2 = 800
    }

    // ── Easing tokens ───────────────────────────────────────────────────
    val EasingStandard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EasingStandardDecelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
    val EasingStandardAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)
    val EasingEmphasized = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EasingEmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EasingEmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    // ── Pre-built specs ─────────────────────────────────────────────────
    fun <T> tweenStandard() = tween<T>(durationMillis = Duration.Medium2, easing = EasingStandard)
    fun <T> tweenEmphasized() = tween<T>(durationMillis = Duration.Long2, easing = EasingEmphasized)
    fun <T> tweenFast() = tween<T>(durationMillis = Duration.Short3, easing = EasingStandard)

    fun <T> springExpressive() = spring<T>(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> springGentle() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}
