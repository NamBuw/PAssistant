package com.ctslab.app.pconnect.ui.screen.config

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ctslab.app.pconnect.core.network.TokenManager
import com.ctslab.app.pconnect.ui.theme.PTalkTokens

private data class PlanInfo(
    val tier: String,
    val name: String,
    val quota: String,
    val price: String,
    val badge: String?
)

/** Plans differ ONLY by daily question quota. */
private val PLANS = listOf(
    PlanInfo("basic", "Cơ Bản", "20 câu hỏi mỗi ngày", "Miễn phí", null),
    PlanInfo("pro", "Pro", "500 câu hỏi mỗi ngày", "800.000đ/tháng", "★ Đáng giá nhất"),
    PlanInfo("ultra", "Ultra", "Không giới hạn câu hỏi", "1.500.000đ/tháng", null),
)

private fun planRank(tier: String): Int = when (tier) {
    "pro" -> 1; "ultra" -> 2; "admin" -> 3; else -> 0
}

private fun tierLabel(tier: String): String = when (tier) {
    "admin" -> "Admin"
    "ultra" -> "Ultra Member"
    "pro" -> "Pro Member"
    else -> "Thành viên miễn phí"
}

/**
 * Resolve the user's subscription tier from the JWT access-token claims
 * (`subscription_tier`, `is_superuser`). P-Connect has no /quota endpoint, so the
 * token is the only source. Any decode failure falls back to "basic" (fail-safe).
 */
private fun resolveTier(token: String?): String {
    if (token.isNullOrBlank()) return "basic"
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return "basic"
        val payload = String(
            android.util.Base64.decode(
                parts[1],
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING
            )
        )
        val json = org.json.JSONObject(payload)
        if (json.optBoolean("is_superuser", false)) "admin"
        else json.optString("subscription_tier", "basic").ifBlank { "basic" }
    } catch (e: Exception) {
        "basic"
    }
}

/**
 * Subscription / plans screen ("Gói đăng ký"). Showcases the three tiers and points
 * upgrades to the contact email — there is no payment flow yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val currentTier = remember { resolveTier(TokenManager(context).getToken()) }
    var selectedIndex by remember {
        mutableStateOf(
            when (currentTier) {
                "pro" -> 1
                "ultra", "admin" -> 2
                else -> 1   // showcase Pro by default
            }
        )
    }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nâng cấp gói") },
            text = {
                Text("Tính năng thanh toán đang được hoàn thiện.\n\nVui lòng liên hệ ctslab@ptit.vn để nâng cấp gói.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ctslab@ptit.vn")).apply {
                        putExtra(Intent.EXTRA_SUBJECT, "Nâng cấp gói P-Connect")
                    }
                    try { context.startActivity(intent) } catch (e: Exception) { /* no email app */ }
                }) { Text("Gửi email") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Đóng") }
            }
        )
    }

    Scaffold(
        containerColor = PTalkTokens.Colors.ProfileBg,
        topBar = {
            TopAppBar(
                title = { Text("Gói đăng ký") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = MaterialTheme.typography.headlineSmall)
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Các gói chỉ khác nhau ở số câu hỏi mỗi ngày.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Gói hiện tại: ${tierLabel(currentTier)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PTalkTokens.Colors.TextSubGreeting
            )

            // ── Segmented selector ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                PLANS.forEachIndexed { i, plan ->
                    val selected = i == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedIndex = i }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            plan.name,
                            color = if (selected) Color.White else Color(0xFF707072),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── Detail card ──
            val plan = PLANS[selectedIndex]
            val curRank = planRank(currentTier)
            val planR = planRank(plan.tier)
            val badgeText = when {
                planR == curRank -> "Đang dùng"
                planR > curRank -> plan.badge
                else -> null
            }
            val ctaText = when {
                planR == curRank -> "Đang dùng"
                planR > curRank -> "Nâng cấp ${plan.name}"
                else -> "Bạn đang dùng gói cao hơn"
            }
            val ctaEnabled = planR > curRank

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            plan.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111111),
                            modifier = Modifier.weight(1f)
                        )
                        if (badgeText != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    badgeText,
                                    color = Color(0xFFE07B20),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(plan.quota, fontSize = 15.sp, color = Color(0xFF666666))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(plan.price, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111111))
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showDialog = true },
                        enabled = ctaEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTalkTokens.Colors.PTITRedDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text(ctaText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                "Liên hệ ctslab@ptit.vn để được hỗ trợ nâng cấp.",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
