package com.avis.app.ptalk.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.component.foundation.PDeviceAvatar
import com.avis.app.ptalk.ui.component.foundation.PEmptyState
import com.avis.app.ptalk.ui.component.foundation.PRelativeTime
import com.avis.app.ptalk.ui.component.foundation.PSectionHeader
import com.avis.app.ptalk.ui.component.foundation.PSkeletonCard
import com.avis.app.ptalk.ui.component.foundation.PStatusDot
import java.time.Instant

/**
 * Showcase grid for the foundation components from PV1. Useful when
 * tweaking shared visuals (chips, dots, avatars, skeletons, empty
 * states) so changes can be eyeballed in isolation.
 */
@Composable
internal fun FoundationsGalleryContent() {
    val colors = LocalAppColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { PSectionHeader(title = "Status dot") }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PStatusDot(online = true, size = 12.dp)
                    Spacer(Modifier.height(4.dp))
                    Text("online", color = colors.textSecondary, style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PStatusDot(online = false, size = 12.dp)
                    Spacer(Modifier.height(4.dp))
                    Text("offline", color = colors.textSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item { PSectionHeader(title = "Chip variants") }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PChip(text = "Success", variant = PChipVariant.Success, icon = Icons.Default.CheckCircle)
                PChip(text = "Warning", variant = PChipVariant.Warning)
                PChip(text = "Error", variant = PChipVariant.Error)
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PChip(text = "Info", variant = PChipVariant.Info)
                PChip(text = "Brand", variant = PChipVariant.Brand, icon = Icons.Default.Star)
                PChip(text = "Neutral", variant = PChipVariant.Neutral)
            }
        }

        item { PSectionHeader(title = "Device avatars") }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PDeviceAvatar(name = "Bùi Vân", size = 36.dp)
                PDeviceAvatar(name = "Loa Sảnh", size = 48.dp, online = true)
                PDeviceAvatar(name = "Phòng Bin", size = 64.dp, online = false)
            }
        }

        item { PSectionHeader(title = "Skeleton card") }
        item { PSkeletonCard() }
        item { PSkeletonCard(height = 64.dp) }

        item { PSectionHeader(title = "Empty state") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.card, RoundedCornerShape(20.dp))
                    .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            ) {
                PEmptyState(
                    icon = Icons.Default.Star,
                    title = "Chưa có dữ liệu",
                    description = "Bắt đầu bằng cách thêm thiết bị mới để dùng tính năng.",
                    ctaText = "Thêm ngay",
                    onCtaClick = {}
                )
            }
        }

        item { PSectionHeader(title = "Relative time (PRelativeTime)") }
        item {
            val now = Instant.now()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "Vừa xong" to now.toString(),
                    "5 phút trước" to now.minusSeconds(300).toString(),
                    "3 giờ trước" to now.minusSeconds(3 * 3600L).toString(),
                    "Hôm qua" to now.minusSeconds(28 * 3600L).toString(),
                    "Tuần trước" to now.minusSeconds(5 * 24 * 3600L).toString()
                ).forEach { (label, ts) ->
                    Row {
                        Text(
                            label,
                            modifier = Modifier.width(140.dp),
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            PRelativeTime.format(ts),
                            color = colors.textPrimary,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }
}
