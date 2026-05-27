package com.avis.app.ptalk.ui.screen.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.R
import com.avis.app.ptalk.ui.theme.PTalkTokens
import com.avis.app.ptalk.ui.viewmodel.auth.VMOIDCLogin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    oidcViewModel: VMOIDCLogin = hiltViewModel()
) {
    val oidcState by oidcViewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // OIDC Activity Result Launcher
    val oidcLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { intent ->
            oidcViewModel.handleAuthCallback(intent)
        }
    }

    // Observe OIDC pending intent — launch when ready
    LaunchedEffect(oidcViewModel.pendingAuthIntent.collectAsState().value) {
        oidcViewModel.pendingAuthIntent.value?.let { intent ->
            oidcLauncher.launch(intent)
            oidcViewModel.onAuthIntentConsumed()
        }
    }

    // Navigate on OIDC success
    LaunchedEffect(oidcState.success) {
        if (oidcState.success) {
            onNavigateToHome()
        }
    }

    var isEnglish by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PTalkTokens.Colors.LoginBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = PTalkTokens.Spacing.XXL)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HERO SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = PTalkTokens.Spacing.HeroTop,
                        start = PTalkTokens.Spacing.XL,
                        end = PTalkTokens.Spacing.XL
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                // Larger logo PTIT as requested
                Image(
                    painter = painterResource(id = R.drawable.logo_ptit),
                    contentDescription = "Logo PTIT",
                    modifier = Modifier.size(130.dp)
                )

                // Language Selector Toggle (Interactive VIE/ENG)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(PTalkTokens.Shapes.LangBadge)
                        .background(PTalkTokens.Colors.InputFieldBg)
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(PTalkTokens.Shapes.LangBadge)
                            .background(if (!isEnglish) PTalkTokens.Colors.Black else Color.Transparent)
                            .clickable { isEnglish = false }
                            .padding(horizontal = PTalkTokens.Spacing.M, vertical = PTalkTokens.Spacing.XS + 2.dp)
                    ) {
                        Text(
                            "VIE",
                            color = if (!isEnglish) PTalkTokens.Colors.White else PTalkTokens.Colors.LoginSubheadline,
                            fontSize = PTalkTokens.FontSizes.BrandTitle,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(PTalkTokens.Shapes.LangBadge)
                            .background(if (isEnglish) PTalkTokens.Colors.Black else Color.Transparent)
                            .clickable { isEnglish = true }
                            .padding(horizontal = PTalkTokens.Spacing.M, vertical = PTalkTokens.Spacing.XS + 2.dp)
                    ) {
                        Text(
                            "ENG",
                            color = if (isEnglish) PTalkTokens.Colors.White else PTalkTokens.Colors.LoginSubheadline,
                            fontSize = PTalkTokens.FontSizes.BrandTitle,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // Brand Header: CHÀO MỪNG BẠN TRỞ LẠI
            Text(
                text = if (isEnglish) "WELCOME\nBACK" else "CHÀO MỪNG\nBẠN TRỞ LẠI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                color = PTalkTokens.Colors.LoginHeadline
            )

            Text(
                text = if (isEnglish) "Sign in to continue setting up your device." else "Đăng nhập để tiếp tục thiết lập thiết bị của bạn.",
                fontSize = PTalkTokens.FontSizes.LoginSubheadline,
                color = PTalkTokens.Colors.LoginSubheadline,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = PTalkTokens.Spacing.M, start = 24.dp, end = 24.dp)
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

            // OIDC error display
            if (!oidcState.error.isNullOrEmpty()) {
                Text(
                    text = oidcState.error!!,
                    color = PTalkTokens.Colors.LoginError,
                    fontSize = PTalkTokens.FontSizes.LoginError,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = PTalkTokens.LoginDimens.FormMarginH)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))
            }

            // SSO Login Button
            Button(
                onClick = {
                    oidcViewModel.initiateLogin(context as android.app.Activity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PTalkTokens.LoginDimens.FormMarginH)
                    .height(PTalkTokens.LoginDimens.InputHeight),
                shape = PTalkTokens.Shapes.LoginButton,
                enabled = !oidcState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5),
                    contentColor = PTalkTokens.Colors.White,
                    disabledContainerColor = PTalkTokens.Colors.SplashDivider
                )
            ) {
                if (oidcState.isLoading) {
                    CircularProgressIndicator(
                        color = PTalkTokens.Colors.White,
                        modifier = Modifier.size(PTalkTokens.Spacing.XL),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isEnglish) "SIGN IN WITH SSO" else "ĐĂNG NHẬP SSO",
                        fontSize = PTalkTokens.FontSizes.LoginButton,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

            // Footer
            Text(
                text = "2025 Lab CTS Học viện Công nghệ Bưu chính Viễn thông",
                color = PTalkTokens.Colors.LoginFooter,
                fontSize = PTalkTokens.FontSizes.LoginFooter,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = PTalkTokens.Spacing.FooterMarginBottom)
            )
        }
    }
}
