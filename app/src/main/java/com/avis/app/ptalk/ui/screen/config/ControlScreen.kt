package com.avis.app.ptalk.ui.screen.config

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.component.foundation.PStatusDot
import com.avis.app.ptalk.core.websocket.DeviceStatusResponse
import com.avis.app.ptalk.ui.theme.AppColors
import com.avis.app.ptalk.ui.viewmodel.VMControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    macAddress: String,
    deviceName: String,
    onBack: () -> Unit,
    viewModel: VMControl = hiltViewModel()
) {
    val status by viewModel.deviceStatus.collectAsState()
    val isConnected by viewModel.connectionState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lastError by viewModel.lastError.collectAsState()
    val localDeviceName by viewModel.localDeviceName.collectAsState()

    LaunchedEffect(macAddress) {
        viewModel.initConnection(macAddress)
    }

    ControlContent(
        deviceName = deviceName,
        localDeviceName = localDeviceName,
        status = status,
        isConnected = isConnected,
        isLoading = isLoading,
        lastError = lastError,
        onBack = onBack,
        onClearError = viewModel::clearError,
        onSetVolume = viewModel::setVolume,
        onSetBrightness = viewModel::setBrightness,
        onSetDeviceName = viewModel::setDeviceName,
        onResetWifi = viewModel::resetWifi,
        onReboot = viewModel::rebootDevice
    )
}

/**
 * Stateless body for ControlScreen. Pure inputs/outputs so it can be
 * driven from `@Preview` or the debug Gallery without a real
 * MQTT/Hilt graph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ControlContent(
    deviceName: String,
    localDeviceName: String?,
    status: DeviceStatusResponse?,
    isConnected: Boolean,
    isLoading: Boolean,
    lastError: String?,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    onSetVolume: (Int) -> Unit,
    onSetBrightness: (Int) -> Unit,
    onSetDeviceName: (String) -> Unit,
    onResetWifi: () -> Unit,
    onReboot: () -> Unit
) {
    val colors = LocalAppColors.current
    val isOnline = status?.connectivityState == "ONLINE"

    var volume by remember { mutableFloatStateOf(50f) }
    var brightness by remember { mutableFloatStateOf(50f) }
    var showRenameSheet by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(status) {
        status?.let {
            volume = (it.volume ?: 30).toFloat()
            brightness = (it.brightness ?: 50).toFloat()
        }
    }

    LaunchedEffect(lastError) {
        lastError?.let {
            snackbar.showSnackbar(it)
            onClearError()
        }
    }

    if (showRenameSheet) {
        AlertDialog(
            onDismissRequest = { showRenameSheet = false },
            containerColor = colors.surface,
            title = {
                Text(
                    "Đổi tên thiết bị",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Tên thiết bị") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        cursorColor = colors.primary,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onSetDeviceName(newName.trim())
                            showRenameSheet = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.primary)
                ) {
                    Text("Lưu", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameSheet = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        containerColor = colors.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbar,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = colors.surfaceVariant,
                        contentColor = colors.textPrimary,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = localDeviceName ?: status?.deviceName ?: deviceName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    IconButton(onClick = {
                        newName = localDeviceName ?: status?.deviceName ?: deviceName
                        showRenameSheet = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Đổi tên", tint = colors.textPrimary)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Hero status card ─────────────────────────────
            HeroStatusCard(
                isOnline = isOnline,
                isConnectedToMqtt = isConnected,
                battery = status?.batteryLevel,
                wifiSsid = status?.wifiSsid,
                wifiRssi = status?.wifiRssi,
                firmware = status?.firmwareVersion,
                uptimeSec = status?.uptimeSec,
                colors = colors
            )

            // ── Volume slider card ───────────────────────────
            ControlSliderCard(
                title = "Âm lượng",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                accent = colors.primary,
                value = volume,
                enabled = isConnected && isOnline,
                onValueChange = { volume = it },
                onValueChangeFinished = { onSetVolume(volume.toInt()) },
                colors = colors
            )

            // ── Brightness slider card ───────────────────────
            ControlSliderCard(
                title = "Độ sáng màn hình",
                icon = Icons.Default.BrightnessMedium,
                accent = colors.warning,
                value = brightness,
                enabled = isConnected && isOnline,
                onValueChange = { brightness = it },
                onValueChangeFinished = { onSetBrightness(brightness.toInt()) },
                colors = colors
            )

            // ── Advanced section ─────────────────────────────
            AdvancedSection(
                enabled = isConnected && isOnline && !isLoading,
                colors = colors,
                onResetWifi = {
                    onResetWifi()
                    onBack()
                },
                onReboot = {
                    onReboot()
                    onBack()
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroStatusCard(
    isOnline: Boolean,
    isConnectedToMqtt: Boolean,
    battery: Int?,
    wifiSsid: String?,
    wifiRssi: Int?,
    firmware: String?,
    uptimeSec: Int?,
    colors: AppColors
) {
    val statusLabel: String
    val statusVariant: PChipVariant
    when {
        isOnline -> { statusLabel = "Đang trực tuyến"; statusVariant = PChipVariant.Success }
        isConnectedToMqtt -> { statusLabel = "Đang chờ thiết bị"; statusVariant = PChipVariant.Warning }
        else -> { statusLabel = "Đang kết nối MQTT"; statusVariant = PChipVariant.Neutral }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BatteryDonut(percent = battery, colors = colors)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PStatusDot(online = isOnline, size = 10.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (battery != null) "Pin: $battery%" else "Đang đồng bộ trạng thái…",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                PChip(text = statusLabel, variant = statusVariant)
            }

            Spacer(Modifier.height(16.dp))

            // Info chips row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!wifiSsid.isNullOrBlank()) {
                    InfoChip(
                        icon = Icons.Default.Wifi,
                        label = wifiSsid + (wifiRssi?.let { " · ${it}dBm" } ?: ""),
                        colors = colors
                    )
                }
                if (uptimeSec != null) {
                    InfoChip(
                        icon = Icons.Default.Schedule,
                        label = formatUptime(uptimeSec),
                        colors = colors
                    )
                }
                if (!firmware.isNullOrBlank()) {
                    InfoChip(
                        icon = Icons.Default.Memory,
                        label = "FW $firmware",
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun BatteryDonut(percent: Int?, colors: AppColors) {
    val pct = (percent ?: 0).coerceIn(0, 100) / 100f
    val arcColor = when {
        percent == null -> colors.textMuted
        (percent) < 20 -> colors.error
        (percent) < 50 -> colors.warning
        else -> colors.success
    }
    Box(
        modifier = Modifier.size(72.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val stroke = Stroke(width = 8f)
            val pad = stroke.width / 2f
            val arcSize = Size(this.size.width - stroke.width, this.size.height - stroke.width)
            drawArc(
                color = colors.outlineVariant,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * pct,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.BatteryFull,
                contentDescription = null,
                tint = arcColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = percent?.let { "$it%" } ?: "—",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, colors: AppColors) {
    Row(
        modifier = Modifier
            .background(colors.surfaceVariant, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ControlSliderCard(
    title: String,
    icon: ImageVector,
    accent: androidx.compose.ui.graphics.Color,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    colors: AppColors
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accent.copy(alpha = if (colors.isDark) 0.22f else 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${value.toInt()}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) accent else colors.textMuted
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = 2.dp, top = 6.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = 0f..100f,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = accent,
                    activeTrackColor = accent,
                    inactiveTrackColor = colors.outlineVariant,
                    disabledThumbColor = colors.textMuted,
                    disabledActiveTrackColor = colors.outlineVariant
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(0, 25, 50, 75, 100).forEach { tick ->
                    Text(
                        text = "$tick",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvancedSection(
    enabled: Boolean,
    colors: AppColors,
    onResetWifi: () -> Unit,
    onReboot: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = "Nâng cao",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
            modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 4.dp)
        )

        AdvancedRow(
            icon = Icons.Default.Bluetooth,
            iconTint = colors.warning,
            title = "Chế độ cấu hình BLE",
            description = "Đưa thiết bị về chế độ Bluetooth để cấu hình lại",
            enabled = enabled,
            colors = colors,
            onClick = onResetWifi
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 60.dp, end = 20.dp)
                .background(colors.outlineVariant)
        )

        AdvancedRow(
            icon = Icons.Default.PowerSettingsNew,
            iconTint = colors.error,
            title = "Khởi động lại thiết bị",
            description = "Reset thiết bị qua MQTT",
            enabled = enabled,
            colors = colors,
            onClick = onReboot
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun AdvancedRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    enabled: Boolean,
    colors: AppColors,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconTint.copy(alpha = if (colors.isDark) 0.22f else 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = if (enabled) colors.textPrimary else colors.textMuted
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (enabled) colors.textSecondary else colors.textMuted
        )
    }
}

private fun formatUptime(seconds: Int?): String {
    if (seconds == null) return "?"
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return when {
        h > 0 -> "${h}h ${m}m"
        m > 0 -> "${m}m ${s}s"
        else -> "${s}s"
    }
}
