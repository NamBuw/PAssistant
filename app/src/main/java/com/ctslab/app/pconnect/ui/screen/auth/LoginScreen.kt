package com.ctslab.app.pconnect.ui.screen.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ctslab.app.pconnect.R
import com.ctslab.app.pconnect.ui.component.PrimaryActionButton
import com.ctslab.app.pconnect.ui.theme.AndroidPTalkTheme

/**
 * P-Connect login — Material 3, PTIT-red, brand-forward, tablet-aware.
 * Vertically centered card; consent gates the SSO button; no error shown on load.
 * Auth logic is untouched — onLaunchSSO is the only entry point. Self-signup has been
 * removed: [onNavigateToSignup] is kept for the nav contract but no longer surfaced.
 */
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit, // part of the nav contract (passed by ConfigNavGraph); SSO success navigates from the graph
    onNavigateToSignup: () -> Unit = {}, // retained for source compat; signup is no longer reachable from the UI
    onLaunchSSO: () -> Unit = {}
) {
    val context = LocalContext.current
    val openUrl = { url: String ->
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
    var agreeTerms by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Soft brand wash in the top corner (very light red), purely decorative.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomStart = 220.dp)
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            val isTablet = maxWidth >= 600.dp
            val cardWidth = if (isTablet) 480.dp else maxWidth

            Column(
                modifier = Modifier
                    .widthIn(max = cardWidth)
                    .fillMaxWidth()
                    .padding(horizontal = if (isTablet) 0.dp else 24.dp)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isTablet) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        LoginContent(
                            agreeTerms = agreeTerms,
                            onAgreeChange = { agreeTerms = it },
                            onLaunchSSO = onLaunchSSO,
                            openUrl = openUrl,
                            contentPadding = 32.dp
                        )
                    }
                } else {
                    LoginContent(
                        agreeTerms = agreeTerms,
                        onAgreeChange = { agreeTerms = it },
                        onLaunchSSO = onLaunchSSO,
                        openUrl = openUrl,
                        contentPadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Footer pinned to the bottom
        Text(
            text = stringResource(R.string.login_footer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun LoginContent(
    agreeTerms: Boolean,
    onAgreeChange: (Boolean) -> Unit,
    onLaunchSSO: () -> Unit,
    openUrl: (String) -> Unit,
    contentPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Branding
        Image(
            painter = painterResource(id = R.drawable.logo_p_connect),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(88.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_subheadline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.login_tagline),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Consent — inline clickable links (red)
        val cPrefix = stringResource(R.string.consent_prefix)
        val cPrivacy = stringResource(R.string.consent_privacy)
        val cAnd = stringResource(R.string.consent_and)
        val cTerms = stringResource(R.string.consent_terms)
        val primaryColor = MaterialTheme.colorScheme.primary
        val consentText = remember(cPrefix, cPrivacy, cAnd, cTerms, primaryColor) {
            val linkStyle = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textDecoration = TextDecoration.Underline
            )
            buildAnnotatedString {
                append(cPrefix.trim()); append(" ")
                pushStringAnnotation("url", "https://dashboard.ctslab.net/privacy")
                withStyle(linkStyle) { append(cPrivacy.trim()) }
                pop()
                append(" "); append(cAnd.trim()); append(" ")
                pushStringAnnotation("url", "https://dashboard.ctslab.net/terms")
                withStyle(linkStyle) { append(cTerms.trim()) }
                pop()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = onAgreeChange,
                    modifier = Modifier.semantics { contentDescription = "agree_terms_checkbox" }
                )
            }
            ClickableText(
                text = consentText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                modifier = Modifier.weight(1f)
            ) { offset ->
                consentText.getStringAnnotations("url", offset, offset)
                    .firstOrNull()?.let { openUrl(it.item) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SSO button (primary) — gated by consent
        PrimaryActionButton(
            text = stringResource(R.string.login_btn_sso),
            onClick = onLaunchSSO,
            enabled = agreeTerms,
            leadingIcon = Icons.AutoMirrored.Filled.Login,
            semanticsTag = "sso_login_button"
        )

        // Neutral microcopy only while disabled (NOT an error on load)
        if (!agreeTerms) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.login_sso_disabled_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Login — phone", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LoginScreenPhonePreview() {
    AndroidPTalkTheme(darkTheme = false) {
        LoginScreen(onNavigateToHome = {}, onNavigateToSignup = {}, onLaunchSSO = {})
    }
}

@Preview(name = "Login — tablet", showBackground = true, widthDp = 840, heightDp = 1100)
@Composable
private fun LoginScreenTabletPreview() {
    AndroidPTalkTheme(darkTheme = false) {
        LoginScreen(onNavigateToHome = {}, onNavigateToSignup = {}, onLaunchSSO = {})
    }
}
