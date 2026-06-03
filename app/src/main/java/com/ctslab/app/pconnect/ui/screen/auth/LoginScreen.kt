package com.ctslab.app.pconnect.ui.screen.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ctslab.app.pconnect.LocalAppColors
import com.ctslab.app.pconnect.R
import com.ctslab.app.pconnect.ui.theme.PTalkTokens
import com.ctslab.app.pconnect.ui.theme.TechColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onLaunchSSO: () -> Unit = {}
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val openUrl = { url: String ->
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PTalkTokens.LoginDimens.FormMarginH)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.HeroTop))

            // PTIT logo
            Image(
                painter = painterResource(id = R.drawable.logo_ptit),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // Headline: 34sp, letterSpacing 0.5sp (not 2sp), lineHeight from typography scale
            Text(
                text = stringResource(R.string.login_headline),
                style = MaterialTheme.typography.displayLarge,
                color = TechColors.PTITRed
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            Text(
                text = stringResource(R.string.login_subheadline),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // ── Privacy & Terms consent ──────────────────────────────────
            var agreeTerms by remember { mutableStateOf(false) }

            // Single-line checkbox row: keep the flow on one line and let text wrap naturally
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    modifier = Modifier
                        .size(24.dp)
                        .semantics { contentDescription = "agree_terms_checkbox" }
                )
                Spacer(modifier = Modifier.width(PTalkTokens.Spacing.S))
                // Use a single Text with AnnotatedString so the whole sentence wraps
                // gracefully; tap targets on the links are handled by separate clickable Texts below.
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.consent_prefix))
                        withStyle(SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = PTalkTokens.Colors.LinkBlue,
                            textDecoration = TextDecoration.Underline
                        )) { append(stringResource(R.string.consent_privacy)) }
                        append(stringResource(R.string.consent_and))
                        withStyle(SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = PTalkTokens.Colors.LinkBlue,
                            textDecoration = TextDecoration.Underline
                        )) { append(stringResource(R.string.consent_terms)) }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { openUrl("https://dashboard.ctslab.net/privacy") }
                )
            }

            // Privacy / Terms as separate tappable rows below the checkbox (better tap targets)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.consent_privacy),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PTalkTokens.Colors.LinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/privacy") }
                )
                Text(
                    text = stringResource(R.string.consent_terms),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PTalkTokens.Colors.LinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/terms") }
                )
            }

            // Error message — conditional rendering (not height(0) hack)
            if (!agreeTerms) {
                Text(
                    text = stringResource(R.string.consent_error),
                    style = MaterialTheme.typography.labelMedium,
                    color = PTalkTokens.Colors.LoginError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PTalkTokens.Spacing.XS)
                )
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // SSO button — keep original 16dp radius (not full pill), original 56dp height
            Button(
                onClick = { onLaunchSSO() },
                enabled = agreeTerms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics { contentDescription = "sso_login_button" },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechColors.PTITRed,
                    disabledContainerColor = PTalkTokens.Colors.LoginDividerLine
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(PTalkTokens.Spacing.S))
                Text(
                    text = stringResource(R.string.login_btn_sso),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.signup_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.signup_link),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TechColors.PTITRed,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.FooterBottom))
        }
    }
}
