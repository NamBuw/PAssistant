package com.ctslab.app.pconnect.ui.screen.config

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ctslab.app.pconnect.LocalAppColors
import com.ctslab.app.pconnect.core.network.ChatMessageResponse
import com.ctslab.app.pconnect.core.network.ChatSessionResponse
import com.ctslab.app.pconnect.ui.theme.TechColors
import com.ctslab.app.pconnect.ui.viewmodel.VMDeviceDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    macAddress: String,
    deviceName: String,
    deviceId: String? = null,
    onBack: () -> Unit,
    viewModel: VMDeviceDetail = hiltViewModel()
) {
    val colors = LocalAppColors.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(deviceId) {
        if (!deviceId.isNullOrEmpty()) {
            viewModel.loadSessions(deviceId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = if (uiState.selectedSession != null)
                        "Phiên chat"
                    else
                        "Lịch sử chat - $deviceName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (uiState.selectedSession != null) {
                        viewModel.clearSelectedSession()
                    } else {
                        onBack()
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.background,
                titleContentColor = colors.textPrimary,
                navigationIconContentColor = colors.textPrimary
            )
        )

        if (deviceId.isNullOrEmpty()) {
            // No device ID available
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Thiết bị chưa được đăng ký trên server.\nVui lòng cấu hình lại thiết bị.",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (uiState.selectedSession != null) {
            // Show messages for selected session
            ChatMessagesList(
                messages = uiState.messages,
                isLoading = uiState.isLoadingMessages,
                colors = colors
            )
        } else {
            // Tabs: PTalk / KidMentor
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = colors.background,
                contentColor = TechColors.PTITRed
            ) {
                VMDeviceDetail.ChatTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                fontWeight = if (uiState.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Show session list
            ChatSessionsList(
                sessions = uiState.sessions,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onSessionClick = { session -> viewModel.loadMessages(session.id) },
                onDeleteSession = { session -> viewModel.deleteSession(deviceId ?: "", session.id) },
                colors = colors
            )
        }
    }
}

@Composable
private fun ChatSessionsList(
    sessions: List<ChatSessionResponse>,
    isLoading: Boolean,
    error: String?,
    onSessionClick: (ChatSessionResponse) -> Unit,
    onDeleteSession: (ChatSessionResponse) -> Unit,
    colors: com.ctslab.app.pconnect.ui.theme.AppColors
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TechColors.PTITRed)
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    if (sessions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.textSecondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Chưa có lịch sử chat nào",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(sessions) { session ->
            SessionCard(
                session = session,
                onClick = { onSessionClick(session) },
                onDelete = { onDeleteSession(session) },
                colors = colors
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SessionCard(
    session: ChatSessionResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    colors: com.ctslab.app.pconnect.ui.theme.AppColors
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(TechColors.PTITRed.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = TechColors.PTITRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title ?: "Phiên chat ${session.productSource}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${session.messageCount} tin nhắn • ${session.channel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                if (session.lastMessageAt != null) {
                    Text(
                        text = session.lastMessageAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xoá lịch sử phiên này",
                    tint = TechColors.PTITRed
                )
            }
        }
    }
}

@Composable
private fun ChatMessagesList(
    messages: List<ChatMessageResponse>,
    isLoading: Boolean,
    colors: com.ctslab.app.pconnect.ui.theme.AppColors
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TechColors.PTITRed)
        }
        return
    }

    if (messages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Phiên chat trống",
                color = colors.textSecondary
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(messages) { message ->
            MessageBubble(message = message, colors = colors)
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessageResponse,
    colors: com.ctslab.app.pconnect.ui.theme.AppColors
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(TechColors.PTITRed.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = TechColors.PTITRed,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) TechColors.PTITRed else colors.card
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else colors.textPrimary
                )
                if (message.sentiment != null && message.sentiment != "neutral") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.sentiment,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else colors.textSecondary
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(TechColors.OrangeAccent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = TechColors.OrangeAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

