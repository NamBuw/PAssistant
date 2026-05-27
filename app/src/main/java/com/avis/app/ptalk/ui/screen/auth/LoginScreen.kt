package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.R
import com.avis.app.ptalk.ui.theme.PTalkTokens
import com.avis.app.ptalk.ui.viewmodel.auth.VMLogin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: VMLogin = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEnglish by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onNavigateToHome()
        }
    }

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

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            // FORM CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PTalkTokens.LoginDimens.FormMarginH)
            ) {
                // Username
                Text(
                    text = if (isEnglish) "ACCOUNT" else "TÀI KHOẢN",
                    fontSize = PTalkTokens.FontSizes.LoginLabel,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = PTalkTokens.Colors.LoginLabel,
                    modifier = Modifier.padding(bottom = PTalkTokens.Spacing.S)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it; viewModel.clearError() },
                    placeholder = {
                        Text(
                            if (isEnglish) "Enter username" else "Nhập tên đăng nhập",
                            color = PTalkTokens.Colors.LoginInputHint,
                            fontSize = PTalkTokens.FontSizes.Input
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PTalkTokens.LoginDimens.InputHeight),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = PTalkTokens.Colors.InputFieldBg,
                        unfocusedContainerColor = PTalkTokens.Colors.InputFieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = PTalkTokens.Colors.LoginInputText,
                        unfocusedTextColor = PTalkTokens.Colors.LoginInputText
                    ),
                    shape = PTalkTokens.Shapes.InputField
                )

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

                // Password
                Text(
                    text = if (isEnglish) "PASSWORD" else "MẬT KHẨU",
                    fontSize = PTalkTokens.FontSizes.LoginLabel,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = PTalkTokens.Colors.LoginLabel,
                    modifier = Modifier.padding(bottom = PTalkTokens.Spacing.S)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it; viewModel.clearError() },
                    placeholder = {
                        Text(
                            if (isEnglish) "Enter password" else "Nhập mật khẩu",
                            color = PTalkTokens.Colors.LoginInputHint,
                            fontSize = PTalkTokens.FontSizes.Input
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PTalkTokens.LoginDimens.InputHeight),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = PTalkTokens.Colors.InputFieldBg,
                        unfocusedContainerColor = PTalkTokens.Colors.InputFieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = PTalkTokens.Colors.LoginInputText,
                        unfocusedTextColor = PTalkTokens.Colors.LoginInputText
                    ),
                    shape = PTalkTokens.Shapes.InputField
                )

                if (!uiState.error.isNullOrEmpty()) {
                    Text(
                        text = uiState.error!!,
                        color = PTalkTokens.Colors.LoginError,
                        fontSize = PTalkTokens.FontSizes.LoginError,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = PTalkTokens.Spacing.M)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

                // Login button — Black pill (PTalk: 30dp radius, #111111)
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PTalkTokens.LoginDimens.InputHeight),
                    shape = PTalkTokens.Shapes.LoginButton,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTalkTokens.Colors.LoginHeadline,    // #111111 normal
                        contentColor = PTalkTokens.Colors.LoginBtnText,       // #FFFFFF
                        disabledContainerColor = PTalkTokens.Colors.SplashDivider  // #E5E5E5 disabled
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = PTalkTokens.Colors.White,
                            modifier = Modifier.size(PTalkTokens.Spacing.XL),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEnglish) "SIGN IN" else "ĐĂNG NHẬP",
                            fontSize = PTalkTokens.FontSizes.LoginButton,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

                // Vibrant Blue Registration Link Text (Chưa có tài khoản? Đăng kí ngay)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSignup() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEnglish) "Don't have an account? Register now" else "Chưa có tài khoản? Đăng kí ngay",
                        color = Color(0xFF1E88E5), // Vibrant blue
                        fontSize = PTalkTokens.FontSizes.GuestButton,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

            // Footer (2025 Lab CTS Học viện Công nghệ Bưu chính Viễn thông)
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
