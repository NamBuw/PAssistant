package com.ctslab.app.pconnect.ui.screen.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ctslab.app.pconnect.core.network.BannedTopicDto
import com.ctslab.app.pconnect.ui.viewmodel.VMBannedWords

/**
 * Parental moderation screen: manage banned topics (AI-suggested words) and standalone words.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannedWordsScreen(
    onBack: () -> Unit,
    viewModel: VMBannedWords = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }

    var newTopic by remember { mutableStateOf("") }
    var newWord by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Từ ngữ & chủ đề bị cấm") },
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

            // ── Topics ──
            Text("Chủ đề bị cấm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Nhập một chủ đề — AI sẽ gợi ý các từ cấm liên quan. Bạn có thể sửa/thêm sau.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newTopic,
                    onValueChange = { newTopic = it },
                    placeholder = { Text("vd: bạo lực, ma tuý...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.suggestTopic(newTopic); newTopic = "" },
                    enabled = newTopic.isNotBlank() && !uiState.suggesting
                ) {
                    if (uiState.suggesting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Gợi ý từ AI")
                    }
                }
            }

            uiState.topics.forEach { topic ->
                TopicCard(
                    topic = topic,
                    onToggleTopic = { viewModel.toggleTopic(topic.id, topic.isActive) },
                    onDeleteTopic = { viewModel.deleteTopic(topic.id) },
                    onToggleWord = { wid, active -> viewModel.toggleWord(wid, active) },
                    onDeleteWord = { wid -> viewModel.deleteWord(wid) },
                    onAddWord = { w -> viewModel.addWord(w, topicId = topic.id) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // ── Standalone words ──
            Text("Từ ngữ bị cấm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    placeholder = { Text("Nhập từ cần cấm...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.addWord(newWord); newWord = "" },
                    enabled = newWord.isNotBlank()
                ) { Icon(Icons.Default.Add, contentDescription = "Thêm từ") }
            }

            if (uiState.isLoading) {
                Text("Đang tải...", style = MaterialTheme.typography.bodySmall)
            } else if (uiState.words.isEmpty()) {
                Text("Chưa có từ nào bị cấm.", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            uiState.words.forEach { w ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        w.word,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (w.isActive) TextDecoration.None else TextDecoration.LineThrough
                    )
                    Switch(checked = w.isActive, onCheckedChange = { viewModel.toggleWord(w.id, w.isActive) })
                    IconButton(onClick = { viewModel.deleteWord(w.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xoá", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopicCard(
    topic: BannedTopicDto,
    onToggleTopic: () -> Unit,
    onDeleteTopic: () -> Unit,
    onToggleWord: (String, Boolean) -> Unit,
    onDeleteWord: (String) -> Unit,
    onAddWord: (String) -> Unit
) {
    var addWordText by remember(topic.id) { mutableStateOf("") }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    topic.topic,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (topic.isActive) TextDecoration.None else TextDecoration.LineThrough
                )
                Switch(checked = topic.isActive, onCheckedChange = { onToggleTopic() })
                IconButton(onClick = onDeleteTopic) {
                    Icon(Icons.Default.Delete, contentDescription = "Xoá chủ đề", tint = MaterialTheme.colorScheme.error)
                }
            }
            topic.words.forEach { w ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "• ${w.word}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (w.isActive) TextDecoration.None else TextDecoration.LineThrough
                    )
                    Switch(checked = w.isActive, onCheckedChange = { onToggleWord(w.id, w.isActive) })
                    IconButton(onClick = { onDeleteWord(w.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xoá từ", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = addWordText,
                    onValueChange = { addWordText = it },
                    placeholder = { Text("Thêm từ vào chủ đề...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onAddWord(addWordText); addWordText = "" },
                    enabled = addWordText.isNotBlank()
                ) { Icon(Icons.Default.Add, contentDescription = "Thêm từ") }
            }
        }
    }
}
