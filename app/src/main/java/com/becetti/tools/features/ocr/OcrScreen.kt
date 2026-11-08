package com.becetti.tools.features.ocr

import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.data.local.entity.NoteEntity
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch

@Composable
fun OcrScreen(app: BecettiToolsApp) {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    var uri by remember { mutableStateOf<Uri?>(null) }
    var text by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var savedNote by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uri = it
        savedNote = false
        copied = false
    }

    fun runOcr() {
        uri?.let { u ->
            loading = true
            savedNote = false
            copied = false
            try {
                ctx.contentResolver.openInputStream(u)?.use { ins ->
                    val bmp = BitmapFactory.decodeStream(ins)
                    if (bmp == null) {
                        text = "${s.errorPrefix}: Could not decode image."
                        loading = false
                        return@use
                    }
                    val image = InputImage.fromBitmap(bmp, 0)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    recognizer.process(image)
                        .addOnSuccessListener { res ->
                            text = res.text.ifBlank { s.ocrNoTextDetected }
                            loading = false
                        }
                        .addOnFailureListener { err ->
                            text = "${s.errorPrefix}: ${err.message}"
                            loading = false
                        }
                        .addOnCompleteListener {
                            runCatching { recognizer.close() }
                        }
                }
            } catch (e: Exception) {
                text = "${s.errorPrefix}: ${e.message}"
                loading = false
            }
        }
    }

    val toolMeta = s.tools["ocr"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "ocr",
            title = toolMeta?.name ?: "OCR Text Extractor",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowCyan) {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowCyan)
            ) {
                Text(s.imgSelectBtn, color = Color.White, fontWeight = FontWeight.Bold)
            }
            uri?.let {
                Spacer(Modifier.height(6.dp))
                Text(it.lastPathSegment ?: it.toString(), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { runOcr() },
                enabled = uri != null && !loading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
            ) {
                if (loading) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(s.ocrExtractBtn, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (text.isNotEmpty()) {
            GlowingCard(accentColor = GlowEmerald) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(text))
                            copied = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                    ) {
                        Text(if (copied) s.copied else s.ocrCopyText, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                app.database.noteDao().insert(
                                    NoteEntity(
                                        title = "OCR • ${System.currentTimeMillis() % 10000}",
                                        content = text
                                    )
                                )
                                savedNote = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (savedNote) s.ocrSavedToNotes else s.ocrSaveAsNote)
                    }
                }
            }
        }
        Text(s.ocrLocalNote, style = MaterialTheme.typography.bodySmall)
    }
}
