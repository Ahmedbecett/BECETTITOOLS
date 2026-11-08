package com.becetti.tools.features.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.MonoTextStyle
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.features.tools.FilterPill
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/* ========================================== Regex tester ==================================== */

@Composable
fun RegexTestScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Projects

    var pattern by remember { mutableStateOf("") }
    var flags by remember { mutableStateOf(setOf<String>()) }
    var testText by remember { mutableStateOf("") }
    var replacement by remember { mutableStateOf("") }
    var matches by remember { mutableStateOf<List<RegexMatch>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var replaced by remember { mutableStateOf("") }

    fun evaluate() {
        error = null
        replaced = ""
        if (pattern.isEmpty()) {
            matches = emptyList()
            return
        }
        runCatching {
            var flagsValue = 0
            if ("i" in flags) flagsValue = flagsValue or Pattern.CASE_INSENSITIVE
            if ("m" in flags) flagsValue = flagsValue or Pattern.MULTILINE
            if ("s" in flags) flagsValue = flagsValue or Pattern.DOTALL
            val compiled = Pattern.compile(pattern, flagsValue)
            val matcher = compiled.matcher(testText)
            val found = mutableListOf<RegexMatch>()
            var guard = 0
            while (guard < 500 && matcher.find()) {
                val groups = (0..matcher.groupCount()).map { index ->
                    val value = runCatching { matcher.group(index) }.getOrNull()
                    "$index: ${value ?: "null"}"
                }
                found.add(RegexMatch(matcher.start(), matcher.end(), matcher.group(), groups))
                guard++
            }
            matches = found
        }.onFailure { e ->
            matches = emptyList()
            error = if (e is PatternSyntaxException) e.description + " @ " + e.index else e.message
        }
    }

    ToolPage(
        title = s.toolName("dev_regex"),
        subtitle = s.toolDescription("dev_regex"),
        icon = BecettiGlyphs.RegexStar,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = pattern,
                onValueChange = { pattern = it; evaluate() },
                label = s.devRegexPattern,
                placeholder = "\\b(\\w+)@(\\w+\\.\\w+)\\b",
                accent = accent,
                isError = error != null,
                supporting = error
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("i" to "Aa", "m" to "^$", "s" to ".\\n").forEach { (flag, label) ->
                    FilterPill(label = label, selected = flag in flags, accent = accent) {
                        flags = if (flag in flags) flags - flag else flags + flag
                        evaluate()
                    }
                }
            }
            BecettiField(
                value = testText,
                onValueChange = { testText = it; evaluate() },
                label = s.devRegexTestText,
                placeholder = "contact@becetti.tools, dev@becetti.tools",
                singleLine = false,
                minLines = 4,
                maxLines = 10,
                accent = accent
            )
        }

        Panel(accent = ToolAccents.Business) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(s.devMatches, style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                Text("${matches.size}", style = MaterialTheme.typography.titleSmall, color = ToolAccents.Business)
            }
            if (matches.isEmpty()) {
                Text(
                    if (pattern.isEmpty()) s.devRegexPattern else s.devNoMatches,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            } else {
                matches.take(50).forEachIndexed { index, match ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.chipSurface)
                            .padding(9.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text("#${index + 1}  [${match.start}–${match.end}]", style = MaterialTheme.typography.labelSmall, color = ToolAccents.Business)
                        Text(match.value, style = MonoTextStyle, color = palette.textPrimary)
                        if (match.groups.size > 1) {
                            Text(match.groups.drop(1).joinToString("  "), style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                        }
                    }
                }
            }
        }

        Panel(accent = ToolAccents.Files) {
            BecettiField(
                value = replacement,
                onValueChange = { replacement = it },
                label = s.devRegexReplace,
                placeholder = "[email]",
                supporting = s.devReplaceHint,
                accent = ToolAccents.Files
            )
            ActionButton(
                text = s.devReplaceAll,
                onClick = {
                    if (pattern.isNotEmpty()) {
                        replaced = runCatching { testText.replace(Regex(pattern), replacement) }
                            .getOrElse { error = it.message; "" }
                    }
                },
                accent = ToolAccents.Files,
                outlined = true,
                enabled = pattern.isNotEmpty() && testText.isNotEmpty()
            )
        }

        if (replaced.isNotEmpty()) {
            ResultPanel(
                text = replaced,
                accent = ToolAccents.Files,
                title = s.gOutput,
                mono = false,
                onCopy = { clipboard.setText(AnnotatedString(replaced)) },
                onShare = { FileExporter.shareText(ctx, replaced, "Regex") },
                copyLabel = s.copy,
                shareLabel = s.share
            )
        }
    }
}

private data class RegexMatch(val start: Int, val end: Int, val value: String, val groups: List<String>)

/* ========================================== Diff viewer ===================================== */

private data class DiffLine(val kind: Char, val text: String, val leftNo: Int?, val rightNo: Int?)

@Composable
fun DiffScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Media

    var left by remember { mutableStateOf("") }
    var right by remember { mutableStateOf("") }
    var lines by remember { mutableStateOf<List<DiffLine>>(emptyList()) }
    var added by remember { mutableIntStateOf(0) }
    var removed by remember { mutableIntStateOf(0) }
    var identical by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun compute() {
        error = null
        identical = false
        if (left.isBlank() && right.isBlank()) {
            lines = emptyList()
            return
        }
        runCatching { diffLines(left, right) }.fold(
            onSuccess = { result ->
                lines = result
                added = result.count { it.kind == '+' }
                removed = result.count { it.kind == '-' }
                identical = added == 0 && removed == 0
            },
            onFailure = { error = it.message; lines = emptyList() }
        )
    }

    ToolPage(
        title = s.toolName("dev_diff"),
        subtitle = s.toolDescription("dev_diff"),
        icon = BecettiGlyphs.SplitCompare,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = left,
                onValueChange = { left = it },
                label = s.devOriginal,
                singleLine = false,
                minLines = 4,
                maxLines = 10,
                accent = accent
            )
            BecettiField(
                value = right,
                onValueChange = { right = it },
                label = s.devModified,
                singleLine = false,
                minLines = 4,
                maxLines = 10,
                accent = accent
            )
            ActionButton(
                text = s.devCompare,
                onClick = { compute() },
                accent = accent,
                enabled = left.isNotEmpty() || right.isNotEmpty()
            )
        }

        error?.let {
            OutputBlock(text = "", accent = accent, error = it, errorLabel = s.errorPrefix)
        }

        if (identical) {
            SuccessTick(visible = true, message = s.devIdentical)
        }

        if (lines.isNotEmpty() && !identical) {
            Panel(accent = accent) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("+$added ${s.devAdded}", style = MaterialTheme.typography.labelMedium, color = palette.success)
                    Text("−$removed ${s.devRemoved}", style = MaterialTheme.typography.labelMedium, color = palette.error)
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(lines) { line ->
                            val color = when (line.kind) {
                                '+' -> palette.success
                                '-' -> palette.error
                                else -> palette.textSecondary
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(color.copy(alpha = 0.08f))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "${line.kind} ",
                                    style = MonoTextStyle,
                                    color = color
                                )
                                Text(line.text.ifBlank { " " }, style = MonoTextStyle, color = palette.textPrimary)
                            }
                        }
                    }
                }
                ActionButton(
                    text = s.copy,
                    onClick = {
                        val unified = lines.joinToString("\n") { "${it.kind}${it.text}" }
                        clipboard.setText(AnnotatedString(unified))
                    },
                    accent = accent,
                    outlined = true,
                    icon = BecettiGlyphs.CopyStack
                )
                ActionButton(
                    text = s.share,
                    onClick = {
                        val unified = lines.joinToString("\n") { "${it.kind}${it.text}" }
                        FileExporter.shareText(ctx, unified, "Diff")
                    },
                    accent = accent,
                    outlined = true,
                    icon = BecettiGlyphs.SwapArrows
                )
            }
        }
    }
}

/**
 * Longest-common-subsequence line diff. Capped at 1200 lines per side so a pathological paste can
 * never exhaust memory on a low-RAM device.
 */
private fun diffLines(left: String, right: String): List<DiffLine> {
    val a = left.lines().take(1200)
    val b = right.lines().take(1200)
    val n = a.size
    val m = b.size
    val lcs = Array(n + 1) { IntArray(m + 1) }
    for (i in n - 1 downTo 0) {
        for (j in m - 1 downTo 0) {
            lcs[i][j] = if (a[i] == b[j]) lcs[i + 1][j + 1] + 1 else maxOf(lcs[i + 1][j], lcs[i][j + 1])
        }
    }
    val out = mutableListOf<DiffLine>()
    var i = 0
    var j = 0
    while (i < n && j < m) {
        if (a[i] == b[j]) {
            out.add(DiffLine(' ', a[i], i + 1, j + 1)); i++; j++
        } else if (lcs[i + 1][j] >= lcs[i][j + 1]) {
            out.add(DiffLine('-', a[i], i + 1, null)); i++
        } else {
            out.add(DiffLine('+', b[j], null, j + 1)); j++
        }
    }
    while (i < n) { out.add(DiffLine('-', a[i], i + 1, null)); i++ }
    while (j < m) { out.add(DiffLine('+', b[j], null, j + 1)); j++ }
    return out
}
