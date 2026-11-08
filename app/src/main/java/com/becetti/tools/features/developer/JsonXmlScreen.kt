package com.becetti.tools.features.developer

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.domain.models.ToolGroup
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * JSON & XML studio: pretty-print, minify, validate and convert.
 * All parsing happens locally (Gson + a deterministic XML pretty-printer).
 */
@Composable
fun JsonXmlScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Developer
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf("json") }
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }

    fun run(action: (String) -> String) {
        if (input.isBlank()) {
            error = s.gInvalidInput
            output = ""
            return
        }
        scope.launch {
            running = true
            error = null
            status = ""
            val result = withContext(Dispatchers.Default) {
                runCatching { action(input) }
            }
            running = false
            result.fold(
                onSuccess = { output = it; status = s.gValidate + " ✓" },
                onFailure = { output = ""; error = it.message ?: it::class.java.simpleName }
            )
        }
    }

    ToolPage(
        title = s.toolName("dev_json"),
        subtitle = s.toolDescription("dev_json"),
        icon = BecettiGlyphs.Braces,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            ChipSelector(
                options = listOf("json" to "JSON", "xml" to "XML"),
                selected = mode,
                onSelect = { mode = it; output = ""; error = null },
                accent = accent
            )
            BecettiField(
                value = input,
                onValueChange = { input = it; error = null },
                label = s.gInput,
                placeholder = if (mode == "json") "{\"tool\":\"BECETTI\"}" else "<root><tool/></root>",
                singleLine = false,
                minLines = 6,
                maxLines = 14,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = s.gFormat,
                    onClick = {
                        run { source ->
                            if (mode == "json") formatJson(source) else formatXml(source)
                        }
                    },
                    accent = accent,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
                ActionButton(
                    text = s.gMinify,
                    onClick = {
                        run { source ->
                            if (mode == "json") minifyJson(source) else minifyXml(source)
                        }
                    },
                    accent = accent,
                    outlined = true,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = s.gValidate,
                    onClick = {
                        run { source ->
                            if (mode == "json") {
                                parseJson(source)
                                "✓ Valid JSON"
                            } else {
                                parseXmlSafe(source)
                                "✓ Valid XML"
                            }
                        }
                    },
                    accent = ToolAccents.Business,
                    outlined = true,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
                ActionButton(
                    text = s.devToXml,
                    onClick = {
                        if (mode != "json") {
                            error = s.devJsonOnly
                            return@ActionButton
                        }
                        run { source -> jsonToXml(source) }
                    },
                    accent = ToolAccents.Files,
                    outlined = true,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
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
            onCopy = {
                clipboard.setText(AnnotatedString(output))
                status = s.gCopiedToClipboard
            },
            onSave = {
                scope.launch {
                    val path = withContext(Dispatchers.IO) {
                        runCatching {
                            FileExporter.saveText(
                                ctx,
                                "becetti_${System.currentTimeMillis()}.${if (mode == "json") "json" else "xml"}",
                                if (mode == "json") "application/json" else "application/xml",
                                output
                            )
                        }
                    }
                    status = path.fold({ "${s.savedPathPrefix} $it" }, { "${s.errorPrefix}: ${it.message}" })
                }
            },
            onShare = { FileExporter.shareText(ctx, output, "BECETTI TOOLS") },
            copyLabel = s.copy,
            saveLabel = s.save,
            shareLabel = s.share
        )

        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}

/* ----------------------------------------- engines ----------------------------------------- */

private val prettyGson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

/** Gson 2.8-compatible parser entry point (works on every version shipped with the app). */
@Suppress("DEPRECATION")
private fun parseJson(source: String): com.google.gson.JsonElement = JsonParser().parse(source)

private fun formatJson(source: String): String {
    val element = try {
        parseJson(source)
    } catch (e: JsonSyntaxException) {
        throw IllegalArgumentException("Invalid JSON: ${e.message?.lineSequence()?.firstOrNull() ?: "syntax error"}")
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid JSON: ${e.message}")
    }
    return prettyGson.toJson(element)
}

private fun minifyJson(source: String): String {
    val element = parseJson(source)
    return element.toString()
}

private fun jsonToXml(source: String): String {
    val element = parseJson(source)
    val builder = StringBuilder()
    fun emit(name: String, value: com.google.gson.JsonElement, depth: Int) {
        val indent = "  ".repeat(depth)
        when {
            value.isJsonObject -> {
                builder.append("$indent<$name>\n")
                value.asJsonObject.entrySet().forEach { (k, v) -> emit(sanitizeTag(k), v, depth + 1) }
                builder.append("$indent</$name>\n")
            }
            value.isJsonArray -> {
                if (value.asJsonArray.size() == 0) {
                    builder.append("$indent<$name/>\n")
                } else {
                    builder.append("$indent<$name>\n")
                    value.asJsonArray.forEach { item -> emit("item", item, depth + 1) }
                    builder.append("$indent</$name>\n")
                }
            }
            value.isJsonNull -> builder.append("$indent<$name/>\n")
            else -> builder.append("$indent<$name>${escapeXml(value.asString)}</$name>\n")
        }
    }
    emit("root", element, 0)
    return builder.toString()
}

private fun sanitizeTag(raw: String): String {
    val cleaned = raw.replace(Regex("[^A-Za-z0-9_.-]"), "_")
    return if (cleaned.isEmpty() || cleaned.first().isDigit()) "_$cleaned" else cleaned
}

private fun escapeXml(text: String): String = text
    .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    .replace("\"", "&quot;").replace("'", "&apos;")

private fun parseXmlSafe(source: String) {
    val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
    runCatching { factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
    runCatching { factory.isExpandEntityReferences = false }
    val builder = factory.newDocumentBuilder()
    builder.setErrorHandler(object : org.xml.sax.ErrorHandler {
        override fun warning(exception: org.xml.sax.SAXParseException) {}
        override fun error(exception: org.xml.sax.SAXParseException) = throw exception
        override fun fatalError(exception: org.xml.sax.SAXParseException) = throw exception
    })
    builder.parse(source.byteInputStream())
}

/**
 * Deterministic XML pretty printer (2-space indent, preserves attributes and text nodes).
 * Written by hand so output is identical across Android versions.
 */
private fun formatXml(source: String): String {
    parseXmlSafe(source)
    val cleaned = source.replace(Regex(">\\s*<"), "><").trim()
    val tokens = Regex("<[^>]+>|[^<]+").findAll(cleaned).map { it.value }.toList()
    val builder = StringBuilder()
    var depth = 0
    tokens.forEach { token ->
        val trimmed = token.trim()
        if (trimmed.isEmpty()) return@forEach
        when {
            trimmed.startsWith("</") -> {
                depth = (depth - 1).coerceAtLeast(0)
                builder.append("  ".repeat(depth)).append(trimmed).append('\n')
            }
            trimmed.startsWith("<?") || trimmed.startsWith("<!") -> {
                builder.append("  ".repeat(depth)).append(trimmed).append('\n')
            }
            trimmed.startsWith("<") && trimmed.endsWith("/>") -> {
                builder.append("  ".repeat(depth)).append(trimmed).append('\n')
            }
            trimmed.startsWith("<") -> {
                builder.append("  ".repeat(depth)).append(trimmed).append('\n')
                depth++
            }
            else -> {
                builder.append("  ".repeat(depth)).append(trimmed).append('\n')
            }
        }
    }
    return builder.toString().trimEnd()
}

private fun minifyXml(source: String): String {
    parseXmlSafe(source)
    return source.replace(Regex(">\\s+<"), "><").trim()
}
