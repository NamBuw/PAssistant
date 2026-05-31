package com.avis.app.ptalk.ui.screen.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.R
import com.avis.app.ptalk.domain.model.Device
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.component.foundation.PDeviceAvatar
import com.avis.app.ptalk.ui.component.foundation.PEmptyState
import com.avis.app.ptalk.ui.component.foundation.PSectionHeader
import com.avis.app.ptalk.ui.theme.AppColors
import kotlinx.coroutines.launch

/**
 * Home — main entry after login.
 *
 * Layout:
 *   ┌──────────────────────────────────────────┐
 *   │ Avatar greeting              [profile]   │
 *   │ Brand pill (PTIT — PASSISTANT — CTS)     │
 *   │                                          │
 *   │ Section: Thiết bị của bạn (n)            │
 *   │ ┌── DeviceCard ─────────────────────┐    │
 *   │ │ avatar  Tên thiết bị              │    │
 *   │ │         MAC                        │   │
 *   │ │ [Điều khiển] [Lịch sử chat]        │   │
 *   │ └────────────────────────────────────┘   │
 *   │ ...                                      │
 *   │                                          │
 *   │                       [+ Cấu hình] (FAB) │
 *   └──────────────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToControl: (String, String) -> Unit,
    onNavigateToDeviceDetail: (String, String, String?) -> Unit = { _, _, _ -> },
    onSignOut: () -> Unit = {},
    viewModel: com.avis.app.ptalk.ui.viewmodel.VMHome = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeContent(
        uiState = uiState,
        username = viewModel.getUsername(),
        email = viewModel.getEmail(),
        phone = viewModel.getPhone(),
        userId = viewModel.getUserId(),
        onNavigateToScan = onNavigateToScan,
        onNavigateToControl = onNavigateToControl,
        onNavigateToDeviceDetail = onNavigateToDeviceDetail,
        onSignOut = {
            viewModel.signOut()
            onSignOut()
        },
        onDeleteDevice = viewModel::deleteDevice
    )
}

/**
 * Stateless body for HomeScreen — accepts plain values & callbacks
 * so it can be rendered by Compose Preview and the debug Gallery.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    uiState: com.avis.app.ptalk.ui.viewmodel.VMHome.UiState,
    username: String?,
    email: String?,
    phone: String?,
    userId: String?,
    onNavigateToScan: () -> Unit,
    onNavigateToControl: (String, String) -> Unit,
    onNavigateToDeviceDetail: (String, String, String?) -> Unit,
    onSignOut: () -> Unit,
    onDeleteDevice: (Device) -> Unit
) {
    val colors = LocalAppColors.current

    var showProfileSheet by remember { mutableStateOf(false) }
    var showDeviceManagement by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val deviceSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            sheetState = sheetState,
            containerColor = colors.surface
        ) {
            ProfileSheetContent(
                username = username,
                email = email,
                phone = phone,
                userId = userId,
                colors = colors,
                onManageDevices = {
                    scope.launch {
                        sheetState.hide()
                        showProfileSheet = false
                        showDeviceManagement = true
                    }
                },
                onSignOut = {
                    scope.launch {
                        sheetState.hide()
                        showProfileSheet = false
                        onSignOut()
                    }
                }
            )
        }
    }

    if (showDeviceManagement) {
        ModalBottomSheet(
            onDismissRequest = { showDeviceManagement = false },
            sheetState = deviceSheetState,
            containerColor = colors.surface
        ) {
            DeviceManagementSheetContent(
                devices = uiState.devices,
                colors = colors,
                onDeleteDevice = onDeleteDevice
            )
        }
    }

    Scaffold(
        containerColor = colors.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToScan,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = {
                    Text(
                        "Cấu hình thiết bị",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HomeHeader(
                    username = username,
                    onProfileClick = { showProfileSheet = true },
                    colors = colors
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item { BrandPill(colors = colors) }
            item { Spacer(Modifier.height(8.dp)) }
            item {
                PSectionHeader(
                    title = "Thiết bị của bạn",
                    trailing = {
                        if (uiState.devices.isNotEmpty()) {
                            PChip(text = "${uiState.devices.size}", variant = PChipVariant.Brand)
                        }
                    }
                )
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = colors.primary) }
                    }
                }
                !uiState.error.isNullOrEmpty() -> {
                    item {
                        Text(
                            uiState.error!!,
                            color = colors.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                uiState.devices.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.card, RoundedCornerShape(20.dp))
                                .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
                        ) {
                            PEmptyState(
                                icon = Icons.Default.Bluetooth,
                                title = "Chưa có thiết bị nào",
                                description = "Nhấn nút bên dưới để tìm và cấu hình thiết bị PTalk đầu tiên.",
                                ctaText = "Bắt đầu cấu hình",
                                onCtaClick = onNavigateToScan
                            )
                        }
                    }
                }
                else -> {
                    items(uiState.devices, key = { it.macAddress }) { device ->
                        DeviceCard(
                            device = device,
                            colors = colors,
                            onControl = {
                                onNavigateToControl(device.macAddress, device.name ?: device.macAddress)
                            },
                            onChat = {
                                onNavigateToDeviceDetail(
                                    device.macAddress,
                                    device.name ?: device.macAddress,
                                    device.deviceId
                                )
                            }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) } // space for FAB
        }
    }
}

@Composable
private fun HomeHeader(
    username: String?,
    onProfileClick: () -> Unit,
    colors: AppColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Xin chào,",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            Text(
                text = username?.takeIf { it.isNotBlank() } ?: "Người dùng",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            PDeviceAvatar(name = username, size = 44.dp)
        }
    }
}

@Composable
private fun BrandPill(colors: AppColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(colors.card, RoundedCornerShape(32.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(32.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_ptit),
            contentDescription = "Logo PTIT",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "PASSISTANT",
            color = colors.accent,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp
            )
        )
        Image(
            painter = painterResource(id = R.drawable.logo_cts_flashscreen),
            contentDescription = "Logo CTS",
            modifier = Modifier
                .width(56.dp)
                .height(40.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun DeviceCard(
    device: Device,
    colors: AppColors,
    onControl: () -> Unit,
    onChat: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onControl)
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PDeviceAvatar(name = device.name ?: "P", size = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name ?: "Thiết bị không tên",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = device.macAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Mở điều khiển",
                    tint = colors.textMuted
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionChip(
                    icon = Icons.Default.Tune,
                    label = "Điều khiển",
                    onClick = onControl,
                    colors = colors,
                    primary = true
                )
                QuickActionChip(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "Lịch sử chat",
                    onClick = onChat,
                    colors = colors,
                    primary = false
                )
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    colors: AppColors,
    primary: Boolean
) {
    val bg = if (primary) colors.primary.copy(alpha = if (colors.isDark) 0.22f else 0.10f)
             else colors.surfaceVariant
    val fg = if (primary) colors.primary else colors.textPrimary

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
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
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PDeviceAvatar(name = username, size = 80.dp)

        Spacer(Modifier.height(12.dp))

        Text(
            text = username ?: "Người dùng",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        if (!email.isNullOrEmpty()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = colors.outlineVariant)

        Spacer(Modifier.height(12.dp))

        if (!email.isNullOrBlank()) {
            ProfileInfoRow(Icons.Default.Email, "Email", email, colors)
            Spacer(Modifier.height(8.dp))
        }
        if (!phone.isNullOrBlank()) {
            ProfileInfoRow(Icons.Default.Phone, "Số điện thoại", phone, colors)
            Spacer(Modifier.height(8.dp))
        }
        if (!userId.isNullOrBlank()) {
            ProfileInfoRow(Icons.Default.DeveloperMode, "User ID", userId, colors)
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(color = colors.outlineVariant)

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onManageDevices,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            )
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Quản lý thiết bị",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Đăng xuất",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
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
                .background(colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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

    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Xóa thiết bị") },
            text = {
                Text("Bạn có chắc muốn xóa \"${device.name ?: device.macAddress}\"?\n\nThiết bị sẽ được chuyển về chế độ cấu hình BLE.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDevice(device)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
                ) { Text("Xóa") }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) { Text("Hủy") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp)
    ) {
        Text(
            "Quản lý thiết bị",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )

        Spacer(Modifier.height(16.dp))

        if (devices.isEmpty()) {
            PEmptyState(
                icon = Icons.Default.PhoneAndroid,
                title = "Chưa có thiết bị",
                description = "Bạn cần cấu hình ít nhất một thiết bị để quản lý.",
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            devices.forEach { device ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.cardHighlight, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PDeviceAvatar(name = device.name ?: "P", size = 40.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            device.name ?: "Thiết bị",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        Text(
                            device.macAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    IconButton(onClick = { deviceToDelete = device }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = colors.error)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
