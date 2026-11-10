package com.becetti.tools.features.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun QrScannerScreen(onCodeScanned: ((String) -> Unit)? = null) {
    val ctx = LocalContext.current
    val s = LocalAppStrings.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboard = LocalClipboardManager.current
    var hasPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPerm = it }
    var result by remember { mutableStateOf<String?>(null) }
    var torchOn by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    val codeReported = remember { AtomicBoolean(false) }

    fun reportCode(value: String) {
        if (onCodeScanned != null) {
            if (codeReported.compareAndSet(false, true)) onCodeScanned(value)
        } else {
            result = value
            copied = false
        }
    }

    val scanner = remember { runCatching { BarcodeScanning.getClient() }.getOrNull() }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { pickedUri ->
        pickedUri?.let { u ->
            runCatching {
                ctx.contentResolver.openInputStream(u)?.use { ins ->
                    val bmp = BitmapFactory.decodeStream(ins)
                    if (bmp != null && scanner != null) {
                        val img = InputImage.fromBitmap(bmp, 0)
                        scanner.process(img).addOnSuccessListener { codes ->
                            if (codes.isNotEmpty()) {
                                codes.first().rawValue?.let(::reportCode)
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { cameraProviderRef?.unbindAll() }
            runCatching { scanner?.close() }
        }
    }

    val toolMeta = s.tools["qr_scanner"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "qr_scanner",
            title = toolMeta?.name ?: "QR Scanner",
            subtitle = toolMeta?.description ?: ""
        )

        OutlinedButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("🖼️ ${s.qrPickFromGallery}", fontWeight = FontWeight.SemiBold)
        }

        if (!hasPerm) {
            GlowingCard(accentColor = GlowBlue) {
                Button(
                    onClick = { permLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowBlue)
                ) {
                    Text(s.qrGrantCamera, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                Text(s.qrCameraRationale, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            if (result == null) {
                GlowingCard(accentColor = GlowBlue) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        AssistChip(
                            onClick = {
                                torchOn = !torchOn
                                runCatching { activeCamera?.cameraControl?.enableTorch(torchOn) }
                            },
                            label = { Text(if (torchOn) "🔦 ${s.qrTorchOn}" else "🔦 ${s.qrTorchOff}") }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    AndroidView(
                        factory = {
                            val pv = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                runCatching {
                                    val provider = cameraProviderFuture.get()
                                    cameraProviderRef = provider
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(pv.surfaceProvider)
                                    }
                                    val analyzer = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also { analysis ->
                                            analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                                val mediaImage = imageProxy.image
                                                val activeScanner = scanner
                                                if (mediaImage != null && activeScanner != null) {
                                                    val input = InputImage.fromMediaImage(
                                                        mediaImage,
                                                        imageProxy.imageInfo.rotationDegrees
                                                    )
                                                    activeScanner.process(input)
                                                        .addOnSuccessListener { barcodes ->
                                                            if (barcodes.isNotEmpty()) {
                                                                barcodes.first().rawValue?.let(::reportCode)
                                                            }
                                                        }
                                                        .addOnCompleteListener { imageProxy.close() }
                                                } else {
                                                    imageProxy.close()
                                                }
                                            }
                                        }
                                    provider.unbindAll()
                                    activeCamera = provider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        analyzer
                                    )
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            pv
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            } else {
                val scannedValue = result ?: ""
                GlowingCard(accentColor = GlowEmerald) {
                    Text(s.resultLabel, style = MaterialTheme.typography.labelLarge, color = GlowEmerald)
                    Spacer(Modifier.height(6.dp))
                    Text(scannedValue, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    val isUrl = scannedValue.startsWith("http://") || scannedValue.startsWith("https://")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isUrl) {
                            Button(
                                onClick = {
                                    runCatching {
                                        ctx.startActivity(
                                            Intent(Intent.ACTION_VIEW, Uri.parse(scannedValue)).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                            ) {
                                Text(s.qrOpenLinkConfirm, color = Color.White)
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(scannedValue))
                                copied = true
                            }
                        ) {
                            Text(if (copied) s.copied else s.copy)
                        }
                        OutlinedButton(onClick = { result = null; copied = false }) {
                            Text(s.qrScanAgain)
                        }
                    }
                }
                Text(s.qrPolicyNote, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
