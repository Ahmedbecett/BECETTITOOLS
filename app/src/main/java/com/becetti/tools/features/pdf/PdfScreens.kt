package com.becetti.tools.features.pdf

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private fun writePdfOutput(ctx: Context, fileName: String, writer: (OutputStream) -> Unit): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Becetti")
        }
        val outUri = ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
            ?: error("Could not create MediaStore entry")
        ctx.contentResolver.openOutputStream(outUri)?.use { out -> writer(out) }
        "Documents/Becetti/$fileName"
    } else {
        val dir = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Becetti").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { out -> writer(out) }
        file.absolutePath
    }
}

@Composable
fun ImageToPdfScreen() {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pageSize by remember { mutableStateOf("A4") }
    var status by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        uris = it
        status = ""
    }

    val toolMeta = s.tools["image_to_pdf"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "image_to_pdf",
            title = toolMeta?.name ?: "Image to PDF",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowRose) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A4", "A3", "Letter").forEach { sz ->
                    FilterChip(selected = pageSize == sz, onClick = { pageSize = sz }, label = { Text(sz) })
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowRose)
            ) {
                Text("${s.pdfSelectImages} (${uris.size})", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (uris.isNotEmpty()) {
            GlowingCard(accentColor = GlowEmerald) {
                Button(
                    onClick = {
                        scope.launch {
                            isProcessing = true
                            status = ""
                            try {
                                val savedPath = withContext(Dispatchers.IO) {
                                    val doc = PdfDocument()
                                    try {
                                        uris.forEachIndexed { idx, uri ->
                                            ctx.contentResolver.openInputStream(uri)?.use { ins ->
                                                val bmp = BitmapFactory.decodeStream(ins) ?: return@use
                                                val (w, h) = when (pageSize) {
                                                    "A3" -> 842 to 1190
                                                    "Letter" -> 612 to 792
                                                    else -> 595 to 842
                                                }
                                                val pageInfo = PdfDocument.PageInfo.Builder(w, h, idx + 1).create()
                                                val page = doc.startPage(pageInfo)
                                                val scaled = Bitmap.createScaledBitmap(bmp, w - 40, h - 40, true)
                                                page.canvas.drawBitmap(scaled, 20f, 20f, null)
                                                doc.finishPage(page)
                                                if (scaled != bmp) scaled.recycle()
                                                bmp.recycle()
                                            }
                                        }
                                        val fileName = "becetti_${System.currentTimeMillis()}.pdf"
                                        writePdfOutput(ctx, fileName) { out -> doc.writeTo(out) }
                                    } finally {
                                        doc.close()
                                    }
                                }
                                status = "${s.pdfExportedSuccess}: $savedPath"
                            } catch (e: Exception) {
                                status = "${s.errorPrefix}: ${e.message}"
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text(s.pdfExportBtn, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { uris = emptyList(); status = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(s.clear)
                }
            }
        }
        if (status.isNotEmpty()) {
            Text(status, color = GlowEmerald, fontWeight = FontWeight.Bold)
        }
        Text(s.pdfPreservesNote, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PdfMergerScreen() {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var status by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        uris = it
        status = ""
    }

    val toolMeta = s.tools["pdf_merger"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "pdf_merger",
            title = toolMeta?.name ?: "PDF Merger",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowIndigo) {
            Button(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowIndigo)
            ) {
                Text("${s.pdfSelectPdfs} (${uris.size})", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (uris.isNotEmpty()) {
            GlowingCard(accentColor = GlowCyan) {
                uris.forEachIndexed { idx, uri ->
                    Text("${idx + 1}. ${uri.lastPathSegment ?: "document.pdf"}", fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isProcessing = true
                            status = ""
                            try {
                                val savedPath = withContext(Dispatchers.IO) {
                                    runCatching { PDFBoxResourceLoader.init(ctx.applicationContext) }
                                    val outDoc = PDDocument()
                                    val sourceDocs = mutableListOf<PDDocument>()
                                    try {
                                        uris.forEach { uri ->
                                            ctx.contentResolver.openInputStream(uri)?.use { ins ->
                                                val doc = PDDocument.load(ins)
                                                sourceDocs.add(doc)
                                                doc.pages.forEach { page -> outDoc.addPage(page) }
                                            }
                                        }
                                        val fileName = "merged_${System.currentTimeMillis()}.pdf"
                                        writePdfOutput(ctx, fileName) { out -> outDoc.save(out) }
                                    } finally {
                                        runCatching { outDoc.close() }
                                        sourceDocs.forEach { src -> runCatching { src.close() } }
                                    }
                                }
                                status = "${s.pdfMergedSuccess}: $savedPath"
                            } catch (e: Exception) {
                                status = "${s.errorPrefix}: ${e.message}"
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text(s.pdfMergeBtn, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { uris = emptyList(); status = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(s.clear)
                }
            }
        }
        if (status.isNotEmpty()) {
            Text(status, color = GlowEmerald, fontWeight = FontWeight.Bold)
        }
        Text(s.pdfPreservesNote, style = MaterialTheme.typography.bodySmall)
    }
}
