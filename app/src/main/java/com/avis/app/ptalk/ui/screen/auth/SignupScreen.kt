package com.avis.app.ptalk.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.R
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.theme.AppColors
import com.avis.app.ptalk.ui.viewmodel.auth.VMSignup

@Composable
fun SignupScreen(
    uiState: VMSignup.UiState,
    onRegister: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    val colors = LocalAppColors.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val emailValid by remember(email) {
        derivedStateOf { email.isBlank() || EMAIL_REGEX.matches(email) }
    }
    val passwordsMatch by remember(password, confirmPassword) {
        derivedStateOf { confirmPassword.isEmpty() || password == confirmPassword }
    }
    val canSubmit = username.isNotBlank() && emailValid && email.isNotBlank() &&
        password.length >= 8 && passwordsMatch && !uiState.isLoading && !uiState.success

    val backgroundBrush = Brush.verticalGradient(
        colors = if (colors.isDark) {
            listOf(colors.background, Color(0xFF15181C))
        } else {
            listOf(Color(0xFFFFF6F6), colors.background)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ptit),
                contentDescription = "Logo PTIT",
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Tạo tài khoản PTalk",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Đăng ký để đồng bộ thiết bị và lịch sử",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.card, RoundedCornerShape(28.dp))
                    .border(1.dp, colors.outlineVariant, RoundedCornerShape(28.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FieldUsername(
                    value = username,
                    onValueChange = { username = it; onClearError() },
                    colors = colors
                )

                FieldEmail(
                    value = email,
                    onValueChange = { email = it; onClearError() },
                    isValid = emailValid,
                    colors = colors
                )

                FieldPassword(
                    value = password,
                    onValueChange = { password = it; onClearError() },
                    label = "Mật khẩu",
                    visible = passwordVisible,
                    onToggleVisibility = { passwordVisible = !passwordVisible },
                    imeAction = ImeAction.Next,
                    colors = colors,
                    onDone = null
                )

                if (password.isNotEmpty()) {
                    PasswordStrengthBar(password = password, colors = colors)
                }

                FieldPassword(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; onClearError() },
                    label = "Xác nhận mật khẩu",
                    visible = confirmVisible,
                    onToggleVisibility = { confirmVisible = !confirmVisible },
                    imeAction = ImeAction.Done,
                    colors = colors,
                    onDone = { onRegister(username, email, password, confirmPassword) },
                    error = !passwordsMatch
                )

                if (!passwordsMatch) {
                    Text(
                        text = "Mật khẩu xác nhận không khớp",
                        color = colors.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (uiState.success) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.success,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Đăng ký thành công! Vui lòng đăng nhập.",
                            color = colors.success,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = { onRegister(username, email, password, confirmPassword) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canSubmit,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = colors.surfaceVariant,
                        disabledContentColor = colors.textMuted
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = colors.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Đăng ký",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản? ", color = colors.textSecondary)
                Text(
                    text = "Đăng nhập",
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FieldUsername(
    value: String,
    onValueChange: (String) -> Unit,
    colors: AppColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Tên đăng nhập") },
        leadingIcon = { Icon(Icons.Default.Person, null, tint = colors.textSecondary) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = textFieldColors(colors)
    )
}

@Composable
private fun FieldEmail(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    colors: AppColors
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = colors.textSecondary) },
        singleLine = true,
        isError = !isValid && value.isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        colors = textFieldColors(colors)
    )
}

@Composable
private fun FieldPassword(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    imeAction: ImeAction,
    colors: AppColors,
    onDone: (() -> Unit)?,
    error: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, null, tint = colors.textSecondary) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                    tint = colors.textSecondary
                )
            }
        },
        singleLine = true,
        isError = error,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onDone?.invoke() }),
        colors = textFieldColors(colors)
    )
}

@Composable
private fun textFieldColors(c: AppColors) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = c.primary,
    unfocusedBorderColor = c.outline,
    cursorColor = c.primary,
    focusedTextColor = c.textPrimary,
    unfocusedTextColor = c.textPrimary,
    focusedLabelColor = c.primary,
    unfocusedLabelColor = c.textSecondary,
    focusedLeadingIconColor = c.textSecondary,
    unfocusedLeadingIconColor = c.textSecondary
)

@Composable
private fun PasswordStrengthBar(password: String, colors: AppColors) {
    val strength = passwordStrength(password)
    val (label, variant, color) = when (strength) {
        0 -> Triple("Quá yếu", PChipVariant.Error, colors.error)
        1 -> Triple("Yếu", PChipVariant.Error, colors.error)
        2 -> Triple("Trung bình", PChipVariant.Warning, colors.warning)
        3 -> Triple("Tốt", PChipVariant.Info, colors.accent)
        else -> Triple("Mạnh", PChipVariant.Success, colors.success)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (index < strength) color else colors.outlineVariant,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PChip(text = label, variant = variant)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Tối thiểu 8 ký tự, có chữ hoa, số, ký tự đặc biệt",
                color = colors.textMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private fun passwordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return score.coerceIn(0, 4)
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
