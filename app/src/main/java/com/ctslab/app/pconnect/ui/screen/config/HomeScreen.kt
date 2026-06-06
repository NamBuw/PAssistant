package com.ctslab.app.pconnect.ui.screen.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ctslab.app.pconnect.LocalAppColors
import com.ctslab.app.pconnect.R
import com.ctslab.app.pconnect.ui.theme.AppColors
import com.ctslab.app.pconnect.ui.theme.PTalkTokens
import com.ctslab.app.pconnect.ui.theme.TechColors
import com.ctslab.app.pconnect.ui.viewmodel.VMHome
import com.ctslab.app.pconnect.domain.model.Device
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ctslab.app.pconnect.ui.component.PrimaryActionButton
import com.ctslab.app.pconnect.ui.theme.AndroidPTalkTheme

/**
 * Home Screen - Shows PTIT logo, user devices, and button to connect new device
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToControl: (String, String) -> Unit,
    onNavigateToDeviceDetail: (String, String, String?) -> Unit = { _, _, _ -> },
    onNavigateToBannedWords: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: VMHome = hiltViewModel()
) {
    val colors = LocalAppColors.current
    val uiState by viewModel.uiState.collectAsState()

    var showProfileSheet by remember { mutableStateOf(false) }
    var showDeviceManagement by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val deviceSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Profile Bottom Sheet
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            sheetState = sheetState,
            containerColor = PTalkTokens.Colors.White
        ) {
            ProfileSheetContent(
                username = viewModel.getUsername(),
                email = viewModel.getEmail(),
                phone = viewModel.getPhone(),
                userId = viewModel.getUserId(),
                colors = colors,
                onManageDevices = {
                    scope.launch {
                        sheetState.hide()
                        showProfileSheet = false
                        showDeviceManagement = true
                    }
                },
                onSubscription = {
                    scope.launch {
                        sheetState.hide()
                        showProfileSheet = false
                        onNavigateToSubscription()
                    }
                },
                onSignOut = {
                    scope.launch {
                        sheetState.hide()
                        showProfileSheet = false
                        viewModel.signOut()
                        onSignOut()
                    }
                }
            )
        }
    }

    // Device Management Bottom Sheet
    if (showDeviceManagement) {
        ModalBottomSheet(
            onDismissRequest = { showDeviceManagement = false },
            sheetState = deviceSheetState,
            containerColor = PTalkTokens.Colors.White
        ) {
            DeviceManagementSheetContent(
                devices = uiState.devices,
                colors = colors,
                onDeleteDevice = { device -> viewModel.deleteDevice(device) }
            )
        }
    }

    // Main content — neutral background, brand-consistent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxContentWidth = if (maxWidth >= 600.dp) 680.dp else maxWidth

            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .systemBarsPadding()
                    .padding(horizontal = PTalkTokens.Spacing.XL)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.S))

                // App bar: small PTIT logo + title + actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_ptit),
                        contentDescription = "Logo PTIT",
                        modifier = Modifier.size(32.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(PTalkTokens.Spacing.M))
                    Text(
                        text = "P-Connect",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onNavigateToBannedWords) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Từ ngữ & chủ đề bị cấm",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showProfileSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Tài khoản",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

                // Section heading
                Text(
                    text = "Thiết bị của bạn",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XS))
                Text(
                    text = stringResource(R.string.home_devices_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

                // Content states
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (!uiState.error.isNullOrEmpty()) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (uiState.devices.isEmpty()) {
                    HomeEmptyStateCard()
                } else {
                    uiState.devices.forEach { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = PTalkTokens.Spacing.M)
                                .clickable { onNavigateToControl(device.macAddress, device.name ?: device.macAddress) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(PTalkTokens.Spacing.L),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(PTalkTokens.Spacing.TouchTargetMin)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.width(PTalkTokens.Spacing.L))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = device.name ?: "Thiết bị không tên",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(PTalkTokens.Spacing.XS))
                                    Text(
                                        text = "MAC: ${device.macAddress}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onNavigateToDeviceDetail(
                                            device.macAddress,
                                            device.name ?: device.macAddress,
                                            device.deviceId
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = "Lịch sử chat",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

                // CTA
                PrimaryActionButton(
                    text = "Bắt đầu cấu hình",
                    onClick = onNavigateToScan,
                    leadingIcon = Icons.Default.Bluetooth,
                    semanticsTag = "start_config_button"
                )

                Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))
            }
        }
    }
}

@Composable
private fun HomeEmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PTalkTokens.Spacing.XL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DevicesOther,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))
            Text(
                text = stringResource(R.string.home_empty_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.S))
            Text(
                text = stringResource(R.string.home_empty_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))
            EmptyStateStep(index = "1", text = stringResource(R.string.home_empty_step1))
            EmptyStateStep(index = "2", text = stringResource(R.string.home_empty_step2))
            EmptyStateStep(index = "3", text = stringResource(R.string.home_empty_step3))
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.home_empty_cue),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(PTalkTokens.Spacing.XS))
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateStep(index: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PTalkTokens.Spacing.XS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(PTalkTokens.Spacing.M))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileSheetContent(
    username: String?,
    email: String?,
    phone: String?,
    userId: String?,
    colors: AppColors,
    onManageDevices: () -> Unit,
    onSubscription: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTalkTokens.Spacing.XL)
            .padding(bottom = PTalkTokens.Spacing.XXL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PTalkTokens.Colors.PTITRed,
                            PTalkTokens.Colors.PTITRed.copy(alpha = 0.7f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (username?.firstOrNull()?.uppercaseChar() ?: 'U').toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = PTalkTokens.Colors.White
            )
        }

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))

        Text(
            text = username ?: "Người dùng",
            fontSize = PTalkTokens.FontSizes.SubGreeting,
            fontWeight = FontWeight.Bold,
            color = PTalkTokens.Colors.ProfileHeaderText
        )

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

        HorizontalDivider(color = PTalkTokens.Colors.SplashDivider)

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

        // User info rows
        if (!email.isNullOrBlank()) {
            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = email,
                colors = colors
            )
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))
        }

        if (!phone.isNullOrBlank()) {
            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Số điện thoại",
                value = phone,
                colors = colors
            )
            Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))
        }

        if (!userId.isNullOrBlank()) {
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "User ID",
                value = userId,
                colors = colors
            )
        }

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.XL))

        HorizontalDivider(color = PTalkTokens.Colors.SplashDivider)

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

        // Subscription button
        OutlinedButton(
            onClick = onSubscription,
            modifier = Modifier
                .fillMaxWidth()
                .height(PTalkTokens.Spacing.TouchTargetMin),
            shape = PTalkTokens.Shapes.InputField,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PTalkTokens.Colors.PTITRedDark
            )
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(PTalkTokens.Spacing.S))
            Text(
                text = "Gói đăng ký",
                fontSize = PTalkTokens.FontSizes.LoginButton,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))

        // Manage devices button
        Button(
            onClick = onManageDevices,
            modifier = Modifier
                .fillMaxWidth()
                .height(PTalkTokens.Spacing.TouchTargetMin),
            shape = PTalkTokens.Shapes.InputField,
            colors = ButtonDefaults.buttonColors(
                containerColor = PTalkTokens.Colors.PTITRedDark,
                contentColor = PTalkTokens.Colors.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(PTalkTokens.Spacing.S))
            Text(
                text = "Quản lý thiết bị",
                fontSize = PTalkTokens.FontSizes.LoginButton,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.M))

        // Sign out button
        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(PTalkTokens.Spacing.TouchTargetMin),
            shape = PTalkTokens.Shapes.InputField,
            colors = ButtonDefaults.buttonColors(
                containerColor = PTalkTokens.Colors.CancelButtonBg,
                contentColor = PTalkTokens.Colors.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(PTalkTokens.Spacing.S))
            Text(
                text = "Đăng xuất",
                fontSize = PTalkTokens.FontSizes.LoginButton,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DeviceManagementSheetContent(
    devices: List<Device>,
    colors: AppColors,
    onDeleteDevice: (Device) -> Unit
) {
    var deviceToDelete by remember { mutableStateOf<Device?>(null) }

    // Confirm delete dialog
    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Xóa thiết bị") },
            text = {
                Text("Bạn có chắc muốn xóa thiết bị \"${device.name ?: device.macAddress}\"?\n\nThiết bị sẽ được chuyển về chế độ cấu hình BLE.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDevice(device)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = PTalkTokens.Colors.CancelButtonBg)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PTalkTokens.Spacing.XL)
            .padding(bottom = PTalkTokens.Spacing.XXL)
    ) {
        Text(
            text = "Quản lý thiết bị",
            fontSize = PTalkTokens.FontSizes.SubGreeting,
            fontWeight = FontWeight.Bold,
            color = PTalkTokens.Colors.ProfileHeaderText
        )

        Spacer(modifier = Modifier.height(PTalkTokens.Spacing.L))

        if (devices.isEmpty()) {
            Text(
                "Chưa có thiết bị nào.",
                color = PTalkTokens.Colors.LoginSubheadline,
                fontSize = PTalkTokens.FontSizes.Status
            )
        } else {
            devices.forEach { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = PTalkTokens.Spacing.S),
                    shape = PTalkTokens.Shapes.InputField,
                    colors = CardDefaults.cardColors(
                        containerColor = PTalkTokens.Colors.ProfileBg
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTalkTokens.Spacing.M),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PhoneAndroid,
                            null,
                            tint = PTalkTokens.Colors.PTITRed,
                            modifier = Modifier.size(PTalkTokens.Spacing.XL)
                        )
                        Spacer(Modifier.width(PTalkTokens.Spacing.M))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                device.name ?: "Thiết bị",
                                fontSize = PTalkTokens.FontSizes.Status,
                                fontWeight = FontWeight.SemiBold,
                                color = PTalkTokens.Colors.ProfileHeaderText
                            )
                            Text(
                                device.macAddress,
                                fontSize = PTalkTokens.FontSizes.BrandSubtitle,
                                color = PTalkTokens.Colors.LoginSubheadline
                            )
                        }
                        IconButton(
                            onClick = { deviceToDelete = device }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "Xóa",
                                tint = PTalkTokens.Colors.CancelButtonBg
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    colors: AppColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PTalkTokens.Colors.PTITRed.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PTalkTokens.Colors.PTITRed,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(PTalkTokens.Spacing.M))
        Column {
            Text(
                text = label,
                fontSize = PTalkTokens.FontSizes.BrandSubtitle,
                color = PTalkTokens.Colors.LoginSubheadline
            )
            Text(
                text = value,
                fontSize = PTalkTokens.FontSizes.Status,
                fontWeight = FontWeight.Medium,
                color = PTalkTokens.Colors.ProfileHeaderText
            )
        }
    }
}

@Preview(name = "Home empty — phone", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun HomeEmptyPreview() {
    AndroidPTalkTheme(darkTheme = false) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(PTalkTokens.Spacing.XL)
        ) {
            HomeEmptyStateCard()
        }
    }
}
