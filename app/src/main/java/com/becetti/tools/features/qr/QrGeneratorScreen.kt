package com.becetti.tools.features.qr

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

fun generateQrBitmap(content: String, size: Int = 512, fgArgb: Int = android.graphics.Color.BLACK): Bitmap? {
    if (content.isBlank()) return null
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) fgArgb else android.graphics.Color.WHITE)
        }
    }
    return bmp
}

@Composable
fun QrGeneratorScreen() {
    val s = LocalAppStrings.current
    var typeIdx by remember { mutableStateOf(0) }
    var text by remember { mutableStateOf("https://becetti.tools") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPass by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF172235)) }
    var status by remember { mutableStateOf("") }

    val qrColors = listOf(
        Color(0xFF172235),
        PrimaryOrange,
        GlowEmerald,
        GlowBlue,
        GlowViolet
    )

    val content = when (typeIdx) {
        2 -> "WIFI:T:WPA;S:$wifiSsid;P:$wifiPass;;"
        3 -> "BEGIN:VCARD\nVERSION:3.0\nFN:$contactName\nTEL:$contactPhone\nEND:VCARD"
        else -> text
    }
    val bmp = remember(content, selectedColor) {
        try {
            generateQrBitmap(content, 512, selectedColor.toArgb())
        } catch (e: Exception) {
            null
        }
    }
    val ctx = LocalContext.current
    val toolMeta = s.tools["qr_generator"]

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "qr_generator",
            title = toolMeta?.name ?: "QR Code Studio",
            subtitle = toolMeta?.description ?: ""
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                0 to s.qrTypeText,
                1 to s.qrTypeUrl,
                2 to s.qrTypeWifi,
                3 to s.qrTypeContact
            ).forEach { (idx, label) ->
                FilterChip(
                    selected = typeIdx == idx,
                    onClick = { typeIdx = idx },
                    label = { Text(label) }
                )
            }
        }

        GlowingCard(accentColor = GlowViolet) {
            when (typeIdx) {
                0, 1 -> OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(s.qrContentLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                2 -> {
                    OutlinedTextField(
                        value = wifiSsid,
                        onValueChange = { wifiSsid = it },
                        label = { Text(s.qrWifiSsid) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wifiPass,
                        onValueChange = { wifiPass = it },
                        label = { Text(s.qrWifiPassword) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                3 -> {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text(s.qrContactName) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text(s.qrContactPhone) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(s.qrColorLabel, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                qrColors.forEach { col ->
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { selectedColor = col },
                        color = col,
                        shape = CircleShape,
                        border = BorderStroke(
                            if (selectedColor == col) 3.dp else 1.dp,
                            if (selectedColor == col) PrimaryOrange else Color.White.copy(alpha = 0.4f)
                        )
                    ) {}
                }
            }
        }

        bmp?.let {
            GlowingCard(accentColor = selectedColor) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.padding(12.dp).size(220.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                bmp?.let { b ->
                    try {
                        val fileName = "qr_${System.currentTimeMillis()}.png"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val values = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Becetti")
                            }
                            val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            uri?.let { u ->
                                ctx.contentResolver.openOutputStream(u)?.use { out ->
                                    b.compress(Bitmap.CompressFormat.PNG, 100, out)
                                }
                            }
                            status = "${s.qrSavedNote} ($fileName)"
                        } else {
                            val dir = File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Becetti").apply { mkdirs() }
                            val file = File(dir, fileName)
                            FileOutputStream(file).use { out ->
                                b.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                            status = "${s.qrSavedNote}: ${file.name}"
                        }
                    } catch (e: Exception) {
                        status = "${s.errorPrefix}: ${e.message}"
                    }
                }
            },
            enabled = bmp != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GlowViolet)
        ) {
            Text(s.qrExportPng, color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (status.isNotEmpty()) {
            Text(status, color = GlowEmerald, fontWeight = FontWeight.SemiBold)
        }
        Text(s.qrLocalNote, style = MaterialTheme.typography.bodySmall)
    }
}
