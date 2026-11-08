package com.becetti.tools.features.developer

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
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.features.tools.FilterPill
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

private val sharedClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
}

@Suppress("DEPRECATION")
private fun parseJsonElement(text: String): JsonElement = JsonParser().parse(text)

/* ========================================== REST client ===================================== */

private data class ApiResult(
    val status: Int,
    val message: String,
    val latencyMs: Long,
    val body: String,
    val headers: List<Pair<String, String>>,
    val bytes: Long
)

@Composable
fun ApiClientScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val accent = ToolAccents.Developer
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette

    var url by remember { mutableStateOf("https://api.github.com/repos/Ahmedbecett/BECETTITOOLS") }
    var method by remember { mutableStateOf("GET") }
    var headersText by remember { mutableStateOf("Accept: application/json") }
    var bodyText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<ApiResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var running by remember { mutableStateOf(false) }
    var showHeaders by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }

    fun send() {
        val target = url.trim()
        if (!target.startsWith("http://") && !target.startsWith("https://")) {
            error = s.devInvalidUrl
            return
        }
        scope.launch {
            running = true
            error = null
            result = null
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val builder = Request.Builder().url(target)
                    headersText.lines()
                        .map { it.trim() }
                        .filter { it.contains(":") }
                        .forEach { line ->
                            val key = line.substringBefore(":").trim()
                            val value = line.substringAfter(":").trim()
                            if (key.isNotEmpty()) builder.addHeader(key, value)
                        }
                    val contentType = "application/json; charset=utf-8".toMediaTypeOrNull()
                    val requestBody = if (method in listOf("POST", "PUT", "PATCH")) bodyText.toRequestBody(contentType) else null
                    builder.method(method, requestBody)
                    val started = System.currentTimeMillis()
                    sharedClient.newCall(builder.build()).execute().use { response ->
                        val raw = response.body?.bytes() ?: ByteArray(0)
                        val text = String(raw, Charsets.UTF_8)
                        val pretty = runCatching { prettyPrintJson(text) }.getOrDefault(text)
                        ApiResult(
                            status = response.code,
                            message = response.message,
                            latencyMs = System.currentTimeMillis() - started,
                            body = pretty.take(200_000),
                            headers = response.headers.names().sorted().map { it to (response.header(it) ?: "") },
                            bytes = raw.size.toLong()
                        )
                    }
                }
            }
            running = false
            outcome.fold(
                onSuccess = { result = it },
                onFailure = { error = it.message ?: it::class.java.simpleName }
            )
        }
    }

    ToolPage(
        title = s.toolName("dev_api"),
        subtitle = s.toolDescription("dev_api"),
        icon = BecettiGlyphs.CloudCode,
        accent = accent,
        hint = s.devApiHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = url,
                onValueChange = { url = it; error = null },
                label = s.devUrl,
                singleLine = false,
                minLines = 1,
                maxLines = 3,
                keyboardType = KeyboardType.Uri,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("GET", "POST", "PUT", "PATCH", "DELETE").forEach { verb ->
                    FilterPill(label = verb, selected = method == verb, accent = accent) { method = verb }
                }
            }
            BecettiField(
                value = headersText,
                onValueChange = { headersText = it },
                label = s.devHeaders,
                supporting = s.devHeadersHint,
                singleLine = false,
                minLines = 2,
                maxLines = 6,
                accent = accent
            )
            if (method in listOf("POST", "PUT", "PATCH")) {
                BecettiField(
                    value = bodyText,
                    onValueChange = { bodyText = it },
                    label = s.devBody,
                    placeholder = "{\"key\":\"value\"}",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 10,
                    accent = accent
                )
            }
            ActionButton(
                text = s.devSend,
                onClick = { send() },
                accent = accent,
                icon = BecettiGlyphs.CloudCode,
                loading = running
            )
        }

        if (running) LoadingBlock(text = s.gProcessing, accent = accent)
        error?.let {
            OutputBlock(text = "", accent = accent, error = it, errorLabel = s.errorPrefix)
        }

        result?.let { res ->
            Panel(accent = if (res.status in 200..299) palette.success else palette.error) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatTile(
                        s.gStatus,
                        "${res.status}",
                        if (res.status in 200..299) palette.success else palette.error,
                        Modifier.weight(1f)
                    )
                    StatTile(s.devLatency, "${res.latencyMs} ms", ToolAccents.Files, Modifier.weight(1f))
                    StatTile(s.gSize, FileExporter.humanSize(res.bytes), ToolAccents.Media, Modifier.weight(1f))
                }
                Text(res.message, style = MaterialTheme.typography.labelMedium, color = palette.textSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterPill(s.devResponseBody, !showHeaders, accent) { showHeaders = false }
                    FilterPill("${s.devResponseHeaders} (${res.headers.size})", showHeaders, ToolAccents.Network) { showHeaders = true }
                }
                if (!showHeaders) {
                    ResultPanel(
                        text = res.body,
                        accent = accent,
                        title = s.devResponseBody,
                        mono = true,
                        maxHeight = 380.dp,
                        onCopy = { clipboard.setText(AnnotatedString(res.body)); status = s.gCopiedToClipboard },
                        onSave = {
                            scope.launch {
                                val path = withContext(Dispatchers.IO) {
                                    runCatching {
                                        FileExporter.saveText(
                                            ctx,
                                            "becetti_response_${System.currentTimeMillis()}.txt",
                                            "text/plain",
                                            res.body
                                        )
                                    }
                                }
                                status = path.fold({ "${s.savedPathPrefix} $it" }, { "${s.errorPrefix}: ${it.message}" })
                            }
                        },
                        onShare = { FileExporter.shareText(ctx, res.body, "API response") },
                        copyLabel = s.copy,
                        saveLabel = s.save,
                        shareLabel = s.share
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        res.headers.forEach { (key, value) -> KeyValueRow(key, value, mono = true) }
                    }
                }
            }
        }
        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}

@Suppress("DEPRECATION")
private fun prettyPrintJson(raw: String): String {
    val element = JsonParser().parse(raw)
    return com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(element)
}

/* ======================================= GitHub utilities =================================== */

@Composable
fun GitHubScreen() {
    val s = LocalAppStrings.current
    val accent = ToolAccents.Network
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette

    var user by remember { mutableStateOf("") }
    var repo by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var releases by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var running by remember { mutableStateOf(false) }
    var avatarLoaded by remember { mutableStateOf(false) }

    fun lookup() {
        val name = user.trim()
        if (name.isEmpty()) {
            error = s.gInvalidInput
            return
        }
        scope.launch {
            running = true
            error = null
            rows = emptyList()
            releases = emptyList()
            avatarLoaded = false
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val out = mutableListOf<Pair<String, String>>()
                    val repoName = repo.trim()
                    val target = if (repoName.isEmpty()) "https://api.github.com/users/$name"
                    else "https://api.github.com/repos/$name/$repoName"
                    sharedClient.newCall(
                        Request.Builder().url(target)
                            .header("Accept", "application/vnd.github+json")
                            .header("User-Agent", "BecettiTools")
                            .build()
                    ).execute().use { response ->
                        val text = response.body?.string().orEmpty()
                        if (response.code == 403) throw IllegalStateException(s.devRateLimit)
                        if (response.code == 404) throw IllegalStateException("404 · not found")
                        if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}")
                        val json = parseJsonElement(text).asJsonObject
                        fun field(key: String): String? {
                            val value = json.get(key) ?: return null
                            if (value.isJsonNull || !value.isJsonPrimitive) return null
                            return value.asString
                        }
                        if (repoName.isEmpty()) {
                            field("login")?.let { out.add("Login" to it) }
                            field("name")?.let { out.add("Name" to it) }
                            field("bio")?.let { out.add("Bio" to it) }
                            field("company")?.let { out.add("Company" to it) }
                            field("location")?.let { out.add("Location" to it) }
                            field("blog")?.let { out.add("Website" to it) }
                            field("created_at")?.let { out.add("Joined" to it.take(10)) }
                            json.get("public_repos")?.let { out.add(s.devRepos to it.asString) }
                            json.get("followers")?.let { out.add("Followers" to it.asString) }
                            json.get("following")?.let { out.add("Following" to it.asString) }
                        } else {
                            field("full_name")?.let { out.add("Repository" to it) }
                            field("description")?.let { out.add(s.projDescription to it) }
                            field("language")?.let { out.add(s.devLanguage to it) }
                            json.get("stargazers_count")?.let { out.add(s.devStars to it.asString) }
                            json.get("forks_count")?.let { out.add(s.devForks to it.asString) }
                            json.get("open_issues_count")?.let { out.add(s.devOpenIssues to it.asString) }
                            field("updated_at")?.let { out.add(s.devUpdated to it.take(10)) }
                            runCatching { json.getAsJsonObject("license")?.get("name")?.asString }
                                ?.let { out.add("License" to it) }
                            field("default_branch")?.let { out.add("Branch" to it) }
                        }
                    }
                    if (repoName.isNotEmpty()) {
                        val relTarget = "https://api.github.com/repos/$name/$repoName/releases?per_page=5"
                        sharedClient.newCall(
                            Request.Builder().url(relTarget)
                                .header("Accept", "application/vnd.github+json")
                                .header("User-Agent", "BecettiTools")
                                .build()
                        ).execute().use { response ->
                            if (response.isSuccessful) {
                                val arr = parseJsonElement(response.body?.string().orEmpty())
                                if (arr.isJsonArray) {
                                    releases = arr.asJsonArray.mapNotNull { element ->
                                        runCatching {
                                            val obj = element.asJsonObject
                                            val tag = obj.get("tag_name")?.asString.orEmpty()
                                            val published = obj.get("published_at")?.asString?.take(10).orEmpty()
                                            "$tag · $published"
                                        }.getOrNull()
                                    }
                                }
                            }
                        }
                    }
                    out
                }
            }
            running = false
            outcome.fold(
                onSuccess = { rows = it; avatarLoaded = true },
                onFailure = { error = it.message ?: it::class.java.simpleName }
            )
        }
    }

    ToolPage(
        title = s.toolName("dev_github"),
        subtitle = s.toolDescription("dev_github"),
        icon = BecettiGlyphs.GithubMark,
        accent = accent,
        hint = s.devRateLimit
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = user,
                onValueChange = { user = it; error = null },
                label = s.devGitHubUser,
                placeholder = "Ahmedbecett",
                accent = accent
            )
            BecettiField(
                value = repo,
                onValueChange = { repo = it; error = null },
                label = s.devGitHubRepo,
                placeholder = "BECETTITOOLS",
                supporting = s.devRepoHint,
                accent = accent
            )
            ActionButton(
                text = s.devGitHubLookup,
                onClick = { lookup() },
                accent = accent,
                icon = BecettiGlyphs.GithubMark,
                loading = running,
                enabled = !running
            )
        }

        if (running) LoadingBlock(text = s.gLoading, accent = accent)
        error?.let { OutputBlock(text = "", accent = accent, error = it, errorLabel = s.errorPrefix) }

        if (rows.isNotEmpty()) {
            Panel(accent = accent) {
                SectionHeader(s.gDetails)
                rows.forEach { (key, value) -> KeyValueRow(key, value) }
                if (avatarLoaded) SuccessTick(visible = true, message = s.gReady, accent = palette.success)
            }
        }
        if (releases.isNotEmpty()) {
            Panel(accent = ToolAccents.Business) {
                SectionHeader(s.devReleases)
                releases.forEach { release ->
                    Text("• $release", style = MaterialTheme.typography.bodySmall, color = palette.textPrimary)
                }
            }
        }
    }
}
