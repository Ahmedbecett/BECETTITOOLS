package com.becetti.tools.features.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Environment

@Composable
fun ImageCompressorScreen(){
    val ctx = LocalContext.current
    var uri by remember { mutableStateOf<Uri?>(null) }
    var quality by remember { mutableStateOf(70f) }
    var originalBmp by remember { mutableStateOf<Bitmap?>(null) }
    var compressedBmp by remember { mutableStateOf<Bitmap?>(null) }
    var sizes by remember { mutableStateOf<Pair<Int,Int>?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ it?.let{ u-> uri=u; ctx.contentResolver.openInputStream(u)?.use{ ins-> originalBmp=BitmapFactory.decodeStream(ins) } } }
    fun compress(){
        originalBmp?.let{ bmp->
            val out = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, quality.toInt(), out)
            val bytes = out.toByteArray()
            compressedBmp = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            sizes = (bmp.byteCount/1024) to (bytes.size/1024)
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Image Compressor", style=MaterialTheme.typography.titleLarge)
        Button(onClick={ launcher.launch("image/*") }, modifier=Modifier.fillMaxWidth()){ Text("Select Image") }
        Text("Quality: ${'$'}{quality.toInt()}%")
        Slider(value=quality, onValueChange={quality=it; compress()}, valueRange=10f..100f)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()){
            originalBmp?.let{ Image(bitmap=it.asImageBitmap(), contentDescription="Original", modifier=Modifier.weight(1f).height(160.dp)) }
            compressedBmp?.let{ Image(bitmap=it.asImageBitmap(), contentDescription="Compressed", modifier=Modifier.weight(1f).height(160.dp)) }
        }
        sizes?.let{ (o,c)-> Text("Original: ${'$'}o KB → Compressed: ${'$'}c KB", style=MaterialTheme.typography.bodyMedium) }
        Button(onClick={
            compressedBmp?.let{ bmp->
                val values = ContentValues().apply{ put(MediaStore.Images.Media.DISPLAY_NAME, "compressed_${'$'}{System.currentTimeMillis()}.jpg"); put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg"); put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Becetti") }
                val outUri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                outUri?.let{ u-> ctx.contentResolver.openOutputStream(u)?.use{ out-> bmp.compress(Bitmap.CompressFormat.JPEG, quality.toInt(), out)} }
            }
        }, enabled=compressedBmp!=null, modifier=Modifier.fillMaxWidth()){ Text("Save Compressed") }
    }
}
