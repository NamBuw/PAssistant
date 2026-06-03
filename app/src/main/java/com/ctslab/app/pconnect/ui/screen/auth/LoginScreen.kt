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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var agreeTerms by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // ── Co-branding header (PTIT | P-Connect | CTS) ──────────────
            CoBrandingHeader()

            Spacer(modifier = Modifier.height(44.dp))

            // Headline (28sp) — prominent but balanced
            Text(
                text = stringResource(R.string.login_headline),
                style = MaterialTheme.typography.displayLarge,
                color = TechColors.PTITRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.login_subheadline),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Consent ───────────────────────────────────────────────────
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
                    text = stringResource(R.string.consent_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, top = 2.dp),
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

            if (!agreeTerms) {
                Text(
                    text = stringResource(R.string.consent_error),
                    style = MaterialTheme.typography.labelMedium,
                    color = PTalkTokens.Colors.LoginError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PTalkTokens.Spacing.S)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── SSO button (primary) ─────────────────────────────────────
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

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Footer pinned to the bottom — fills the empty space, adds institutional identity
        Text(
            text = stringResource(R.string.login_footer),
            style = MaterialTheme.typography.labelSmall,
            color = PTalkTokens.Colors.LoginFooter,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        )
    }
}

/** PTIT | P-Connect | CTS lockup — consistent with the home-screen pill and the XML apps. */
@Composable
private fun CoBrandingHeader() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PTalkTokens.Colors.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ptit),
                contentDescription = "PTIT",
                modifier = Modifier.size(44.dp),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(1.dp)
                    .height(32.dp)
                    .background(PTalkTokens.Colors.LoginDividerLine)
            )
            Text(
                text = "P-CONNECT",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = PTalkTokens.Colors.HomePillText,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(1.dp)
                    .height(32.dp)
                    .background(PTalkTokens.Colors.LoginDividerLine)
            )
            Image(
                painter = painterResource(id = R.drawable.logo_cts_flashscreen),
                contentDescription = "CTS",
                modifier = Modifier
                    .width(56.dp)
                    .height(40.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
