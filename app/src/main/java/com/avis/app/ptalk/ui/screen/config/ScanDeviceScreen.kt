package com.avis.app.ptalk.ui.screen.config

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.ui.component.dialog.ErrorDialog
import com.avis.app.ptalk.ui.component.dialog.LoadingDialog
import com.avis.app.ptalk.ui.component.dialog.SuccessDialog
import com.avis.app.ptalk.ui.screen.config.scan.DeviceRadar
import com.avis.app.ptalk.ui.screen.config.scan.ScanListView
import com.avis.app.ptalk.ui.screen.config.scan.ScanWarningCard
import com.avis.app.ptalk.ui.screen.config.scan.checkSystemServices
import com.avis.app.ptalk.ui.viewmodel.VMConfigDevice

/**
 * BLE provisioning entry — orchestrates permission/system gating,
 * scan toggle, radar/list view, and the configuration dialog flow.
 *
 * Sub-components live in `ui/screen/config/scan/`:
 *  - DeviceRadar      — animated canvas radar
 *  - ScanListView     — list alternative sorted by RSSI
 *  - ScanWarningCard  — permission/BT/GPS-off banners
 *  - SystemServices   — runtime BT + Location read helper
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDeviceScreen(
    onDeviceConnected: (String) -> Unit,
    onBack: () -> Unit = {},
    vm: VMConfigDevice = hiltViewModel()
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by vm.ui.collectAsState()

    var permissionsGranted by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var isBluetoothEnabled by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(false) }

    // RADAR ↔ LIST view toggle (keep behaviour, add option)
    var showAsList by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val (bt, loc) = checkSystemServices(context)
                isBluetoothEnabled = bt
                isLocationEnabled = loc
                if (uiState.scanning && (!bt || !loc)) vm.stopScan()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        val (bt, loc) = checkSystemServices(context)
        isBluetoothEnabled = bt
        isLocationEnabled = loc
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsGranted = result.values.all { it }
    }

    fun requestBlePermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) { requestBlePermissions() }

    LoadingDialog(
        show = isConnecting,
        message = "Đang kết nối thiết bị...",
        onDismiss = { isConnecting = false }
    )

    if (showConfigDialog) {
        DeviceConfigDialog(
            deviceId = uiState.deviceId,
            wifiNetworks = uiState.wifiNetworks,
            loadingWifiList = uiState.loadingWifiList,
            onRefreshWifi = { vm.refreshWifiList() },
            onDismiss = {
                vm.disconnectDevice()
                showConfigDialog = false
            },
            onSubmit = { ssid, pass, volume, brightness ->
                isConnecting = true
                showConfigDialog = false
                vm.configDevice(
                    ssid, pass, volume, brightness,
                    onSuccess = { isConnecting = false; showSuccess = true },
                    onError = { error ->
                        isConnecting = false
                        errorMessage = error
                        showError = true
                    }
                )
            }
        )
    }

    SuccessDialog(
        show = showSuccess,
        title = "Thành công",
        message = "Cấu hình thiết bị hoàn tất!\nThiết bị sẽ khởi động lại và kết nối WiFi.",
        confirmText = "Hoàn thành",
        onDismiss = {
            vm.disconnectDevice()
            showSuccess = false
        }
    )

    ErrorDialog(
        show = showError,
        title = "Lỗi kết nối",
        message = errorMessage.ifEmpty { "Không thể kết nối với thiết bị" },
        onDismiss = {
            vm.disconnectDevice()
            showError = false
        }
    )

    ScanDeviceContent(
        uiState = uiState,
        permissionsGranted = permissionsGranted,
        isBluetoothEnabled = isBluetoothEnabled,
        isLocationEnabled = isLocationEnabled,
        showAsList = showAsList,
        onToggleView = { showAsList = !showAsList },
        onBack = onBack,
        onRequestPermission = { requestBlePermissions() },
        onOpenBluetoothSettings = {
            context.startActivity(
                Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        },
        onOpenLocationSettings = {
            context.startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        },
        onScanToggle = {
            val (bt, loc) = checkSystemServices(context)
            isBluetoothEnabled = bt
            isLocationEnabled = loc
            if (!bt || !loc) return@ScanDeviceContent
            if (uiState.scanning) vm.stopScan() else vm.startScan()
        },
        onDeviceClick = { device ->
            isConnecting = true
            vm.stopScan()
            vm.connectDevice(device.address) {
                isConnecting = false
                showConfigDialog = true
            }
        }
    )
}

/**
 * Stateless body for ScanDeviceScreen — accepts pure state + callbacks
 * so it can be rendered by Compose Preview / debug Gallery without
 * needing real BLE permissions, lifecycle observers, or system services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScanDeviceContent(
    uiState: VMConfigDevice.UiState,
    permissionsGranted: Boolean,
    isBluetoothEnabled: Boolean,
    isLocationEnabled: Boolean,
    showAsList: Boolean,
    onToggleView: () -> Unit,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    onScanToggle: () -> Unit,
    onDeviceClick: (com.avis.app.ptalk.core.ble.ScannedDevice) -> Unit
) {
    val colors = LocalAppColors.current

    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Quét thiết bị",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleView) {
                        Icon(
                            imageVector = if (showAsList) Icons.Default.Radar
                                          else Icons.AutoMirrored.Filled.List,
                            contentDescription = if (showAsList) "Chế độ Radar" else "Chế độ danh sách",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !permissionsGranted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    ScanWarningCard(
                        icon = Icons.Default.Lock,
                        title = "Cần cấp quyền Bluetooth",
                        message = "Nhấn để cấp quyền truy cập Bluetooth và Vị trí.",
                        actionLabel = "Cấp quyền",
                        colors = colors,
                        onAction = onRequestPermission
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(
                visible = permissionsGranted && !isBluetoothEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    ScanWarningCard(
                        icon = Icons.Default.Bluetooth,
                        title = "Bluetooth đang tắt",
                        message = "Bật Bluetooth để có thể quét thiết bị PTalk.",
                        actionLabel = "Mở cài đặt",
                        colors = colors,
                        onAction = onOpenBluetoothSettings
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(
                visible = permissionsGranted && isBluetoothEnabled && !isLocationEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    ScanWarningCard(
                        icon = Icons.Default.LocationOn,
                        title = "Vị trí đang tắt",
                        message = "Android yêu cầu bật Vị trí để quét BLE.",
                        actionLabel = "Mở cài đặt",
                        colors = colors,
                        onAction = onOpenLocationSettings
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (showAsList) {
                    if (uiState.devices.isEmpty()) {
                        EmptyHint(colors = colors, scanning = uiState.scanning)
                    } else {
                        ScanListView(
                            devices = uiState.devices,
                            colors = colors,
                            onDeviceClick = onDeviceClick
                        )
                    }
                } else {
                    DeviceRadar(
                        devices = uiState.devices,
                        isScanning = uiState.scanning,
                        colors = colors,
                        onDeviceClick = onDeviceClick
                    )
                }
            }

            Text(
                text = if (uiState.scanning) "Đang quét... (${uiState.devices.size} thiết bị)"
                       else "Tìm thấy ${uiState.devices.size} thiết bị",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            ScanButton(
                isScanning = uiState.scanning,
                enabled = permissionsGranted && isBluetoothEnabled && isLocationEnabled,
                onClick = onScanToggle
            )

            uiState.error?.let { err ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = err,
                    color = colors.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScanButton(
    isScanning: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isScanning) colors.error else colors.primary,
            contentColor = colors.onPrimary,
            disabledContainerColor = colors.surfaceVariant,
            disabledContentColor = colors.textMuted
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.BluetoothSearching,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isScanning) "Dừng quét" else "Quét thiết bị",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun EmptyHint(colors: com.avis.app.ptalk.ui.theme.AppColors, scanning: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Bluetooth,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (scanning) "Đang tìm thiết bị..." else "Chưa tìm thấy thiết bị",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary
        )
        Text(
            text = if (scanning) "Đảm bảo thiết bị PTalk đang ở chế độ cấu hình BLE."
                   else "Nhấn \"Quét thiết bị\" để bắt đầu.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )
    }
}
