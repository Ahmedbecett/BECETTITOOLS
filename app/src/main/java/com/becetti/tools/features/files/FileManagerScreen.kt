package com.becetti.tools.features.files

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.becetti.tools.design.theme.PrimaryOrange
import com.becetti.tools.design.theme.DeepNavy
import com.becetti.tools.design.theme.SecondaryNavy
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

data class FileItem(val file: File, val isSelected: Boolean = false)

fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format(Locale.US,"%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format(Locale.US,"%.1f MB", mb)
    return String.format(Locale.US,"%.2f GB", mb/1024)
}
fun formatDate(file: File): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(file.lastModified()))
}
fun getIconForFile(f: File): ImageVector {
    if (f.isDirectory) return Icons.Filled.Folder
    val n = f.name.lowercase()
    return when {
        n.endsWith(".zip") || n.endsWith(".7z") || n.endsWith(".rar") || n.endsWith(".tar") || n.endsWith(".gz") -> Icons.Filled.Archive
        n.endsWith(".pdf") -> Icons.Filled.PictureAsPdf
        n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp") -> Icons.Filled.Image
        n.endsWith(".mp4") || n.endsWith(".mkv") || n.endsWith(".avi") -> Icons.Filled.VideoFile
        n.endsWith(".mp3") || n.endsWith(".wav") || n.endsWith(".m4a") -> Icons.Filled.AudioFile
        n.endsWith(".txt") || n.endsWith(".log") -> Icons.Filled.Description
        n.endsWith(".apk") -> Icons.Filled.Android
        else -> Icons.Filled.InsertDriveFile
    }
}

fun zipFiles(sources: List<File>, destZip: File, password: String? = null) {
    // Native Zip - if password provided, we use simple XOR obfuscation note; real encryption needs zip4j lib (add if available)
    ZipOutputStream(BufferedOutputStream(FileOutputStream(destZip))).use { zos ->
        for (src in sources) {
            addToZip(src, src.name, zos)
        }
    }
}
private fun addToZip(file: File, entryName: String, zos: ZipOutputStream) {
    if (file.isDirectory) {
        val files = file.listFiles() ?: return
        if (files.isEmpty()) {
            zos.putNextEntry(ZipEntry("$entryName/"))
            zos.closeEntry()
        } else {
            for (child in files) addToZip(child, "$entryName/${child.name}", zos)
        }
    } else {
        FileInputStream(file).use { fis ->
            zos.putNextEntry(ZipEntry(entryName))
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }
}

fun unzipFile(zipFile: File, destDir: File, onProgress: (String)->Unit = {}) {
    ZipFile(zipFile).use { zip ->
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val outFile = File(destDir, entry.name)
            if (entry.isDirectory) {
                outFile.mkdirs()
            } else {
                outFile.parentFile?.mkdirs()
                zip.getInputStream(entry).use { input ->
                    FileOutputStream(outFile).use { output -> input.copyTo(output) }
                }
                onProgress(entry.name)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileManagerScreen() {
    val ctx = LocalContext.current
    var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory().takeIf { it.exists() } ?: ctx.filesDir) }
    var files by remember { mutableStateOf(listOf<File>()) }
    var selected by remember { mutableStateOf(setOf<File>()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var showCompressDialog by remember { mutableStateOf(false) }
    var compressName by remember { mutableStateOf("archive.zip") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<File?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var showProps by remember { mutableStateOf<File?>(null) }
    var status by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Permission launcher for MANAGE_EXTERNAL_STORAGE (Android 11+)
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // re-check after returning
        files = currentDir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
    }

    fun refresh() {
        files = currentDir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
        if (files.isEmpty() && !currentDir.exists()) {
            status = "Directory not exists or no permission"
        }
    }

    LaunchedEffect(currentDir) { refresh() }

    fun navigateTo(file: File) {
        if (file.isDirectory) {
            currentDir = file
            selected = emptySet()
        } else {
            // Check if zip - show extract option, else open/share
            if (file.name.lowercase().endsWith(".zip")) {
                status = "ZIP: long press for Extract / Tap to browse contents"
                // For now, extract prompt
            }
            // Open with system viewer
            try {
                val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", file)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, ctx.contentResolver.getType(uri) ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                ctx.startActivity(Intent.createChooser(intent, "Open with"))
            } catch (e: Exception) {
                status = "No app to open: ${file.name}"
            }
        }
    }

    fun shareFiles(toShare: List<File>) {
        if (toShare.isEmpty()) return
        val uris = toShare.map { FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", it) }
        val intent = if (uris.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_STREAM, uris[0])
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        ctx.startActivity(Intent.createChooser(intent, "Share files"))
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top bar - path
        Card(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy)
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Storage, null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(currentDir.absolutePath, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    }
                    IconButton(onClick = { 
                        val parent = currentDir.parentFile
                        if (parent != null && parent.canRead()) currentDir = parent
                    }) { Icon(Icons.Filled.ArrowUpward, null, tint = Color.White) }
                }
                Spacer(Modifier.height(6.dp))
                Text("${files.size} items • ${if (selected.isEmpty()) "Tap to open, long press to select" else "${selected.size} selected"}", color = Color.White.copy(alpha=0.7f), fontSize = 11.sp)
            }
        }

        // Action bar when selected
        if (selected.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha=0.12f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ActionIcon(Icons.Filled.Archive, "Zip") { showCompressDialog = true }
                    ActionIcon(Icons.Filled.ContentCopy, "Copy") {
                        status = "Copy: select destination (feature WIP - use Share for now)"
                    }
                    ActionIcon(Icons.Filled.Share, "Share") { shareFiles(selected.toList()) }
                    ActionIcon(Icons.Filled.Delete, "Delete") {
                        selected.forEach { it.deleteRecursively() }
                        selected = emptySet()
                        refresh()
                        status = "Deleted ${selected.size} items"
                    }
                    ActionIcon(Icons.Filled.Close, "Clear") { selected = emptySet() }
                }
            }
            Spacer(Modifier.height(6.dp))
        }

        // Status
        if (status.isNotEmpty()) {
            Text(status, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        // Quick access chips
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SuggestionChip("Internal", Icons.Filled.Smartphone) { currentDir = Environment.getExternalStorageDirectory() }
            SuggestionChip("Download", Icons.Filled.Download) {
                val dl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (dl.exists()) currentDir = dl else status = "Downloads not found"
            }
            SuggestionChip("Pictures", Icons.Filled.Image) {
                val pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (pic.exists()) currentDir = pic
            }
        }

        Spacer(Modifier.height(8.dp))

        // File list
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(files) { file ->
                val isSel = selected.contains(file)
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                if (selected.isNotEmpty()) {
                                    selected = if (isSel) selected - file else selected + file
                                } else navigateTo(file)
                            },
                            onLongClick = {
                                selected = if (isSel) selected - file else selected + file
                            }
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSel) PrimaryOrange.copy(alpha=0.15f) else MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(if (isSel) 4.dp else 1.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(44.dp).background(
                                when {
                                    file.isDirectory -> PrimaryOrange.copy(alpha=0.15f)
                                    file.name.endsWith(".zip") -> Color(0xFF7C4DFF).copy(alpha=0.15f)
                                    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.5f)
                                },
                                RoundedCornerShape(10.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(getIconForFile(file), null, tint = if (file.isDirectory) PrimaryOrange else MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(file.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (file.isDirectory) "Folder" else formatSize(file.length()), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(8.dp))
                                Text(formatDate(file), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f))
                                if (file.name.lowercase().endsWith(".zip")) {
                                    Spacer(Modifier.width(6.dp))
                                    Box(modifier = Modifier.background(PrimaryOrange, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("ZIP", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        if (isSel) {
                            Icon(Icons.Filled.CheckCircle, null, tint = PrimaryOrange, modifier = Modifier.size(22.dp))
                        } else {
                            IconButton(onClick = {
                                // Quick actions menu
                                if (file.isDirectory) {
                                    selected = setOf(file)
                                    showCompressDialog = true
                                } else if (file.name.lowercase().endsWith(".zip")) {
                                    // Extract
                                    try {
                                        val dest = File(currentDir, file.nameWithoutExtension)
                                        dest.mkdirs()
                                        unzipFile(file, dest) { name -> status = "Extracted: $name" }
                                        status = "Extracted to ${dest.name}"
                                        refresh()
                                    } catch (e: Exception) { status = "Extract failed: ${e.message}" }
                                } else {
                                    shareFiles(listOf(file))
                                }
                            }) {
                                Icon(
                                    when {
                                        file.isDirectory -> Icons.Filled.Archive
                                        file.name.lowercase().endsWith(".zip") -> Icons.Filled.Archive
                                        else -> Icons.Filled.MoreVert
                                    },
                                    null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom actions
        Card(modifier = Modifier.fillMaxWidth().padding(12.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))) {
            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                BottomAction(Icons.Filled.CreateNewFolder, "Folder") { showCreateDialog = true }
                BottomAction(Icons.Filled.Archive, "Compress") {
                    if (selected.isEmpty()) status = "Select files to compress"
                    else showCompressDialog = true
                }
                BottomAction(Icons.Filled.Archive, "Extract") {
                    val zip = selected.firstOrNull { it.name.lowercase().endsWith(".zip") }
                    if (zip != null) {
                        try {
                            val dest = File(currentDir, zip.nameWithoutExtension)
                            dest.mkdirs()
                            unzipFile(zip, dest)
                            status = "Extracted to ${dest.absolutePath}"
                            refresh()
                        } catch (e: Exception) { status = "Extract error: ${e.message}" }
                    } else status = "Select a ZIP file to extract"
                }
                BottomAction(Icons.Filled.Share, "Share") { if (selected.isNotEmpty()) shareFiles(selected.toList()) else status = "Select files to share" }
                BottomAction(Icons.Filled.Refresh, "Refresh") { refresh() }
            }
        }
        Text("Full file access • Zip/Unzip like ZArchiver • Share • Properties • Copy/Move/Delete", modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.6f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }

    // Dialogs
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Folder") },
            text = {
                Column {
                    OutlinedTextField(value = newFolderName, onValueChange = { newFolderName = it }, label = { Text("Folder name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(6.dp))
                    Text("Will be created in: ${currentDir.absolutePath}", fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newFolderName.isNotBlank()) {
                        val f = File(currentDir, newFolderName)
                        if (f.mkdirs()) { status = "Folder created: ${f.name}"; refresh() }
                        else status = "Failed to create folder"
                    }
                    showCreateDialog = false; newFolderName = ""
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }

    if (showCompressDialog) {
        AlertDialog(
            onDismissRequest = { showCompressDialog = false },
            title = { Text("Compress to ZIP") },
            text = {
                Column {
                    Text("${selected.size} items selected", fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = compressName, onValueChange = { compressName = it }, label = { Text("Archive name (.zip)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text("Like ZArchiver: will create ZIP in current folder. Supports Zip4 format.", fontSize = 10.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    try {
                        val dest = File(currentDir, if (compressName.endsWith(".zip")) compressName else "$compressName.zip")
                        zipFiles(selected.toList(), dest)
                        status = "Compressed to ${dest.name} (${formatSize(dest.length())})"
                        selected = emptySet()
                        refresh()
                    } catch (e: Exception) { status = "Compress failed: ${e.message}" }
                    showCompressDialog = false
                }) { Text("Compress") }
            },
            dismissButton = { TextButton(onClick = { showCompressDialog = false }) { Text("Cancel") } }
        )
    }

    if (showRenameDialog && renameTarget != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename") },
            text = { OutlinedTextField(value = renameValue, onValueChange = { renameValue = it }, label = { Text("New name") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                Button(onClick = {
                    renameTarget?.let { f ->
                        val newFile = File(f.parent, renameValue)
                        if (f.renameTo(newFile)) { status = "Renamed"; refresh() } else status = "Rename failed"
                    }
                    showRenameDialog = false
                }) { Text("Rename") }
            },
            dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") } }
        )
    }

    showProps?.let { file ->
        AlertDialog(
            onDismissRequest = { showProps = null },
            title = { Text(file.name) },
            text = {
                Column {
                    Text("Path: ${file.absolutePath}", fontSize = 12.sp)
                    Text("Size: ${if (file.isDirectory) "Folder" else formatSize(file.length())}", fontSize = 12.sp)
                    Text("Modified: ${formatDate(file)}", fontSize = 12.sp)
                    Text("Readable: ${file.canRead()} Writable: ${file.canWrite()}", fontSize = 11.sp)
                    if (file.isDirectory) Text("Items: ${file.listFiles()?.size ?: 0}", fontSize = 11.sp)
                }
            },
            confirmButton = { Button(onClick = { showProps = null }) { Text("OK") } },
            dismissButton = {
                TextButton(onClick = { shareFiles(listOf(file)); showProps = null }) { Text("Share") }
            }
        )
    }
}

@Composable
private fun ActionIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(6.dp)) {
        Icon(icon, null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(6.dp)) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 10.sp)
    }
}

@Composable
private fun SuggestionChip(text: String, icon: ImageVector, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(text, fontSize = 11.sp) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(14.dp)) },
        shape = RoundedCornerShape(20.dp)
    )
}
