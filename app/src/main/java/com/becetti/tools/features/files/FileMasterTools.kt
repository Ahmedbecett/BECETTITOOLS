package com.becetti.tools.features.files

import android.content.Context
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.features.tools.FilterPill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.Locale

private data class FileRow(val file: File, val isDirectory: Boolean, val size: Long, val modified: Long)

/* ======================================== File manager ====================================== */

@Composable
fun FileManagerScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Files
    val scope = rememberCoroutineScope()

    val roots = remember { storageRoots(ctx) }
    var current by remember { mutableStateOf(roots.first()) }
    var rows by remember { mutableStateOf<List<FileRow>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var renameTarget by remember { mutableStateOf<File?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<File?>(null) }
    var sortBySize by remember { mutableStateOf(false) }

    fun refresh() {
        scope.launch {
            loading = true
            error = null
            val loaded = withContext(Dispatchers.IO) {
                runCatching {
                    current.listFiles()?.map { file ->
                        FileRow(file, file.isDirectory, if (file.isDirectory) folderSize(file) else file.length(), file.lastModified())
                    }?.sortedWith(
                        compareByDescending<FileRow> { it.isDirectory }
                            .thenBy { sortBySize.not() }
                            .thenByDescending { if (sortBySize) it.size else 0L }
                            .thenBy { it.file.name.lowercase() }
                    ) ?: emptyList()
                }
            }
            loading = false
            loaded.fold(onSuccess = { rows = it }, onFailure = { error = it.message })
        }
    }

    LaunchedEffect(current, sortBySize) { refresh() }

    ToolPage(
        title = s.toolName("file_manager"),
        subtitle = s.toolDescription("file_manager"),
        icon = BecettiGlyphs.FolderOpen,
        accent = accent,
        hint = s.fileManagerHint
    ) {
        Panel(accent = accent) {
            Text(current.absolutePath, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (current.parentFile != null) {
                    FilterPill("↑ ${s.fileUp}", false, accent) { current = current.parentFile!! }
                }
                roots.forEach { root ->
                    FilterPill(
                        label = root.name.ifBlank { "storage" },
                        selected = current == root,
                        accent = accent
                    ) { current = root }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterPill(s.fileSortName, !sortBySize, accent) { sortBySize = false }
                FilterPill(s.fileSortSize, sortBySize, accent) { sortBySize = true }
                FilterPill(s.gRefresh, false, ToolAccents.Network) { refresh() }
            }
            if (loading) LoadingBlock(s.gLoading, accent = accent)
            error?.let { Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error) }
        }

        if (!loading && rows.isEmpty() && error == null) {
            EmptyState(title = s.fileEmptyFolder, message = s.fileEmptyHint, icon = BecettiGlyphs.FolderOpen, accent = accent)
        }

        if (rows.isNotEmpty()) {
            Panel(accent = accent) {
                rows.take(300).forEach { row ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (row.isDirectory) current = row.file
                                else FileExporter.openFile(ctx, row.file.absolutePath, FileExporter.mimeFor(row.file.name))
                            }
                            .padding(vertical = 7.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Icon(
                            if (row.isDirectory) BecettiGlyphs.FolderOpen else BecettiGlyphs.Invoice,
                            contentDescription = null,
                            tint = if (row.isDirectory) accent else palette.textSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                row.file.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${if (row.isDirectory) s.fileFolder else FileExporter.humanSize(row.size)} · ${FileExporter.humanDate(row.modified, false)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textSecondary
                            )
                        }
                        IconButton(
                            onClick = {
                                renameTarget = row.file
                                renameValue = row.file.name
                            },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(BecettiGlyphs.RegexStar, contentDescription = s.gEdit, tint = palette.textSecondary, modifier = Modifier.size(13.dp))
                        }
                        IconButton(
                            onClick = { deleteTarget = row.file },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(Icons.Filled.Clear, contentDescription = s.delete, tint = palette.error, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }

    BecettiSheet(show = renameTarget != null, onDismiss = { renameTarget = null }, title = s.gEdit) {
        BecettiField(value = renameValue, onValueChange = { renameValue = it }, label = s.gFileName, accent = accent)
        ActionButton(
            text = s.save,
            onClick = {
                val target = renameTarget
                if (target != null && renameValue.isNotBlank()) {
                    runCatching { target.renameTo(File(target.parentFile, renameValue.trim())) }
                    renameTarget = null
                    refresh()
                }
            },
            accent = accent
        )
    }

    ConfirmDialog(
        show = deleteTarget != null,
        title = s.delete,
        message = deleteTarget?.name.orEmpty(),
        confirmLabel = s.delete,
        dismissLabel = s.cancel,
        accent = palette.error,
        onConfirm = {
            deleteTarget?.let { file -> runCatching { file.deleteRecursively() } }
            deleteTarget = null
            scope.launch {
                withContext(Dispatchers.IO) { }
                refresh()
            }
        },
        onDismiss = { deleteTarget = null }
    )
}

private fun storageRoots(ctx: Context): List<File> {
    val roots = mutableListOf<File>()
    roots.add(File(ctx.getExternalFilesDir(null)?.absolutePath ?: ctx.filesDir.absolutePath))
    ctx.getExternalFilesDirs(null)?.forEach { if (it != null) roots.add(it) }
    roots.add(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath))
    roots.add(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath))
    roots.add(ctx.filesDir)
    return roots.distinctBy { it.absolutePath }.filter { it.exists() }
}

private fun folderSize(dir: File): Long = runCatching {
    dir.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
}.getOrDefault(0L)

/* ====================================== Storage analyzer ==================================== */

@Composable
fun StorageAnalyzerScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Files
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var folders by remember { mutableStateOf<List<Pair<String, Long>>>(emptyList()) }
    var largest by remember { mutableStateOf<List<Pair<String, Long>>>(emptyList()) }
    var total by remember { mutableStateOf(0L) }
    var scopePath by remember { mutableStateOf(ctx.getExternalFilesDir(null)?.absolutePath ?: ctx.filesDir.absolutePath) }
    var error by remember { mutableStateOf<String?>(null) }

    fun scan() {
        scope.launch {
            scanning = true
            error = null
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val root = File(scopePath)
                    val aggregates = mutableMapOf<String, Long>()
                    val files = mutableListOf<Pair<String, Long>>()
                    root.walkTopDown().forEach { file ->
                        if (file.isFile) {
                            val size = file.length()
                            files.add(file.absolutePath to size)
                            val top = file.parentFile?.let { parent ->
                                var cursor = parent
                                while (cursor.parentFile != null && cursor.parentFile != root) {
                                    cursor = cursor.parentFile ?: break
                                }
                                cursor
                            }
                            val key = top?.name ?: root.name
                            aggregates[key] = (aggregates[key] ?: 0L) + size
                        }
                    }
                    Triple(aggregates, files, files.sumOf { it.second })
                }
            }
            scanning = false
            outcome.fold(
                onSuccess = { (aggregates, files, sum) ->
                    folders = aggregates.entries.sortedByDescending { it.value }.take(12).map { it.key to it.value }
                    largest = files.sortedByDescending { it.second }.take(12)
                    total = sum
                },
                onFailure = { error = it.message ?: s.gError }
            )
        }
    }

    LaunchedEffect(Unit) { scan() }

    ToolPage(
        title = s.toolName("file_storage"),
        subtitle = s.toolDescription("file_storage"),
        icon = BecettiGlyphs.StoragePie,
        accent = accent,
        hint = s.storageHint
    ) {
        Panel(accent = accent) {
            Text(scopePath, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
            StatTile(s.storageTotal, FileExporter.humanSize(total), accent, Modifier.fillMaxWidth())
            ActionButton(text = s.gRefresh, onClick = { scan() }, accent = accent, loading = scanning, icon = BecettiGlyphs.StoragePie)
            error?.let { Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error) }
        }

        if (folders.isNotEmpty()) {
            Panel(accent = ToolAccents.Network) {
                SectionHeader(s.storageByFolder)
                folders.forEach { (name, size) ->
                    val ratio = if (total > 0) size.toFloat() / total else 0f
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text(FileExporter.humanSize(size), style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                        }
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = ToolAccents.Network,
                            trackColor = ToolAccents.Network.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        if (largest.isNotEmpty()) {
            Panel(accent = ToolAccents.Media) {
                SectionHeader(s.storageLargestFiles)
                largest.forEach { (path, size) ->
                    KeyValueRow(path.substringAfterLast('/'), FileExporter.humanSize(size), mono = false)
                }
            }
        }
    }
}

/* ====================================== Duplicate finder ==================================== */

@Composable
fun DuplicateFinderScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Network
    val scope = rememberCoroutineScope()

    var scanning by remember { mutableStateOf(false) }
    var groups by remember { mutableStateOf<List<List<File>>>(emptyList()) }
    var recoverable by remember { mutableStateOf(0L) }
    var scannedCount by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    var error by remember { mutableStateOf<String?>(null) }
    var minSize by remember { mutableStateOf(4096L) }
    var target by remember { mutableStateOf(ctx.getExternalFilesDir(null)?.absolutePath ?: ctx.filesDir.absolutePath) }

    fun scan() {
        scope.launch {
            scanning = true
            progress = 0f
            error = null
            groups = emptyList()
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val root = File(target)
                    val candidates = root.walkTopDown()
                        .filter { it.isFile && it.length() >= minSize }
                        .toList()
                    scannedCount = candidates.size
                    val bySize = candidates.groupBy { it.length() }.filterValues { it.size > 1 }
                    val duplicates = mutableListOf<List<File>>()
                    var processed = 0
                    bySize.values.forEach { sameSize ->
                        processed++
                        progress = if (bySize.isEmpty()) 1f else processed.toFloat() / bySize.size
                        val byHash = sameSize.groupBy { hashOf(it) }.filterValues { it.size > 1 }
                        duplicates.addAll(byHash.values)
                    }
                    duplicates to duplicates.sumOf { group -> group.drop(1).sumOf { it.length() } }
                }
            }
            scanning = false
            progress = 1f
            outcome.fold(
                onSuccess = { (found, wasted) ->
                    groups = found.sortedByDescending { group -> group.drop(1).sumOf { it.length() } }
                    recoverable = wasted
                },
                onFailure = { error = it.message ?: s.gError }
            )
        }
    }

    ToolPage(
        title = s.toolName("file_duplicates"),
        subtitle = s.toolDescription("file_duplicates"),
        icon = BecettiGlyphs.CopyStack,
        accent = accent,
        hint = s.duplicatesHint
    ) {
        Panel(accent = accent) {
            Text(target, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
            ActionButton(text = s.dupScan, onClick = { scan() }, accent = accent, loading = scanning, icon = BecettiGlyphs.CopyStack)
            if (scanning) LoadingBlock("${s.gProcessing} ($scannedCount ${s.projFiles})", progress = progress, accent = accent)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(s.dupGroups, "${groups.size}", accent, Modifier.weight(1f))
                StatTile(s.dupRecoverable, FileExporter.humanSize(recoverable), ToolAccents.Business, Modifier.weight(1f))
            }
            error?.let { Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error) }
            if (!scanning && groups.isEmpty() && scannedCount > 0) {
                SuccessTick(visible = true, message = s.dupNone, accent = palette.success)
            }
        }

        groups.take(20).forEach { group ->
            Panel(accent = ToolAccents.Media) {
                Text(group.first().name, style = MaterialTheme.typography.titleSmall, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${group.size} × ${FileExporter.humanSize(group.first().length())}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
                group.forEach { file ->
                    KeyValueRow(
                        label = file.parentFile?.name ?: "",
                        value = FileExporter.humanDate(file.lastModified(), false),
                        accent = palette.textSecondary
                    )
                }
            }
        }
    }
}

private fun hashOf(file: File): String = runCatching {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(64 * 1024)
        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            digest.update(buffer, 0, read)
        }
    }
    digest.digest().joinToString("") { "%02x".format(it) }
}.getOrDefault(file.absolutePath.lowercase(Locale.US))
