package com.ctslab.app.pconnect.ui.screen.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
                modifier = Modifier.size(PTalkTokens.LoginDimens.PtitLogoSize)
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // Headline — displayLarge sets letterSpacing=0.5sp and lineHeight for Vietnamese
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

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.BottomDividerMargin))

            // ── Privacy & Terms consent ──────────────────────────────────
            var agreeTerms by remember { mutableStateOf(false) }

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
                Text(
                    text = stringResource(R.string.consent_prefix),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.consent_privacy),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = PTalkTokens.Colors.LinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/privacy") }
                )
                Text(
                    text = stringResource(R.string.consent_and),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.consent_terms),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = PTalkTokens.Colors.LinkBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/terms") }
                )
            }

            // Error message (visible before consent; hidden after)
            Text(
                text = stringResource(R.string.consent_error),
                style = MaterialTheme.typography.labelMedium,
                color = PTalkTokens.Colors.LoginError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PTalkTokens.Spacing.XS)
                    .then(if (agreeTerms) Modifier.height(0.dp) else Modifier)
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // SSO Button — shape uses token; disabled state via `enabled`
            Button(
                onClick = { onLaunchSSO() },
                enabled = agreeTerms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PTalkTokens.LoginDimens.InputHeight)
                    .semantics { contentDescription = "sso_login_button" },
                shape = PTalkTokens.Shapes.LoginButton,
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
