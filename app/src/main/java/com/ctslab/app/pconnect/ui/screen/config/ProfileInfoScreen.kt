package com.ctslab.app.pconnect.ui.screen.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ctslab.app.pconnect.core.network.ChildProfileDto
import com.ctslab.app.pconnect.core.network.ParentProfileDto
import com.ctslab.app.pconnect.ui.viewmodel.VMProfileInfo

/**
 * Read-only "Thông tin cá nhân": shows the parent profile and each child fetched from the
 * Dashboard (/api/v1/profile, /api/v1/children). Pure display — no inputs, no save.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    onBack: () -> Unit,
    viewModel: VMProfileInfo = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cá nhân") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.headlineSmall) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }

            // ── Parent ──
            Text("Phụ huynh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ParentInfoCard(uiState.profile)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── Children ──
            Text("Hồ sơ các bé", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (!uiState.isLoading && uiState.children.isEmpty()) {
                Text(
                    "Chưa có hồ sơ bé nào.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            uiState.children.forEach { child ->
                ChildInfoCard(child)
            }
        }
    }
}

@Composable
private fun ParentInfoCard(profile: ParentProfileDto?) {
    val displayName = profile?.fullName?.takeIf { it.isNotBlank() }
        ?: profile?.displayName?.takeIf { it.isNotBlank() }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow("Họ tên", displayName)
            InfoRow("Ngày sinh", formatDob(profile?.dateOfBirth))
            InfoRow("SĐT", profile?.phone)
            InfoRow("Email", profile?.email)
        }
    }
}

@Composable
private fun ChildInfoCard(child: ChildProfileDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow("Tên", child.fullName)
            InfoRow("Ngày sinh", formatDob(child.dateOfBirth))
            InfoRow("Lớp", child.grade?.takeIf { it.isNotBlank() }?.let { "Lớp $it" })
            InfoRow("Chương trình học", curriculumLabel(child.curriculum))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Map curriculum codes to Vietnamese labels (kept in sync with KidMentor's options). */
private fun curriculumLabel(code: String?): String? = when (code) {
    "chan_troi_sang_tao" -> "Chân trời sáng tạo"
    "canh_dieu" -> "Cánh diều"
    "ket_noi_tri_thuc" -> "Kết nối tri thức"
    else -> code
}

/** "YYYY-MM-DD" -> "DD/MM/YYYY"; leaves any other format untouched. */
private fun formatDob(iso: String?): String? {
    if (iso.isNullOrBlank()) return null
    val parts = iso.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else iso
}
