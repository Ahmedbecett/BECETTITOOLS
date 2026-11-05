package com.becetti.tools.features.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun QrScannerScreen(){
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPerm by remember { mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){ hasPerm = it }
    var result by remember { mutableStateOf<String?>(null) }
    var pendingUrl by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("QR Code Scanner", style=MaterialTheme.typography.titleLarge)
        if(!hasPerm){
            Button(onClick={ launcher.launch(Manifest.permission.CAMERA) }){ Text("Grant Camera Permission") }
            Text("Permission requested only when needed (spec §7, §10).", style=MaterialTheme.typography.bodySmall)
        } else {
            if(result==null){
                AndroidView(factory={
                    val pv = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val provider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also{ it.setSurfaceProvider(pv.surfaceProvider) }
                        val analyzer = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also{
                            it.setAnalyzer(ContextCompat.getMainExecutor(ctx)){ imageProxy ->
                                val mediaImage = imageProxy.image
                                if(mediaImage!=null){
                                    val input = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    BarcodeScanning.getClient().process(input).addOnSuccessListener{ barcodes->
                                        if(barcodes.isNotEmpty()){
                                            result = barcodes.first().rawValue
                                        }
                                    }.addOnCompleteListener{ imageProxy.close() }
                                } else imageProxy.close()
                            }
                        }
                        try{ provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer) }catch(e: Exception){}
                    }, ContextCompat.getMainExecutor(ctx))
                    pv
                }, modifier=Modifier.fillMaxWidth().height(360.dp))
            } else {
                Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text(result!!) } }
                val isUrl = result!!.startsWith("http")
                if(isUrl) pendingUrl = result
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    if(pendingUrl!=null) Button(onClick={ ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pendingUrl)).apply{ addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }) }){ Text("Open Link (confirm)") }
                    OutlinedButton(onClick={ result=null; pendingUrl=null }){ Text("Scan Again") }
                }
                Text("Links open only after user confirmation per policy.", style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}
