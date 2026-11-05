package com.becetti.tools.features.ocr

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@Composable
fun OcrScreen(){
    val ctx = LocalContext.current
    var uri by remember { mutableStateOf<Uri?>(null) }
    var text by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){ uri=it }
    fun runOcr(){
        uri?.let{ u->
            loading=true
            try{
                ctx.contentResolver.openInputStream(u)?.use{ ins->
                    val bmp = BitmapFactory.decodeStream(ins)
                    val image = InputImage.fromBitmap(bmp,0)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    recognizer.process(image).addOnSuccessListener{ res-> text=res.text; loading=false }.addOnFailureListener{ text="Error: ${'$'}{it.message}"; loading=false }
                }
            }catch(e: Exception){ text="Error: ${'$'}{e.message}"; loading=false }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("OCR Text Extractor", style=MaterialTheme.typography.titleLarge)
        Button(onClick={ launcher.launch("image/*") }, modifier=Modifier.fillMaxWidth()){ Text("Select Image") }
        uri?.let{ Text(it.toString(), style=MaterialTheme.typography.bodySmall) }
        Button(onClick={ runOcr() }, enabled=uri!=null, modifier=Modifier.fillMaxWidth()){ if(loading) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Extract Text") }
        if(text.isNotEmpty()){
            Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(12.dp)){ Text(text) } }
            Button(onClick={ clipboard.setText(AnnotatedString(text)) }, modifier=Modifier.fillMaxWidth()){ Text("Copy Text") }
        }
        Text("Supports Arabic, French, English where ML Kit supports. Works offline after model download.", style=MaterialTheme.typography.bodySmall)
    }
}
