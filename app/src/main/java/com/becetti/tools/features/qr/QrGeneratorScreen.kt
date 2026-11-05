package com.becetti.tools.features.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.launch
import java.io.OutputStream

fun generateQrBitmap(content: String, size:Int=512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size,size, Bitmap.Config.RGB_565)
    for(x in 0 until size) for(y in 0 until size) bmp.setPixel(x,y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
    return bmp
}

@Composable
fun QrGeneratorScreen(){
    var type by remember { mutableStateOf("Text") }
    var text by remember { mutableStateOf("https://becetti.tools") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPass by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    val content = when(type){
        "WiFi" -> "WIFI:T:WPA;S:${'$'}wifiSsid;P:${'$'}wifiPass;;"
        "Contact" -> "BEGIN:VCARD\nFN:${'$'}contactName\nTEL:${'$'}contactPhone\nEND:VCARD"
        else -> text
    }
    val bmp = remember(content){ try{ generateQrBitmap(content) } catch(e: Exception){ null } }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("QR Code Generator", style=MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ listOf("Text","URL","WiFi","Contact").forEach{ t-> FilterChip(selected=type==t, onClick={type=t}, label={Text(t)}) } }
        when(type){
            "Text","URL" -> OutlinedTextField(value=text, onValueChange={text=it}, label={Text("Content")}, modifier=Modifier.fillMaxWidth())
            "WiFi" -> { OutlinedTextField(value=wifiSsid, onValueChange={wifiSsid=it}, label={Text("SSID")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=wifiPass, onValueChange={wifiPass=it}, label={Text("Password")}, modifier=Modifier.fillMaxWidth()) }
            "Contact" -> { OutlinedTextField(value=contactName, onValueChange={contactName=it}, label={Text("Name")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=contactPhone, onValueChange={contactPhone=it}, label={Text("Phone")}, modifier=Modifier.fillMaxWidth()) }
        }
        bmp?.let{ Image(bitmap=it.asImageBitmap(), contentDescription="QR", modifier=Modifier.size(240.dp)) }
        Button(onClick={
            bmp?.let{ b->
                val values = ContentValues().apply{ put(MediaStore.Images.Media.DISPLAY_NAME, "qr_${'$'}{System.currentTimeMillis()}.png"); put(MediaStore.Images.Media.MIME_TYPE,"image/png"); put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/Becetti") }
                val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let{ u-> ctx.contentResolver.openOutputStream(u)?.use{ out-> b.compress(Bitmap.CompressFormat.PNG,100,out)} }
            }
        }, modifier=Modifier.fillMaxWidth()){ Text("Export PNG") }
        Text("Generates locally, no upload.", style=MaterialTheme.typography.bodySmall)
    }
}
