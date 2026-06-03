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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ctslab.app.pconnect.LocalAppColors
import com.ctslab.app.pconnect.R
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // PTIT University Logo
            Image(
                painter = painterResource(id = R.drawable.logo_ptit),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ĐĂNG NHẬP",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TechColors.PTITRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sử dụng tài khoản Authentik SSO",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Privacy & Terms Agreement
            var agreeTerms by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I agree to ",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TechColors.PTITRed,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/privacy") }
                )
                Text(
                    text = " and ",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "Terms",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TechColors.PTITRed,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { openUrl("https://dashboard.ctslab.net/terms") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message for unchecked terms
            if (!agreeTerms) {
                Text(
                    text = "You must agree to Privacy Policy and Terms & Conditions",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SSO Button (Authentik) - Primary login method
            Button(
                onClick = { if (agreeTerms) onLaunchSSO() },
                enabled = agreeTerms,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechColors.PTITRed,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = "SSO",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng nhập với SSO",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản? ", color = colors.textSecondary)
                Text(
                    text = "Đăng ký ngay",
                    color = TechColors.PTITRed,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToSignup() }
                )
            }
        }
    }
}
