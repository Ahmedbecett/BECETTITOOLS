package com.becetti.tools.features.files

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.GlowBlue
import com.becetti.tools.design.theme.GlowCyan
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun WifiTransferScreen(nav: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val strings = LocalAppStrings.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val receiveDirectory = remember(context) { File(context.filesDir, "wifi_received").apply { mkdirs() } }
    val pairingHandle = remember(nav) { nav.currentBackStackEntry?.savedStateHandle }
    val pairingFlow = remember(pairingHandle) {
        pairingHandle?.getStateFlow<String?>(WIFI_PAIRING_RESULT_KEY, null)
    }
    val scannedCodeState = pairingFlow?.collectAsState(initial = null)
    val scannedCode = scannedCodeState?.value

    var receiving by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var receiver by remember { mutableStateOf<WifiTransferServer?>(null) }
    var receiveJob by remember { mutableStateOf<Job?>(null) }
    var pairingCode by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sendMode by remember { mutableStateOf(false) }
    var exportTarget by remember { mutableStateOf<File?>(null) }
    var deleteTarget by remember { mutableStateOf<File?>(null) }
    var confirmClearCache by remember { mutableStateOf(false) }
    var receivedFiles by remember(receiveDirectory) {
        mutableStateOf(receiveDirectory.listFiles()?.filter { it.isFile && !it.name.endsWith(".part") }
            ?.sortedByDescending { it.lastModified() }.orEmpty())
    }

    LaunchedEffect(scannedCode) {
        if (!scannedCode.isNullOrBlank()) {
            pairingCode = scannedCode
            pairingHandle?.set(WIFI_PAIRING_RESULT_KEY, null)
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            val pairing = TransferPairing.parse(pairingCode)
            if (pairing == null) {
                status = strings.wifiTransferFailed + ": " + strings.wifiPairCodeHint
            } else {
                sending = true
                status = strings.wifiSendFiles + "…"
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            uris.forEach { uri -> sendSelectedFile(context, uri, pairing) }
                        }
                        status = strings.wifiTransferComplete
                    } catch (error: Throwable) {
                        status = strings.wifiTransferFailed + ": " + (error.message ?: "")
                    } finally {
                        pairing.secret.fill(0)
                        sending = false
                    }
                }
            }
        }
    }

    val exportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { destination ->
        val source = exportTarget
        exportTarget = null
        if (destination != null && source != null) {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(destination)?.use { output ->
                            source.inputStream().use { input -> input.copyTo(output) }
                        } ?: error("Unable to create the destination file")
                    }
                    status = strings.wifiExportFile + ": ${source.name}"
                } catch (error: Throwable) {
                    status = strings.wifiTransferFailed + ": " + (error.message ?: "")
                }
            }
        }
    }

    fun stopReceiver() {
        receiveJob?.cancel()
        receiver?.close()
        receiveJob = null
        receiver = null
        pairingCode = ""
        qrBitmap = null
        receiving = false
    }

    DisposableEffect(lifecycleOwner, receiving, strings.wifiStopReceiver) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && receiving) {
                stopReceiver()
                status = strings.wifiStopReceiver
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            receiveJob?.cancel()
            receiver?.close()
        }
    }

    fun startReceiver() {
        var newServer: WifiTransferServer? = null
        try {
            val server = WifiTransferServer(context)
            newServer = server
            val newQr = createPairingQr(server.pairingCode)
            receiver = server
            pairingCode = server.pairingCode
            qrBitmap = newQr
            receiving = true
            status = strings.wifiReceiverReady
            receiveJob = scope.launch {
                server.listen(
                    onReceived = { file ->
                        receivedFiles = server.receivedFiles
                        status = strings.wifiTransferComplete + ": ${file.name}"
                    },
                    onFailure = { error -> status = strings.wifiTransferFailed + ": $error" }
                )
            }
        } catch (_: Throwable) {
            newServer?.close()
            receiver = null
            qrBitmap = null
            receiving = false
            status = strings.wifiNetworkUnavailable
        }
    }

    val toolText = strings.tools["wifi_transfer"]
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "wifi_transfer",
            title = toolText?.name ?: "Wi-Fi File Transfer",
            subtitle = toolText?.description ?: ""
        )

        Text(strings.wifiSameNetworkNote, style = MaterialTheme.typography.bodySmall)
        Text(strings.wifiSafetyNote, style = MaterialTheme.typography.bodySmall, color = GlowBlue)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = !sendMode,
                onClick = { sendMode = false },
                label = { Text(strings.wifiReceiveFiles) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = sendMode,
                onClick = { sendMode = true },
                label = { Text(strings.wifiSendFiles) },
                modifier = Modifier.weight(1f)
            )
        }

        if (!sendMode) {
            if (!receiving) {
                Button(onClick = ::startReceiver, modifier = Modifier.fillMaxWidth()) {
                    Text(strings.wifiStartReceiver)
                }
            } else {
                qrBitmap?.let { bitmap ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = strings.wifiScanCode,
                                modifier = Modifier.size(240.dp)
                            )
                            Text(strings.wifiReceiverReady, fontWeight = FontWeight.SemiBold)
                            OutlinedButton(onClick = {
                                clipboard.setText(AnnotatedString(pairingCode))
                                status = strings.wifiReceiverReady
                            }) {
                                Text(strings.wifiPairCodeHint)
                            }
                        }
                    }
                }
                Button(onClick = ::stopReceiver, modifier = Modifier.fillMaxWidth()) {
                    Text(strings.wifiStopReceiver)
                }
            }

            GlowingCard(accentColor = GlowCyan) {
                Text(strings.wifiReceivedFiles, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (receivedFiles.isEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("—", style = MaterialTheme.typography.bodySmall)
                } else {
                    receivedFiles.forEach { file ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(file.name, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                                Text(Formatter.formatShortFileSize(context, file.length()), style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { exportTarget = file; exportPicker.launch(file.name) }) {
                                Text(strings.wifiExportFile)
                            }
                            TextButton(onClick = { deleteTarget = file }) {
                                Text(strings.wifiDeleteFile)
                            }
                        }
                    }
                }
            }

            OutlinedButton(onClick = { confirmClearCache = true }, modifier = Modifier.fillMaxWidth()) {
                Text(strings.wifiClearCache)
            }
        } else {
            OutlinedTextField(
                value = pairingCode,
                onValueChange = { pairingCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.wifiPairCodeHint) },
                singleLine = false
            )
            OutlinedButton(
                onClick = { nav.navigate(Route.WifiTransferScan.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.wifiScanCode)
            }
            Button(
                enabled = !sending,
                onClick = {
                    if (TransferPairing.parse(pairingCode) == null) {
                        status = strings.wifiTransferFailed + ": " + strings.wifiPairCodeHint
                    } else {
                        filePicker.launch(arrayOf("*/*"))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (sending) strings.wifiSendFiles + "…" else strings.wifiSelectFiles)
            }
        }

        if (status.isNotBlank()) {
            Text(status, style = MaterialTheme.typography.bodyMedium, color = GlowCyan)
        }
    }

    deleteTarget?.let { file ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(strings.wifiDeleteFile) },
            text = { Text(file.name) },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget = null
                    scope.launch {
                        withContext(Dispatchers.IO) { file.delete() }
                        receivedFiles = receiveDirectory.listFiles()
                            ?.filter { it.isFile && !it.name.endsWith(".part") }
                            ?.sortedByDescending { it.lastModified() }.orEmpty()
                    }
                }) { Text(strings.wifiDeleteFile) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(strings.cancel) } }
        )
    }

    if (confirmClearCache) {
        AlertDialog(
            onDismissRequest = { confirmClearCache = false },
            title = { Text(strings.wifiClearCache) },
            text = { Text(strings.wifiClearCacheConfirm) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClearCache = false
                    scope.launch {
                        val bytesFreed = withContext(Dispatchers.IO) {
                            val cache = context.cacheDir
                            val bytes = cache.walkTopDown().filter { it.isFile }.sumOf { it.length() }
                            cache.listFiles()?.forEach { it.deleteRecursively() }
                            cache.mkdirs()
                            bytes
                        }
                        status = "${strings.wifiClearCache}: ${Formatter.formatShortFileSize(context, bytesFreed)}"
                    }
                }) { Text(strings.wifiClearCache) }
            },
            dismissButton = { TextButton(onClick = { confirmClearCache = false }) { Text(strings.cancel) } }
        )
    }
}

private fun createPairingQr(payload: String): Bitmap {
    val matrix = QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 512, 512)
    val pixels = IntArray(matrix.width * matrix.height)
    for (y in 0 until matrix.height) {
        for (x in 0 until matrix.width) {
            pixels[y * matrix.width + x] = if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888).apply {
        setPixels(pixels, 0, matrix.width, 0, 0, matrix.width, matrix.height)
    }
}
