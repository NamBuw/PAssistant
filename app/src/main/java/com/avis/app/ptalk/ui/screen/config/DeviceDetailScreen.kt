package com.avis.app.ptalk.ui.screen.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.avis.app.ptalk.LocalAppColors
import com.avis.app.ptalk.core.network.ChatMessageResponse
import com.avis.app.ptalk.core.network.ChatSessionResponse
import com.avis.app.ptalk.ui.component.foundation.PChip
import com.avis.app.ptalk.ui.component.foundation.PChipVariant
import com.avis.app.ptalk.ui.component.foundation.PEmptyState
import com.avis.app.ptalk.ui.component.foundation.PRelativeTime
import com.avis.app.ptalk.ui.component.foundation.PSkeletonCard
import com.avis.app.ptalk.ui.theme.AppColors
import com.avis.app.ptalk.ui.viewmodel.VMDeviceDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    macAddress: String,
    deviceName: String,
    deviceId: String? = null,
    onBack: () -> Unit,
    viewModel: VMDeviceDetail = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(deviceId) {
        if (!deviceId.isNullOrEmpty()) {
            viewModel.loadSessions(deviceId)
        }
    }

    DeviceDetailContent(
        deviceName = deviceName,
        deviceId = deviceId,
        uiState = uiState,
        onBack = onBack,
        onSelectTab = viewModel::selectTab,
        onSelectSession = { session -> viewModel.loadMessages(session.id) },
        onClearSelectedSession = viewModel::clearSelectedSession
    )
}

/**
 * Stateless body for DeviceDetailScreen — preview & gallery friendly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeviceDetailContent(
    deviceName: String,
    deviceId: String?,
    uiState: VMDeviceDetail.UiState,
    onBack: () -> Unit,
    onSelectTab: (VMDeviceDetail.ChatTab) -> Unit,
    onSelectSession: (ChatSessionResponse) -> Unit,
    onClearSelectedSession: () -> Unit
) {
    val colors = LocalAppColors.current

    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (uiState.selectedSession != null) "Phiên chat" else deviceName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (uiState.selectedSession == null) {
                        Text(
                            text = "Lịch sử trò chuyện",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (uiState.selectedSession != null) onClearSelectedSession()
                    else onBack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = colors.textPrimary
                    )
                }
            },
            actions = {
                if (uiState.selectedSession == null && !uiState.sessions.isEmpty()) {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (showSearch) "Đóng tìm kiếm" else "Tìm kiếm",
                            tint = colors.textPrimary
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colors.background
            ),
            scrollBehavior = scrollBehavior
        )

        when {
            deviceId.isNullOrEmpty() -> {
                PEmptyState(
                    icon = Icons.Default.Error,
                    title = "Thiết bị chưa được đăng ký",
                    description = "Thiết bị chưa có mặt trên server. Vui lòng cấu hình lại để đăng ký và xem lịch sử chat.",
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.selectedSession != null -> {
                ChatMessagesList(
                    messages = uiState.messages,
                    isLoading = uiState.isLoadingMessages,
                    colors = colors
                )
            }
            else -> {
                AnimatedVisibility(
                    visible = showSearch,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        colors = colors
                    )
                }

                PrimaryTabRow(
                    selectedTabIndex = uiState.selectedTab.ordinal,
                    containerColor = colors.background,
                    contentColor = colors.primary
                ) {
                    VMDeviceDetail.ChatTab.entries.forEach { tab ->
                        Tab(
                            selected = uiState.selectedTab == tab,
                            onClick = { onSelectTab(tab) },
                            text = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (uiState.selectedTab == tab) FontWeight.SemiBold else FontWeight.Medium
                                    )
                                )
                            },
                            selectedContentColor = colors.primary,
                            unselectedContentColor = colors.textSecondary
                        )
                    }
                }

                ChatSessionsList(
                    sessions = uiState.sessions.filter {
                        if (searchQuery.isBlank()) true
                        else (it.title ?: "").contains(searchQuery, ignoreCase = true) ||
                            (it.channel).contains(searchQuery, ignoreCase = true)
                    },
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onSessionClick = onSelectSession,
                    colors = colors,
                    productLabel = uiState.selectedTab.label
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    colors: AppColors
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Tìm phiên chat...", color = colors.textMuted) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary)
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Xoá", tint = colors.textSecondary)
                }
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            cursorColor = colors.primary,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
        )
    )
}

@Composable
private fun ChatSessionsList(
    sessions: List<ChatSessionResponse>,
    isLoading: Boolean,
    error: String?,
    onSessionClick: (ChatSessionResponse) -> Unit,
    colors: AppColors,
    productLabel: String
) {
    if (isLoading) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(5) { PSkeletonCard() }
        }
        return
    }

    if (error != null) {
        PEmptyState(
            icon = Icons.Default.Error,
            title = "Không tải được",
            description = error,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    if (sessions.isEmpty()) {
        PEmptyState(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Chưa có phiên chat $productLabel",
            description = "Khi bạn trò chuyện với thiết bị, các phiên sẽ xuất hiện ở đây.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    // Group by recency bucket while preserving order (sessions are
    // already returned newest-first by the API).
    val grouped = remember(sessions) {
        sessions.groupBy { PRelativeTime.bucket(it.lastMessageAt ?: it.startedAt) }
    }
    val orderedBuckets = PRelativeTime.Bucket.values().filter { grouped[it]?.isNotEmpty() == true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        orderedBuckets.forEach { bucket ->
            val items = grouped[bucket] ?: return@forEach
            item(key = "header-${bucket.name}") {
                Text(
                    text = bucket.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.6.sp()
                    ),
                    color = colors.textSecondary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                )
            }
            items(items, key = { it.id }) { session ->
                SessionCard(
                    session = session,
                    onClick = { onSessionClick(session) },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: ChatSessionResponse,
    onClick: () -> Unit,
    colors: AppColors
) {
    val isPTalk = session.productSource.equals("ptalk", ignoreCase = true)
    val accent = if (isPTalk) colors.primary else colors.accent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colors.card, RoundedCornerShape(20.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent.copy(alpha = if (colors.isDark) 0.22f else 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title?.takeIf { it.isNotBlank() }
                        ?: "Phiên ${if (isPTalk) "PTalk" else "KidMentor"}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PChip(
                        text = "${session.messageCount} tin nhắn",
                        variant = PChipVariant.Neutral
                    )
                    Spacer(Modifier.width(6.dp))
                    PChip(
                        text = session.channel,
                        variant = if (isPTalk) PChipVariant.Brand else PChipVariant.Info
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = PRelativeTime.format(session.lastMessageAt ?: session.startedAt),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted
            )
        }
    }
}

@Composable
private fun ChatMessagesList(
    messages: List<ChatMessageResponse>,
    isLoading: Boolean,
    colors: AppColors
) {
    if (isLoading) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(6) { PSkeletonCard(height = 64.dp) }
        }
        return
    }

    if (messages.isEmpty()) {
        PEmptyState(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Phiên chat trống",
            description = "Phiên này chưa có tin nhắn nào.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val listState = rememberLazyListState()

    // Auto-scroll to the latest message on first load.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Render with date separators between days. Iteration order
        // is preserved (server delivers oldest-first or newest-first;
        // we simply group consecutive items by the same calendar day).
        var lastBucket: String? = null
        var lastSender: String? = null
        var lastTimestamp: String? = null

        messages.forEachIndexed { index, message ->
            val bucketKey = PRelativeTime.bucket(message.createdAt).name
            if (bucketKey != lastBucket) {
                item(key = "sep-$index-$bucketKey") {
                    DateSeparator(label = PRelativeTime.bucket(message.createdAt).label, colors = colors)
                }
                lastBucket = bucketKey
                lastSender = null
            }

            val groupedWithPrevious = lastSender == message.sender &&
                tooClose(lastTimestamp, message.createdAt)

            item(key = message.id) {
                MessageBubble(
                    message = message,
                    grouped = groupedWithPrevious,
                    colors = colors
                )
            }
            lastSender = message.sender
            lastTimestamp = message.createdAt
        }
    }
}

private fun tooClose(prev: String?, current: String): Boolean {
    if (prev == null) return false
    return runCatching {
        val a = java.time.OffsetDateTime.parse(prev).toInstant()
        val b = java.time.OffsetDateTime.parse(current).toInstant()
        java.time.Duration.between(a, b).abs().seconds < 120
    }.getOrDefault(false)
}

@Composable
private fun DateSeparator(label: String, colors: AppColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = colors.outlineVariant
        )
        Text(
            text = label,
            modifier = Modifier
                .padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = colors.outlineVariant
        )
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessageResponse,
    grouped: Boolean,
    colors: AppColors
) {
    val isUser = message.sender == "user"
    var showTimestamp by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (grouped) 2.dp else 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isUser) {
                AvatarSlot(
                    visible = !grouped,
                    icon = Icons.Default.SmartToy,
                    tint = colors.primary,
                    bg = colors.primary.copy(alpha = if (colors.isDark) 0.22f else 0.12f)
                )
                Spacer(Modifier.width(8.dp))
            }

            val bubbleShape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else if (grouped) 18.dp else 4.dp,
                bottomEnd = if (isUser) (if (grouped) 18.dp else 4.dp) else 18.dp
            )

            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .background(
                        color = if (isUser) colors.primary else colors.cardHighlight,
                        shape = bubbleShape
                    )
                    .clickable { showTimestamp = !showTimestamp }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) colors.onPrimary else colors.textPrimary
                )

                if (!message.sentiment.isNullOrEmpty() && message.sentiment != "neutral") {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = sentimentLabel(message.sentiment),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUser) colors.onPrimary.copy(alpha = 0.75f)
                                else colors.textSecondary
                    )
                }
            }

            if (isUser) {
                Spacer(Modifier.width(8.dp))
                AvatarSlot(
                    visible = !grouped,
                    icon = Icons.Default.Person,
                    tint = colors.warning,
                    bg = colors.warning.copy(alpha = if (colors.isDark) 0.22f else 0.12f)
                )
            }
        }

        AnimatedVisibility(
            visible = showTimestamp,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = PRelativeTime.format(message.createdAt),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 40.dp, end = 40.dp),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                textAlign = if (isUser) androidx.compose.ui.text.style.TextAlign.End
                            else androidx.compose.ui.text.style.TextAlign.Start
            )
        }
    }
}

@Composable
private fun AvatarSlot(
    visible: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bg: Color
) {
    if (visible) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(bg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        Spacer(Modifier.width(28.dp))
    }
}

private fun sentimentLabel(raw: String): String = when (raw.lowercase()) {
    "positive", "happy", "joy" -> "😊 Tích cực"
    "negative", "sad", "angry" -> "☹️ Tiêu cực"
    "surprise", "surprised" -> "😮 Bất ngờ"
    else -> raw.replaceFirstChar { it.uppercaseChar() }
}

private fun Number.sp() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(),
    androidx.compose.ui.unit.TextUnitType.Sp
)
