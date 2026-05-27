package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.R
import com.avis.app.ptalk.ui.theme.PTalkTokens
import com.avis.app.ptalk.ui.viewmodel.auth.VMSignup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: VMSignup = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current
    var authUsername by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passConfirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
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
                .padding(horizontal = PTalkTokens.LoginDimens.FormMarginH)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interactive Language Toggle Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PTalkTokens.Spacing.HeroTop),
                contentAlignment = Alignment.TopEnd
            ) {
                Row(
                    modifier = Modifier
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

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))

            Text(
                text = if (isEnglish) "CREATE ACCOUNT" else "TẠO TÀI KHOẢN",
                fontSize = PTalkTokens.FontSizes.LoginHeadline,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = PTalkTokens.Colors.LoginHeadline
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

            // ── Input fields ──

            SignupInputField(
                value = authUsername,
                onValueChange = { authUsername = it; viewModel.clearError() },
                label = if (isEnglish) "USERNAME" else "TÊN ĐĂNG NHẬP",
                placeholder = if (isEnglish) "Username *" else "Tên đăng nhập *"
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            SignupInputField(
                value = username,
                onValueChange = { username = it; viewModel.clearError() },
                label = if (isEnglish) "FULL NAME" else "TÊN NGƯỜI DÙNG",
                placeholder = if (isEnglish) "Full name *" else "Tên người dùng *"
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            SignupInputField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = if (isEnglish) "EMAIL" else "EMAIL",
                placeholder = if (isEnglish) "Email *" else "Email *",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            SignupInputField(
                value = phone,
                onValueChange = { phone = it; viewModel.clearError() },
                label = if (isEnglish) "PHONE NUMBER" else "SỐ ĐIỆN THOẠI",
                placeholder = if (isEnglish) "Phone number" else "Số điện thoại",
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            // Password field
            Column(modifier = Modifier.fillMaxWidth()) {
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
                            if (isEnglish) "Password *" else "Mật khẩu *",
                            color = PTalkTokens.Colors.LoginInputHint,
                            fontSize = PTalkTokens.FontSizes.Input
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PTalkTokens.LoginDimens.InputHeight),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility",
                                tint = PTalkTokens.Colors.LoginLabel
                            )
                        }
                    },
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
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

            // Confirm password field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isEnglish) "CONFIRM PASSWORD" else "XÁC NHẬN MẬT KHẨU",
                    fontSize = PTalkTokens.FontSizes.LoginLabel,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = PTalkTokens.Colors.LoginLabel,
                    modifier = Modifier.padding(bottom = PTalkTokens.Spacing.S)
                )
                TextField(
                    value = passConfirm,
                    onValueChange = { passConfirm = it; viewModel.clearError() },
                    placeholder = {
                        Text(
                            if (isEnglish) "Confirm password *" else "Xác nhận mật khẩu *",
                            color = PTalkTokens.Colors.LoginInputHint,
                            fontSize = PTalkTokens.FontSizes.Input
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(PTalkTokens.LoginDimens.InputHeight),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            }

            if (!uiState.error.isNullOrEmpty()) {
                Text(
                    text = uiState.error!!,
                    color = PTalkTokens.Colors.LoginError,
                    fontSize = PTalkTokens.FontSizes.LoginError,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = PTalkTokens.Spacing.S)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))

            // Register button
            Button(
                onClick = {
                    viewModel.signup(
                        authUsername = authUsername,
                        email = email,
                        pass = password,
                        passConfirm = passConfirm,
                        username = username,
                        phone = phone
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PTalkTokens.LoginDimens.InputHeight),
                shape = PTalkTokens.Shapes.LoginButton,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTalkTokens.Colors.LoginHeadline,
                    contentColor = PTalkTokens.Colors.LoginBtnText,
                    disabledContainerColor = PTalkTokens.Colors.SplashDivider
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
                        text = if (isEnglish) "REGISTER" else "ĐĂNG KÝ",
                        fontSize = PTalkTokens.FontSizes.LoginButton,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (isEnglish) "Already have an account? " else "Đã có tài khoản? ",
                    color = PTalkTokens.Colors.LoginSubheadline,
                    fontSize = PTalkTokens.FontSizes.GuestButton
                )
                Text(
                    text = if (isEnglish) "Sign in" else "Đăng nhập",
                    color = PTalkTokens.Colors.LoginHeadline,
                    fontWeight = FontWeight.Bold,
                    fontSize = PTalkTokens.FontSizes.GuestButton,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XXL))
        }
    }
}

/**
 * Reusable input field matching PTalk's filled input style:
 * #F5F5F5 background, 12dp corner radius, 52dp height, no underline indicator
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignupInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = PTalkTokens.FontSizes.LoginLabel,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = PTalkTokens.Colors.LoginLabel,
            modifier = Modifier.padding(bottom = PTalkTokens.Spacing.S)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = PTalkTokens.Colors.LoginInputHint,
                    fontSize = PTalkTokens.FontSizes.Input
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(PTalkTokens.LoginDimens.InputHeight),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
    }
}
