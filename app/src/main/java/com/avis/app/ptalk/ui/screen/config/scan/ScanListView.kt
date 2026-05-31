package com.avis.app.ptalk.ui.screen.config.scan

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.NetworkWifi2Bar
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material.icons.filled.SignalWifi0Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.theme.AppColors

/**
 * List-style alternative to the radar view. Sorted by RSSI strength.
 */
@Composable
internal fun ScanListView(
    devices: List<ScannedDevice>,
    colors: AppColors,
    onDeviceClick: (ScannedDevice) -> Unit
) {
    val sorted = devices.sortedByDescending { it.rssi }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(sorted, key = { it.address }) { device ->
            ScanListItem(device = device, colors = colors, onClick = { onDeviceClick(device) })
        }
    }
}

@Composable
private fun ScanListItem(
    device: ScannedDevice,
    colors: AppColors,
    onClick: () -> Unit
) {
    val (signalIcon, signalLabel, signalVariant) = signalInfo(device.rssi)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colors.card, RoundedCornerShape(18.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(colors.primary.copy(alpha = if (colors.isDark) 0.22f else 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.BluetoothConnected,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name?.takeIf { it.isNotBlank() } ?: "PTalk Device",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    signalIcon,
                    contentDescription = null,
                    tint = signalColor(colors, signalVariant),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${device.rssi} dBm",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = signalColor(colors, signalVariant)
                )
            }
            Spacer(Modifier.size(4.dp))
            PChip(text = signalLabel, variant = signalVariant)
        }
    }
}

private fun signalInfo(rssi: Int): Triple<androidx.compose.ui.graphics.vector.ImageVector, String, PChipVariant> {
    return when {
        rssi >= -50 -> Triple(Icons.Default.NetworkWifi, "Rất mạnh", PChipVariant.Success)
        rssi >= -65 -> Triple(Icons.Default.NetworkWifi3Bar, "Mạnh", PChipVariant.Success)
        rssi >= -75 -> Triple(Icons.Default.NetworkWifi2Bar, "Trung bình", PChipVariant.Warning)
        rssi >= -85 -> Triple(Icons.Default.NetworkWifi1Bar, "Yếu", PChipVariant.Warning)
        else -> Triple(Icons.Default.SignalWifi0Bar, "Rất yếu", PChipVariant.Error)
    }
}

private fun signalColor(colors: AppColors, variant: PChipVariant) = when (variant) {
    PChipVariant.Success -> colors.success
    PChipVariant.Warning -> colors.warning
    PChipVariant.Error -> colors.error
    else -> colors.textSecondary
}
