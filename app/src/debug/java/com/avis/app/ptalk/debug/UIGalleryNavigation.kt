package com.avis.app.ptalk.debug

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors

/**
 * Lightweight gallery navigator: a list of scenarios, tap to open the
 * real screen body fed with mock state. Back button returns to the list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UIGalleryNavigation(
    onClose: () -> Unit,
    isDark: Boolean,
    onToggleDark: () -> Unit
) {
    val colors = LocalAppColors.current
    var selected by remember { mutableStateOf<GalleryItem?>(null) }

    if (selected != null) {
        Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
            // Compact header so the underlying screen still gets most of
            // the viewport. Useful for quick A/B against design specs.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .systemBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selected = null }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại Gallery",
                        tint = colors.textPrimary
                    )
                }
                Text(
                    text = selected!!.label,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleDark) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Đổi theme",
                        tint = colors.textPrimary
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                renderScenario(selected!!.scenario)
            }
        }
        return
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "UI Gallery",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Debug only · ${if (isDark) "Dark" else "Light"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDark) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Đổi theme",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(GalleryItems) { item ->
                GalleryRow(item = item, onClick = { selected = item })
            }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun GalleryRow(item: GalleryItem, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colors.card, RoundedCornerShape(16.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colors.primary.copy(alpha = if (colors.isDark) 0.20f else 0.10f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = colors.primary)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

internal data class GalleryItem(
    val scenario: GalleryScenario,
    val label: String,
    val description: String,
    val icon: ImageVector
)

private val GalleryItems = listOf(
    // Auth
    GalleryItem(GalleryScenario.Splash, "Splash", "Animation logo + footer", Icons.Default.SmartButton),
    GalleryItem(GalleryScenario.Login, "Login", "Đăng nhập SSO", Icons.Default.Login),
    GalleryItem(GalleryScenario.SignupEmpty, "Signup · trống", "Form mới mở", Icons.Default.PersonAdd),
    GalleryItem(GalleryScenario.SignupError, "Signup · lỗi", "Email đã tồn tại", Icons.Default.PersonAdd),
    GalleryItem(GalleryScenario.SignupSuccess, "Signup · thành công", "Sau khi đăng ký", Icons.Default.PersonAdd),

    // Home
    GalleryItem(GalleryScenario.HomeDevices, "Home · 3 thiết bị", "Danh sách bình thường", Icons.Default.Speaker),
    GalleryItem(GalleryScenario.HomeEmpty, "Home · trống", "Chưa có thiết bị nào", Icons.Default.Speaker),
    GalleryItem(GalleryScenario.HomeLoading, "Home · loading", "Đang tải Room", Icons.Default.Speaker),
    GalleryItem(GalleryScenario.HomeError, "Home · lỗi", "Hiển thị error", Icons.Default.Speaker),

    // Control
    GalleryItem(GalleryScenario.ControlOnline, "Điều khiển · online", "Pin 78%", Icons.Default.Tune),
    GalleryItem(GalleryScenario.ControlLowBattery, "Điều khiển · pin yếu", "Battery donut đỏ", Icons.Default.Tune),
    GalleryItem(GalleryScenario.ControlOffline, "Điều khiển · offline", "Slider disabled", Icons.Default.Tune),
    GalleryItem(GalleryScenario.ControlWaiting, "Điều khiển · waiting", "MQTT đợi response", Icons.Default.Tune),

    // Chat history
    GalleryItem(GalleryScenario.DetailSessionsPTalk, "Chat · sessions PTalk", "Có 5 phiên", Icons.AutoMirrored.Filled.Chat),
    GalleryItem(GalleryScenario.DetailSessionsKidMentor, "Chat · sessions KidMentor", "Tab thứ hai", Icons.AutoMirrored.Filled.Chat),
    GalleryItem(GalleryScenario.DetailSessionsEmpty, "Chat · trống", "Chưa có phiên nào", Icons.AutoMirrored.Filled.Chat),
    GalleryItem(GalleryScenario.DetailMessages, "Chat · messages", "12 tin nhắn", Icons.AutoMirrored.Filled.Chat),
    GalleryItem(GalleryScenario.DetailNoDeviceId, "Chat · no deviceId", "Empty state", Icons.AutoMirrored.Filled.Chat),

    // Scan
    GalleryItem(GalleryScenario.ScanPermission, "Scan · cần cấp quyền", "Warning card", Icons.Default.Radar),
    GalleryItem(GalleryScenario.ScanBtOff, "Scan · BT tắt", "Warning Bluetooth", Icons.Default.Radar),
    GalleryItem(GalleryScenario.ScanLocationOff, "Scan · GPS tắt", "Warning Location", Icons.Default.Radar),
    GalleryItem(GalleryScenario.ScanRadarEmpty, "Scan · radar trống", "Đang sweep", Icons.Default.Radar),
    GalleryItem(GalleryScenario.ScanRadarFull, "Scan · radar có 5 thiết bị", "Markers full", Icons.Default.Radar),
    GalleryItem(GalleryScenario.ScanList, "Scan · list view", "Sorted by RSSI", Icons.Default.Radar),

    // Foundation components
    GalleryItem(GalleryScenario.Foundations, "Foundation · components", "Chip, dot, skeleton, avatar...", Icons.Default.SmartButton)
)
