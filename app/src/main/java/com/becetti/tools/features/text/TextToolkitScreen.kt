package com.becetti.tools.features.text

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*

@Composable
fun TextToolkitScreen() {
    val s = LocalAppStrings.current
    var input by remember { mutableStateOf("") }
    var copied by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current

    val words = remember(input) {
        if (input.isBlank()) 0 else input.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    }
    val chars = input.length
    val charsNoSpaces = input.replace(" ", "").replace("\n", "").length
    val lines = remember(input) { if (input.isEmpty()) 0 else input.lines().size }

    val toolMeta = s.tools["text_toolkit"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "text_toolkit",
            title = toolMeta?.name ?: "Text Toolkit",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowBlue) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it; copied = false },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                placeholder = { Text(s.txtInputHint) }
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("$words", style = MaterialTheme.typography.titleLarge, color = GlowBlue, fontWeight = FontWeight.Bold)
                    Text(s.txtWords, style = MaterialTheme.typography.labelSmall)
                }
                Column {
                    Text("$chars", style = MaterialTheme.typography.titleLarge, color = GlowCyan, fontWeight = FontWeight.Bold)
                    Text(s.txtChars, style = MaterialTheme.typography.labelSmall)
                }
                Column {
                    Text("$charsNoSpaces", style = MaterialTheme.typography.titleLarge, color = GlowEmerald, fontWeight = FontWeight.Bold)
                    Text(s.txtNoSpaces, style = MaterialTheme.typography.labelSmall)
                }
                Column {
                    Text("$lines", style = MaterialTheme.typography.titleLarge, color = GlowOrange, fontWeight = FontWeight.Bold)
                    Text(s.txtLines, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        GlowingCard(accentColor = GlowViolet) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { input = input.uppercase() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                    Text(s.txtUpper)
                }
                Button(onClick = { input = input.lowercase() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                    Text(s.txtLower)
                }
                Button(
                    onClick = {
                        input = input.split(" ").joinToString(" ") { w ->
                            w.lowercase().replaceFirstChar { it.uppercase() }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtTitleCase)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { input = input.replace(Regex(" +"), " ").trim() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtDedupSpaces)
                }
                OutlinedButton(
                    onClick = { input = input.lines().filter { it.isNotBlank() }.joinToString("\n") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtRemoveEmptyLines)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { input = input.lines().sorted().joinToString("\n") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtSortLines)
                }
                OutlinedButton(
                    onClick = { input = input.reversed() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtReverse)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        runCatching {
                            input = Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtBase64Encode)
                }
                OutlinedButton(
                    onClick = {
                        runCatching {
                            input = String(Base64.decode(input.trim(), Base64.DEFAULT), Charsets.UTF_8)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(s.txtBase64Decode)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(input))
                    copied = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
            ) {
                Text(if (copied) s.copied else s.copy, color = Color.White, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { input = ""; copied = false },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(s.clear)
            }
        }
    }
}
