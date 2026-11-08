package com.becetti.tools.features.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.data.local.entity.NoteEntity
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val clipboard = LocalClipboardManager.current
    val repo = remember {
        ToolRepository(
            app.database.favoriteDao(),
            app.database.historyDao(),
            app.database.recentToolDao(),
            app.database.noteDao()
        )
    }
    val notes by repo.notes().collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    val filtered = remember(notes, query) {
        if (query.isBlank()) notes
        else notes.filter { it.title.contains(query, true) || it.content.contains(query, true) }
    }
    var showDialog by remember { mutableStateOf(false) }
    var editNote by remember { mutableStateOf<NoteEntity?>(null) }
    val scope = rememberCoroutineScope()

    val noteColors = listOf(GlowOrange, GlowCyan, GlowEmerald, GlowViolet, GlowAmber, GlowRose)
    val toolMeta = s.tools["notes"]

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editNote = null
                    showDialog = true
                },
                containerColor = PrimaryOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = s.notesNewNote)
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GlowingToolHeader(
                    toolId = "notes",
                    title = toolMeta?.name ?: "Smart Notes Manager",
                    subtitle = toolMeta?.description ?: ""
                )
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(s.notesSearchHint) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                if (filtered.isEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(s.notesEmptyState, style = MaterialTheme.typography.bodyMedium)
                }
            }
            itemsIndexed(filtered, key = { _, n -> n.id }) { idx, n ->
                val accent = noteColors[idx % noteColors.size]
                GlowingCard(
                    accentColor = accent,
                    onClick = {
                        editNote = n
                        showDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                n.title.ifEmpty { "(Untitled)" },
                                style = MaterialTheme.typography.titleMedium,
                                color = accent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                n.content.take(160),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row {
                            AssistChip(
                                onClick = { clipboard.setText(AnnotatedString("${n.title}\n${n.content}")) },
                                label = { Text(s.copy) }
                            )
                            IconButton(onClick = { scope.launch { repo.deleteNote(n) } }) {
                                Icon(Icons.Filled.Delete, contentDescription = s.delete, tint = Error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var title by remember(editNote) { mutableStateOf(editNote?.title ?: "") }
        var content by remember(editNote) { mutableStateOf(editNote?.content ?: "") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editNote == null) s.notesNewNote else s.notesEditNote, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(s.notesTitleLabel) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(s.notesContentLabel) },
                        modifier = Modifier.height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repo.saveNote(
                                NoteEntity(
                                    id = editNote?.id ?: 0,
                                    title = title,
                                    content = content
                                )
                            )
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text(s.save, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text(s.cancel)
                }
            }
        )
    }
}
