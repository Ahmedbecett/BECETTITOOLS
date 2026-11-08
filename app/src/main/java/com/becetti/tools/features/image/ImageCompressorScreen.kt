package com.becetti.tools.features.image

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImageCompressorScreen() {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    var quality by remember { mutableStateOf(70f) }
    var originalBmp by remember { mutableStateOf<Bitmap?>(null) }
    var originalSizeKb by remember { mutableStateOf(0) }
    var compressedBmp by remember { mutableStateOf<Bitmap?>(null) }
    var sizes by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var status by remember { mutableStateOf("") }

    fun compress(bmp: Bitmap? = originalBmp, q: Int = quality.toInt()) {
        bmp?.let { b ->
            val out = ByteArrayOutputStream()
            b.compress(Bitmap.CompressFormat.JPEG, q, out)
            val bytes = out.toByteArray()
            compressedBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val origKb = if (originalSizeKb > 0) originalSizeKb else (b.byteCount / 1024)
            sizes = origKb to (bytes.size / 1024).coerceAtLeast(1)
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { picked ->
        picked?.let { u ->
            status = ""
            ctx.contentResolver.openInputStream(u)?.use { ins ->
                val rawBytes = ins.readBytes()
                originalSizeKb = (rawBytes.size / 1024).coerceAtLeast(1)
                val decoded = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
                originalBmp = decoded
                compress(decoded, quality.toInt())
            }
        }
    }

    val toolMeta = s.tools["image_compressor"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "image_compressor",
            title = toolMeta?.name ?: "Image Compressor",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowAmber) {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowAmber)
            ) {
                Text(s.imgSelectBtn, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "${s.imgQualityLabel}: ${quality.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlowAmber
            )
            Slider(
                value = quality,
                onValueChange = {
                    quality = it
                    compress(originalBmp, it.toInt())
                },
                valueRange = 10f..100f
            )
        }

        if (originalBmp != null || compressedBmp != null) {
            GlowingCard(accentColor = GlowCyan) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    originalBmp?.let {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.imgOriginalLabel, style = MaterialTheme.typography.labelLarge)
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = s.imgOriginalLabel,
                                modifier = Modifier.fillMaxWidth().height(150.dp)
                            )
                        }
                    }
                    compressedBmp?.let {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.imgCompressedLabel, style = MaterialTheme.typography.labelLarge, color = GlowEmerald)
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = s.imgCompressedLabel,
                                modifier = Modifier.fillMaxWidth().height(150.dp)
                            )
                        }
                    }
                }
                sizes?.let { (o, c) ->
                    Spacer(Modifier.height(8.dp))
                    val savedPct = if (o > 0) ((o - c) * 100 / o).coerceAtLeast(0) else 0
                    Text(
                        "${s.imgOriginalLabel}: $o KB → ${s.imgCompressedLabel}: $c KB (-$savedPct%)",
                        style = MaterialTheme.typography.titleMedium,
                        color = GlowEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = {
                compressedBmp?.let { bmp ->
                    try {
                        val fileName = "compressed_${System.currentTimeMillis()}.jpg"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val values = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Becetti")
                            }
                            val outUri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            outUri?.let { u ->
                                ctx.contentResolver.openOutputStream(u)?.use { out ->
                                    bmp.compress(Bitmap.CompressFormat.JPEG, quality.toInt(), out)
                                }
                            }
                            status = "${s.qrSavedNote} ($fileName)"
                        } else {
                            val dir = File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Becetti").apply { mkdirs() }
                            val file = File(dir, fileName)
                            FileOutputStream(file).use { out ->
                                bmp.compress(Bitmap.CompressFormat.JPEG, quality.toInt(), out)
                            }
                            status = "${s.qrSavedNote}: ${file.name}"
                        }
                    } catch (e: Exception) {
                        status = "${s.errorPrefix}: ${e.message}"
                    }
                }
            },
            enabled = compressedBmp != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
        ) {
            Text(s.imgSaveBtn, color = Color.White, fontWeight = FontWeight.Bold)
        }
        if (status.isNotEmpty()) {
            Text(status, color = GlowEmerald, fontWeight = FontWeight.SemiBold)
        }
    }
}
