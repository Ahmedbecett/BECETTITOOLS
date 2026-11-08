package com.becetti.tools.features.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

private val netClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
}

/* ============================================ DNS =========================================== */

@Composable
fun DnsLookupScreen() {
    val s = LocalAppStrings.current
    val accent = ToolAccents.Network
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette
    val clipboard = LocalClipboardManager.current

    var host by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("A") }
    var rows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var running by remember { mutableStateOf(false) }

    fun lookup() {
        val target = host.trim().removePrefix("https://").removePrefix("http://").substringBefore('/')
        if (target.isEmpty()) {
            error = s.gInvalidInput
            return
        }
        scope.launch {
            running = true
            error = null
            rows = emptyList()
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val out = mutableListOf<Pair<String, String>>()
                    val started = System.currentTimeMillis()
                    val url = "https://cloudflare-dns.com/dns-query?name=$target&type=$type"
                    sharedGet(url, "application/dns-json")?.let { payload ->
                        @Suppress("DEPRECATION")
                        val json = JsonParser().parse(payload).asJsonObject
                        val status = json.get("Status")?.asInt ?: -1
                        if (status != 0) throw IllegalStateException("DNS status $status")
                        out.add(s.netResolvedIn to "${System.currentTimeMillis() - started} ms")
                        json.getAsJsonArray("Answer")?.forEach { element ->
                            val obj = element.asJsonObject
                            val name = obj.get("name")?.asString.orEmpty()
                            val data = obj.get("data")?.asString.orEmpty()
                            val recordType = obj.get("type")?.asInt
                            out.add("${typeLabel(recordType)} · $name" to data)
                        }
                        if (out.size == 1) out.add(s.netNoRecords to target)
                    }
                    if (out.isEmpty()) {
                        val addresses = InetAddress.getAllByName(target)
                        addresses.forEach { address ->
                            out.add((if (address is java.net.Inet6Address) "AAAA" else "A") to address.hostAddress.orEmpty())
                        }
                        out.add(s.netResolvedIn to "${System.currentTimeMillis() - started} ms")
                    }
                    out
                }
            }
            running = false
            outcome.fold(onSuccess = { rows = it }, onFailure = { error = it.message ?: s.gError })
        }
    }

    ToolPage(
        title = s.toolName("net_dns"),
        subtitle = s.toolDescription("net_dns"),
        icon = BecettiGlyphs.NetworkNodes,
        accent = accent,
        hint = s.netDnsHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = host,
                onValueChange = { host = it; error = null },
                label = s.netHostname,
                placeholder = "github.com",
                keyboardType = KeyboardType.Uri,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("A", "AAAA", "CNAME", "MX", "TXT", "NS").forEach { record ->
                    FilterPill(record, type == record, accent) { type = record }
                }
            }
            ActionButton(
                text = s.netLookup,
                onClick = { lookup() },
                accent = accent,
                loading = running,
                icon = BecettiGlyphs.NetworkNodes
            )
        }

        if (running) LoadingBlock(s.gLoading, accent = accent)
        error?.let { OutputBlock(text = "", accent = accent, error = it, errorLabel = s.errorPrefix) }

        if (rows.isNotEmpty()) {
            Panel(accent = accent) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(s.gResult, style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                    IconAction(BecettiGlyphs.CopyStack, s.copy, {
                        clipboard.setText(AnnotatedString(rows.joinToString("\n") { "${it.first}: ${it.second}" }))
                    }, accent)
                }
                rows.forEach { (key, value) -> KeyValueRow(key, value, mono = true) }
            }
        }
    }
}

private fun typeLabel(type: Int?): String = when (type) {
    1 -> "A"
    2 -> "NS"
    5 -> "CNAME"
    6 -> "SOA"
    15 -> "MX"
    16 -> "TXT"
    28 -> "AAAA"
    else -> "REC"
}

private fun sharedGet(url: String, accept: String): String? = runCatching {
    netClient.newCall(
        Request.Builder().url(url).header("Accept", accept).header("User-Agent", "BecettiTools").build()
    ).execute().use { response ->
        if (!response.isSuccessful) null else response.body?.string()
    }
}.getOrNull()

/* ============================================ IP info ======================================= */

@Composable
fun IpInfoScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val accent = ToolAccents.Files
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette

    var localRows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var publicRows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            localRows = runCatching { localInterfaces() }.getOrDefault(emptyList())
        }
    }

    fun loadPublic(withGeo: Boolean) {
        scope.launch {
            running = true
            error = null
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val out = mutableListOf<Pair<String, String>>()
                    val ip = sharedGet("https://api.ipify.org?format=json", "application/json")
                    if (ip != null) {
                        @Suppress("DEPRECATION")
                        val json = JsonParser().parse(ip).asJsonObject
                        out.add(s.netPublicIp to (json.get("ip")?.asString ?: "—"))
                    }
                    if (withGeo) {
                        val details = sharedGet("https://ipapi.co/json/", "application/json")
                        if (details != null) {
                            @Suppress("DEPRECATION")
                            val obj = JsonParser().parse(details).asJsonObject
                            listOf(
                                "City" to "city",
                                "Region" to "region",
                                "Country" to "country_name",
                                "ISP" to "org",
                                "Timezone" to "timezone"
                            ).forEach { (label, key) ->
                                obj.get(key)?.takeIf { it.isJsonPrimitive }?.asString?.let { out.add(label to it) }
                            }
                        }
                    }
                    if (out.isEmpty()) throw IllegalStateException(s.gNoNetwork)
                    out
                }
            }
            running = false
            outcome.fold(
                onSuccess = { publicRows = it },
                onFailure = { error = it.message ?: s.gError }
            )
        }
    }

    ToolPage(
        title = s.toolName("net_ip"),
        subtitle = s.toolDescription("net_ip"),
        icon = BecettiGlyphs.Globe,
        accent = accent,
        hint = s.netIpHint
    ) {
        Panel(accent = accent) {
            SectionHeader(s.netLocalAddresses)
            if (localRows.isEmpty()) {
                Text(s.gLoading, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
            } else {
                localRows.forEach { (label, value) -> KeyValueRow(label, value, mono = true) }
            }
            SoftDivider()
            KeyValueRow(s.netConnection, connectionLabel(ctx))
        }

        Panel(accent = ToolAccents.Network) {
            SectionHeader(s.netPublicIp)
            ActionButton(
                text = s.netFetchPublicIp,
                onClick = { loadPublic(false) },
                accent = ToolAccents.Network,
                loading = running,
                icon = BecettiGlyphs.Globe
            )
            ActionButton(
                text = s.netFetchGeo,
                onClick = { loadPublic(true) },
                accent = ToolAccents.Files,
                outlined = true,
                enabled = !running,
                icon = BecettiGlyphs.NetworkNodes
            )
            publicRows.forEach { (label, value) -> KeyValueRow(label, value, mono = true) }
            error?.let {
                Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error)
            }
        }
    }
}

private fun localInterfaces(): List<Pair<String, String>> {
    val rows = mutableListOf<Pair<String, String>>()
    NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { iface ->
        if (!iface.isUp || iface.isLoopback) return@forEach
        iface.inetAddresses?.toList()?.forEach { address ->
            val host = address.hostAddress ?: return@forEach
            val clean = host.substringBefore('%')
            if (clean.contains(':')) return@forEach
            rows.add(iface.name to clean)
        }
    }
    return rows
}

private fun connectionLabel(ctx: Context): String {
    val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return "—"
    val network = cm.activeNetwork ?: return "offline"
    val caps = cm.getNetworkCapabilities(network) ?: return "offline"
    return when {
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile data"
        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
        else -> "Connected"
    }
}

/* ========================================= HTTP headers ===================================== */

@Composable
fun HttpHeadersScreen() {
    val s = LocalAppStrings.current
    val accent = ToolAccents.Network
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette
    val clipboard = LocalClipboardManager.current

    var url by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var redirects by remember { mutableStateOf<List<String>>(emptyList()) }
    var statusLine by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var running by remember { mutableStateOf(false) }

    fun inspect() {
        val target = url.trim()
        if (!target.startsWith("http")) {
            error = s.devInvalidUrl
            return
        }
        scope.launch {
            running = true
            error = null
            rows = emptyList()
            redirects = emptyList()
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    val request = Request.Builder().url(target).header("User-Agent", "BecettiTools").build()
                    netClient.newCall(request).execute().use { response ->
                        val chain = mutableListOf<String>()
                        var prior = response.priorResponse
                        while (prior != null) {
                            chain.add("${prior.code} ${prior.message} · ${prior.request.url}")
                            prior = prior.priorResponse
                        }
                        statusLine = "${response.code} ${response.message} · ${response.protocol}"
                        rows = response.headers.names().sorted().map { name ->
                            name to (response.header(name) ?: "")
                        }
                        redirects = chain.reversed()
                        response.code
                    }
                }
            }
            running = false
            outcome.fold(onSuccess = { }, onFailure = { error = it.message ?: s.gError })
        }
    }

    ToolPage(
        title = s.toolName("net_headers"),
        subtitle = s.toolDescription("net_headers"),
        icon = BecettiGlyphs.HttpWindow,
        accent = accent,
        hint = s.netHeadersHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = url,
                onValueChange = { url = it; error = null },
                label = s.devUrl,
                placeholder = "https://becetti.tools",
                keyboardType = KeyboardType.Uri,
                accent = accent
            )
            ActionButton(
                text = s.netInspect,
                onClick = { inspect() },
                accent = accent,
                loading = running,
                icon = BecettiGlyphs.HttpWindow
            )
        }
        if (running) LoadingBlock(s.gProcessing, accent = accent)
        error?.let { OutputBlock(text = "", accent = accent, error = it, errorLabel = s.errorPrefix) }

        if (statusLine.isNotEmpty()) {
            Panel(accent = ToolAccents.Business) {
                KeyValueRow(s.gStatus, statusLine, mono = true, accent = ToolAccents.Business)
                if (redirects.isNotEmpty()) {
                    SoftDivider()
                    SectionHeader("${s.netRedirects} (${redirects.size})")
                    redirects.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary) }
                }
            }
        }
        if (rows.isNotEmpty()) {
            Panel(accent = accent) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${s.devResponseHeaders} (${rows.size})", style = MaterialTheme.typography.titleSmall, color = palette.textPrimary)
                    IconAction(BecettiGlyphs.CopyStack, s.copy, {
                        clipboard.setText(AnnotatedString(rows.joinToString("\n") { "${it.first}: ${it.second}" }))
                    }, accent)
                }
                rows.forEach { (name, value) -> KeyValueRow(name, value, mono = true) }
            }
        }
    }
}

/* ========================================= Diagnostics ====================================== */

private data class DiagnosticRow(val label: String, val value: String, val ok: Boolean)

@Composable
fun DiagnosticsScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val accent = ToolAccents.Media
    val scope = rememberCoroutineScope()
    val palette = BecettiTheme.palette

    var host by remember { mutableStateOf("1.1.1.1") }
    var rows by remember { mutableStateOf<List<DiagnosticRow>>(emptyList()) }
    var running by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun runChecks() {
        val target = host.trim()
        if (target.isEmpty()) {
            error = s.gInvalidInput
            return
        }
        scope.launch {
            running = true
            error = null
            rows = emptyList()
            val computed = withContext(Dispatchers.IO) {
                val out = mutableListOf<DiagnosticRow>()
                out.add(DiagnosticRow(s.netConnection, connectionLabel(ctx), connectionLabel(ctx) != "offline"))

                val dnsStart = System.currentTimeMillis()
                val resolved = runCatching { InetAddress.getByName(target) }.getOrNull()
                val dnsMs = System.currentTimeMillis() - dnsStart
                out.add(
                    DiagnosticRow(
                        s.netDnsTime,
                        if (resolved != null) "$dnsMs ms · ${resolved.hostAddress}" else s.netFailed,
                        resolved != null
                    )
                )

                val tcpStart = System.currentTimeMillis()
                val tcpOk = runCatching {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress(target, 443), 4000)
                        true
                    }
                }.getOrDefault(false)
                out.add(DiagnosticRow(s.netTcpConnect, "${System.currentTimeMillis() - tcpStart} ms · :443", tcpOk))

                val httpStart = System.currentTimeMillis()
                val httpOk = runCatching {
                    netClient.newCall(
                        Request.Builder().url("https://$target").header("User-Agent", "BecettiTools").build()
                    ).execute().use { it.code }
                }.getOrNull()
                out.add(
                    DiagnosticRow(
                        s.netHttpsCheck,
                        if (httpOk != null) "${System.currentTimeMillis() - httpStart} ms · HTTP $httpOk" else s.netFailed,
                        httpOk != null
                    )
                )

                val pingStart = System.currentTimeMillis()
                val reachable = runCatching { InetAddress.getByName(target).isReachable(3000) }.getOrDefault(false)
                out.add(
                    DiagnosticRow(
                        s.netReachable,
                        if (reachable) "${System.currentTimeMillis() - pingStart} ms" else s.netFailed,
                        reachable
                    )
                )
                out
            }
            rows = computed
            running = false
        }
    }

    ToolPage(
        title = s.toolName("net_diagnostics"),
        subtitle = s.toolDescription("net_diagnostics"),
        icon = BecettiGlyphs.Pulse,
        accent = accent,
        hint = s.netDiagHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = host,
                onValueChange = { host = it; error = null },
                label = s.netHostOrIp,
                placeholder = "1.1.1.1",
                accent = accent
            )
            ActionButton(
                text = s.netRunChecks,
                onClick = { runChecks() },
                accent = accent,
                loading = running,
                icon = BecettiGlyphs.Pulse
            )
            error?.let { Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error) }
        }

        if (running) LoadingBlock(s.gProcessing, accent = accent)

        if (rows.isNotEmpty()) {
            Panel(accent = accent) {
                SectionHeader(s.gResult)
                rows.forEach { row ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(row.label, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                        Text(
                            row.value,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (row.ok) palette.success else palette.error
                        )
                    }
                }
            }
        }
    }
}

/* ======================================= Link inspector ===================================== */

@Composable
fun LinkInspectorScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val accent = ToolAccents.Network
    val palette = BecettiTheme.palette

    var input by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var params by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var warnings by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmOpen by remember { mutableStateOf(false) }
    var target by remember { mutableStateOf("") }

    fun inspect() {
        error = null
        rows = emptyList()
        params = emptyList()
        warnings = emptyList()
        val raw = input.trim()
        runCatching {
            val uri = URI(raw)
            val out = mutableListOf<Pair<String, String>>()
            uri.scheme?.let { out.add("Scheme" to it) }
            uri.host?.let { out.add("Host" to it) }
            if (uri.port != -1) out.add("Port" to uri.port.toString())
            uri.path?.takeIf { it.isNotBlank() }?.let { out.add("Path" to it) }
            uri.fragment?.let { out.add("Fragment" to it) }
            uri.userInfo?.let { out.add("User info" to it) }
            out.add(s.gSize to "${raw.length} chars")

            val found = mutableListOf<Pair<String, String>>()
            uri.rawQuery?.split('&')?.filter { it.isNotBlank() }?.forEach { pair ->
                val key = pair.substringBefore('=')
                val value = if (pair.contains('=')) pair.substringAfter('=') else ""
                found.add(runCatching { URLDecoder.decode(key, "UTF-8") }.getOrDefault(key) to
                    runCatching { URLDecoder.decode(value, "UTF-8") }.getOrDefault(value))
            }

            val flags = mutableListOf<String>()
            if (uri.scheme == "http") flags.add(s.netInsecureHttp)
            uri.host?.let { host ->
                if (host.matches(Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$"))) flags.add(s.netIpHost)
                if (host.startsWith("xn--") || host.contains(".xn--")) flags.add(s.netPunycode)
                if (host.count { it == '.' } >= 4) flags.add(s.netManySubdomains)
            }
            if (uri.userInfo != null) flags.add(s.netHasCredentials)
            if (raw.contains("@") && uri.userInfo == null && uri.host?.contains("@") == true) flags.add(s.netAtTrick)
            if (uri.port != -1 && uri.port !in listOf(80, 443)) flags.add(s.netCustomPort)

            rows = out
            params = found
            warnings = flags
            target = raw
        }.onFailure {
            error = s.devInvalidUrl
        }
    }

    ToolPage(
        title = s.toolName("net_link"),
        subtitle = s.toolDescription("net_link"),
        icon = BecettiGlyphs.LinkChain,
        accent = accent,
        hint = s.gLocalOnly
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = input,
                onValueChange = { input = it; error = null },
                label = s.netLinkInput,
                placeholder = "https://example.com/path?utm_source=becetti",
                singleLine = false,
                minLines = 2,
                maxLines = 5,
                keyboardType = KeyboardType.Uri,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = s.netInspect,
                    onClick = { inspect() },
                    accent = accent,
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
                ActionButton(
                    text = s.gOpen,
                    onClick = { if (target.isNotEmpty()) confirmOpen = true },
                    accent = ToolAccents.Files,
                    outlined = true,
                    enabled = target.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    fillWidth = false
                )
            }
            error?.let { Text("${s.errorPrefix}: $it", style = MaterialTheme.typography.bodySmall, color = palette.error) }
        }

        if (rows.isNotEmpty()) {
            Panel(accent = ToolAccents.Files) {
                SectionHeader(s.gDetails)
                rows.forEach { (key, value) -> KeyValueRow(key, value, mono = true) }
            }
        }
        if (warnings.isNotEmpty()) {
            Panel(accent = palette.warning) {
                SectionHeader(s.netSafety)
                warnings.forEach { warning ->
                    Text("⚠ $warning", style = MaterialTheme.typography.bodySmall, color = palette.warning)
                }
            }
        }
        if (params.isNotEmpty()) {
            Panel(accent = accent) {
                SectionHeader("${s.devQueryParams} (${params.size})")
                params.forEach { (key, value) -> KeyValueRow(key, value, mono = true) }
            }
        }
    }

    ConfirmDialog(
        show = confirmOpen,
        title = s.qrOpenLinkConfirm,
        message = target,
        confirmLabel = s.gOpen,
        dismissLabel = s.cancel,
        accent = accent,
        onConfirm = {
            confirmOpen = false
            runCatching {
                ctx.startActivity(
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(target))
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        },
        onDismiss = { confirmOpen = false }
    )
}
