package com.becetti.tools.features.developer

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.ToolAccents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private fun hex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

/* ============================================ Base64 ======================================== */

@Composable
fun Base64Screen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Developer
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf("text") }
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            running = true
            error = null
            val bytes = withContext(Dispatchers.IO) { FileExporter.readBytes(ctx, uri) }
            running = false
            if (bytes == null) {
                error = s.gError
            } else if (bytes.size > 3 * 1024 * 1024) {
                error = s.devFileTooLarge
            } else {
                val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
                input = FileExporter.displayName(ctx, uri)
                output = encoded
                status = "${bytes.size} B → ${encoded.length} chars"
            }
        }
    }

    ToolPage(
        title = s.toolName("dev_base64"),
        subtitle = s.toolDescription("dev_base64"),
        icon = BecettiGlyphs.CopyStack,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            ChipSelector(
                options = listOf("text" to s.devBase64Text, "file" to s.devBase64File),
                selected = mode,
                onSelect = { mode = it; output = ""; error = null; status = "" },
                accent = accent
            )
            if (mode == "text") {
                BecettiField(
                    value = input,
                    onValueChange = { input = it; error = null },
                    label = s.gInput,
                    placeholder = "BECETTI TOOLS",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 10,
                    accent = accent
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        text = s.gEncode,
                        onClick = {
                            if (input.isEmpty()) error = s.gInvalidInput
                            else {
                                output = Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
                                status = ""
                            }
                        },
                        accent = accent,
                        modifier = Modifier.weight(1f),
                        fillWidth = false
                    )
                    ActionButton(
                        text = s.gDecode,
                        onClick = {
                            runCatching {
                                String(Base64.decode(input.trim(), Base64.DEFAULT), Charsets.UTF_8)
                            }.fold(
                                onSuccess = { output = it; status = "" },
                                onFailure = { output = ""; error = s.gInvalidInput }
                            )
                        },
                        accent = accent,
                        outlined = true,
                        modifier = Modifier.weight(1f),
                        fillWidth = false
                    )
                }
            } else {
                ActionButton(
                    text = s.gPickFile,
                    onClick = { filePicker.launch(arrayOf("*/*")) },
                    accent = accent,
                    icon = BecettiGlyphs.FolderOpen
                )
                Text(
                    if (input.isBlank()) s.gNoFileSelected else input,
                    style = MaterialTheme.typography.bodySmall,
                    color = com.becetti.tools.design.theme.BecettiTheme.palette.textSecondary
                )
                ActionButton(
                    text = s.devDecodeToFile,
                    onClick = {
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                runCatching {
                                    val bytes = Base64.decode(output.trim(), Base64.DEFAULT)
                                    val name = "becetti_decoded_${System.currentTimeMillis()}.bin"
                                    FileExporter.saveBytes(ctx, name, "application/octet-stream", bytes) to bytes.size
                                }
                            }
                            result.fold(
                                onSuccess = { (path, size) -> status = "${s.savedPathPrefix} $path (${FileExporter.humanSize(size.toLong())})" },
                                onFailure = { error = it.message ?: s.gError }
                            )
                        }
                    },
                    accent = ToolAccents.Files,
                    outlined = true,
                    enabled = output.isNotBlank(),
                    icon = BecettiGlyphs.ArchiveZip
                )
            }
        }

        OutputBlock(
            text = output,
            accent = accent,
            title = s.gOutput,
            error = error,
            running = running,
            runningLabel = s.gProcessing,
            errorLabel = s.errorPrefix,
            onCopy = { clipboard.setText(AnnotatedString(output)); status = s.gCopiedToClipboard },
            onSave = {
                scope.launch {
                    val path = withContext(Dispatchers.IO) {
                        runCatching { FileExporter.saveText(ctx, "becetti_base64_${System.currentTimeMillis()}.txt", "text/plain", output) }
                    }
                    status = path.fold({ "${s.savedPathPrefix} $it" }, { "${s.errorPrefix}: ${it.message}" })
                }
            },
            onShare = { FileExporter.shareText(ctx, output, "BECETTI Base64") },
            copyLabel = s.copy,
            saveLabel = s.save,
            shareLabel = s.share
        )
        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}

/* ============================================ Hashing ======================================= */

@Composable
fun HashScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Network
    val scope = rememberCoroutineScope()

    var algorithm by remember { mutableStateOf("SHA-256") }
    var input by remember { mutableStateOf("") }
    var hmacKey by remember { mutableStateOf("") }
    var digest by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            running = true
            error = null
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val bytes = FileExporter.readBytes(ctx, uri)
                        ?: throw IllegalStateException(s.gError)
                    val name = FileExporter.displayName(ctx, uri)
                    val hexDigest = if (hmacKey.isNotBlank()) hmac(algorithm, hmacKey, bytes) else digestOf(algorithm, bytes)
                    Triple(name, hexDigest, bytes.size)
                }
            }
            running = false
            result.fold(
                onSuccess = { (name, value, size) ->
                    fileName = name
                    digest = value
                    status = "$name · ${FileExporter.humanSize(size.toLong())}"
                },
                onFailure = { error = it.message ?: s.gError }
            )
        }
    }

    ToolPage(
        title = s.toolName("dev_hash"),
        subtitle = s.toolDescription("dev_hash"),
        icon = BecettiGlyphs.Hash,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            ChipSelector(
                options = listOf("MD5", "SHA-1", "SHA-256", "SHA-512"),
                selected = algorithm,
                onSelect = { algorithm = it },
                accent = accent
            )
            BecettiField(
                value = input,
                onValueChange = { input = it; error = null },
                label = s.gInput,
                placeholder = "BECETTI TOOLS",
                singleLine = false,
                minLines = 3,
                maxLines = 8,
                accent = accent
            )
            BecettiField(
                value = hmacKey,
                onValueChange = { hmacKey = it },
                label = s.devHmacKey,
                placeholder = "••••••",
                supporting = s.devHmacHint,
                accent = accent
            )
            ActionButton(
                text = "${s.gCalculate} ($algorithm)",
                onClick = {
                    if (input.isEmpty()) {
                        error = s.gInvalidInput
                    } else {
                        runCatching {
                            if (hmacKey.isNotBlank()) hmac(algorithm, hmacKey, input.toByteArray())
                            else digestOf(algorithm, input.toByteArray())
                        }.fold(
                            onSuccess = { digest = it; error = null; status = s.gReady },
                            onFailure = { error = it.message; digest = "" }
                        )
                    }
                },
                accent = accent
            )
            ActionButton(
                text = s.devHashFile,
                onClick = { filePicker.launch(arrayOf("*/*")) },
                accent = accent,
                outlined = true,
                icon = BecettiGlyphs.FolderOpen,
                loading = running
            )
            if (fileName.isNotBlank()) {
                Text(fileName, style = MaterialTheme.typography.bodySmall, color = com.becetti.tools.design.theme.BecettiTheme.palette.textSecondary)
            }
        }

        OutputBlock(
            text = digest,
            accent = accent,
            title = "${algorithm}${if (hmacKey.isNotBlank()) " · HMAC" else ""}",
            error = error,
            running = running,
            runningLabel = s.gProcessing,
            errorLabel = s.errorPrefix,
            onCopy = { clipboard.setText(AnnotatedString(digest)); status = s.gCopiedToClipboard },
            onShare = { FileExporter.shareText(ctx, digest, "BECETTI Hash") },
            copyLabel = s.copy,
            shareLabel = s.share
        )
        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}

private fun digestOf(algorithm: String, bytes: ByteArray): String =
    hex(MessageDigest.getInstance(algorithm).digest(bytes))

private fun hmac(algorithm: String, key: String, bytes: ByteArray): String {
    val macAlgorithm = when (algorithm) {
        "MD5" -> "HmacMD5"
        "SHA-1" -> "HmacSHA1"
        "SHA-512" -> "HmacSHA512"
        else -> "HmacSHA256"
    }
    val mac = Mac.getInstance(macAlgorithm)
    mac.init(SecretKeySpec(key.toByteArray(), macAlgorithm))
    return hex(mac.doFinal(bytes))
}

/* ============================================== UUID ======================================== */

@Composable
fun UuidScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Projects

    var count by remember { mutableStateOf("5") }
    var uppercase by remember { mutableStateOf(false) }
    var uuids by remember { mutableStateOf(listOf(UUID.randomUUID().toString())) }
    var checkValue by remember { mutableStateOf("") }
    var checkResult by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    ToolPage(
        title = s.toolName("dev_uuid"),
        subtitle = s.toolDescription("dev_uuid"),
        icon = BecettiGlyphs.IdCard,
        accent = accent,
        hint = s.devUuidHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = count,
                onValueChange = { count = it.filter { ch -> ch.isDigit() }.take(3) },
                label = s.devUuidCount,
                keyboardType = KeyboardType.Number,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = s.gGenerate,
                    onClick = {
                        val n = count.toIntOrNull() ?: 0
                        if (n !in 1..100) {
                            error = s.gInvalidInput
                        } else {
                            error = null
                            uuids = List(n) { UUID.randomUUID().toString() }
                        }
                    },
                    accent = accent,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
                ActionButton(
                    text = s.devUuidUpper,
                    onClick = { uppercase = !uppercase },
                    accent = accent,
                    outlined = !uppercase,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
            }
            val text = uuids.joinToString("\n") { if (uppercase) it.uppercase() else it }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconAction(BecettiGlyphs.CopyStack, s.copy, {
                    clipboard.setText(AnnotatedString(text))
                }, accent)
                IconAction(BecettiGlyphs.SwapArrows, s.share, {
                    FileExporter.shareText(ctx, text, "UUID")
                }, accent)
            }
        }

        OutputBlock(text = uuids.joinToString("\n") { if (uppercase) it.uppercase() else it }, accent = accent, title = s.gResult, error = error, errorLabel = s.errorPrefix)

        Panel(accent = ToolAccents.Files) {
            SectionHeader(s.gValidate)
            BecettiField(
                value = checkValue,
                onValueChange = { checkValue = it; checkResult = null },
                label = "UUID",
                placeholder = "550e8400-e29b-41d4-a716-446655440000",
                accent = ToolAccents.Files
            )
            ActionButton(
                text = s.gValidate,
                onClick = {
                    checkResult = runCatching {
                        UUID.fromString(checkValue.trim())
                        val version = if (checkValue.trim().length > 14) checkValue.trim()[14] else '?'
                        "✓ Valid UUID · v$version"
                    }.getOrElse { "✗ ${s.gInvalidInput}" }
                },
                accent = ToolAccents.Files,
                outlined = true
            )
            if (checkResult != null) {
                Text(
                    checkResult!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (checkResult!!.startsWith("✓")) com.becetti.tools.design.theme.BecettiTheme.palette.success
                    else com.becetti.tools.design.theme.BecettiTheme.palette.error
                )
            }
        }
    }
}

/* ============================================ URL codec ===================================== */

@Composable
fun UrlCodecScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Files

    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var parsed by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }

    ToolPage(
        title = s.toolName("dev_url"),
        subtitle = s.toolDescription("dev_url"),
        icon = BecettiGlyphs.LinkChain,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = input,
                onValueChange = { input = it; error = null },
                label = s.gInput,
                placeholder = "https://becetti.tools/search?q=dev tools&lang=ar",
                singleLine = false,
                minLines = 3,
                maxLines = 8,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = s.gEncode,
                    onClick = {
                        runCatching { URLEncoder.encode(input, "UTF-8") }.fold(
                            onSuccess = { output = it; error = null },
                            onFailure = { error = it.message }
                        )
                    },
                    accent = accent,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
                ActionButton(
                    text = s.gDecode,
                    onClick = {
                        runCatching { URLDecoder.decode(input, "UTF-8") }.fold(
                            onSuccess = { output = it; error = null },
                            onFailure = { error = s.gInvalidInput }
                        )
                    },
                    accent = accent,
                    outlined = true,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
            }
            ActionButton(
                text = s.devParseUrl,
                onClick = {
                    runCatching {
                        val uri = URI(input.trim())
                        val rows = mutableListOf<Pair<String, String>>()
                        uri.scheme?.let { rows.add("scheme" to it) }
                        uri.host?.let { rows.add("host" to it) }
                        if (uri.port != -1) rows.add("port" to uri.port.toString())
                        uri.path?.takeIf { it.isNotBlank() }?.let { rows.add("path" to it) }
                        uri.query?.split('&')?.filter { it.isNotBlank() }?.forEach { pair ->
                            val key = pair.substringBefore('=')
                            val value = if (pair.contains('=')) pair.substringAfter('=') else ""
                            rows.add(URLDecoder.decode(key, "UTF-8") to URLDecoder.decode(value, "UTF-8"))
                        }
                        uri.fragment?.let { rows.add("fragment" to it) }
                        if (rows.isEmpty()) throw IllegalArgumentException(s.gInvalidInput)
                        rows
                    }.fold(
                        onSuccess = { parsed = it; error = null; status = s.gReady },
                        onFailure = { parsed = emptyList(); error = s.devInvalidUrl }
                    )
                },
                accent = ToolAccents.Network,
                outlined = true,
                icon = BecettiGlyphs.Globe
            )
        }

        OutputBlock(
            text = output,
            accent = accent,
            title = s.gOutput,
            error = error,
            errorLabel = s.errorPrefix,
            onCopy = { clipboard.setText(AnnotatedString(output)); status = s.gCopiedToClipboard },
            onShare = { FileExporter.shareText(ctx, output, "URL") },
            copyLabel = s.copy,
            shareLabel = s.share
        )

        if (parsed.isNotEmpty()) {
            Panel(accent = ToolAccents.Network) {
                SectionHeader(s.devQueryParams)
                parsed.forEach { (key, value) -> KeyValueRow(key, value.ifBlank { "—" }, mono = true) }
            }
        }
        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}
