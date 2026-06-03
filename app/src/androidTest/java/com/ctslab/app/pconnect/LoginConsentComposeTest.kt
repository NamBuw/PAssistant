package com.ctslab.app.pconnect

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI test for consent gating in P-Connect LoginScreen.
 *
 * LoginScreen uses Modifier.semantics { contentDescription = "..." } on:
 *   - Checkbox → "agree_terms_checkbox"
 *   - SSO button → "sso_login_button"
 *
 * Run with: ./gradlew :app:connectedDebugAndroidTest
 *           -Pandroid.testInstrumentationRunnerArguments.class=com.ctslab.app.pconnect.LoginConsentComposeTest
 *
 * NOTE: This test navigates to the Login screen by launching MainActivity with a cleared
 * token state so the app routes to LOGIN. If the CI environment has a token, the test
 * may need to clear SharedPreferences before launch (handled via clearPackageData in
 * testInstrumentationRunner or a custom TestRunner).
 */
@RunWith(AndroidJUnit4::class)
class LoginConsentComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val checkboxDesc = "agree_terms_checkbox"
    private val ssoBtnDesc = "sso_login_button"

    /** T1-2: SSO button disabled on fresh launch (no consent). */
    @Test
    fun ssoBtnDisabledWithoutConsent() {
        composeRule.onNodeWithContentDescription(ssoBtnDesc)
            .assertIsNotEnabled()
    }

    /** T1-4: SSO button enabled after ticking checkbox. */
    @Test
    fun ssoBtnEnabledAfterConsent() {
        composeRule.onNodeWithContentDescription(checkboxDesc)
            .performClick()
        composeRule.onNodeWithContentDescription(ssoBtnDesc)
            .assertIsEnabled()
    }

    /** T1-5: SSO button back to disabled after unchecking. */
    @Test
    fun ssoBtnDisabledAfterUncheck() {
        composeRule.onNodeWithContentDescription(checkboxDesc)
            .performClick() // check
        composeRule.onNodeWithContentDescription(checkboxDesc)
            .performClick() // uncheck
        composeRule.onNodeWithContentDescription(ssoBtnDesc)
            .assertIsNotEnabled()
    }
}
