package com.becetti.tools.features.pdf

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tom_roush.pdfbox.pdmodel.PDDocument

@Composable
fun ImageToPdfScreen(){
    val ctx = LocalContext.current
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pageSize by remember { mutableStateOf("A4") }
    var status by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()){ uris = it }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Image to PDF", style=MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ listOf("A4","A3","Letter").forEach{ s-> FilterChip(selected=pageSize==s, onClick={pageSize=s}, label={Text(s)}) } }
        Button(onClick={ launcher.launch("image/*") }, modifier=Modifier.fillMaxWidth()){ Text("Select Images (${'$'}{uris.size})") }
        if(uris.isNotEmpty()){
            Text("Order: drag not yet — uses selection order. Remove via clear.", style=MaterialTheme.typography.bodySmall)
            Button(onClick={
                try{
                    val doc = PdfDocument()
                    uris.forEachIndexed{ idx, uri->
                        ctx.contentResolver.openInputStream(uri)?.use{ ins->
                            val bmp = BitmapFactory.decodeStream(ins)
                            val (w,h) = when(pageSize){ "A3"-> 842 to 1190; "Letter"-> 612 to 792; else-> 595 to 842 }
                            val pageInfo = PdfDocument.PageInfo.Builder(w,h, idx+1).create()
                            val page = doc.startPage(pageInfo)
                            val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, w-40, h-40, true)
                            page.canvas.drawBitmap(scaled, 20f,20f,null)
                            doc.finishPage(page)
                        }
                    }
                    val values = ContentValues().apply{ put(MediaStore.Files.FileColumns.DISPLAY_NAME, "becetti_${'$'}{System.currentTimeMillis()}.pdf"); put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf"); put(MediaStore.Files.FileColumns.RELATIVE_PATH, "Documents/Becetti") }
                    val outUri = ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                    outUri?.let{ u-> ctx.contentResolver.openOutputStream(u)?.use{ out-> doc.writeTo(out)} }
                    doc.close()
                    status="PDF exported to Documents/Becetti"
                }catch(e: Exception){ status="Error: ${'$'}{e.message}" }
            }, modifier=Modifier.fillMaxWidth()){ Text("Export PDF") }
            OutlinedButton(onClick={ uris=emptyList() }, modifier=Modifier.fillMaxWidth()){ Text("Clear") }
        }
        if(status.isNotEmpty()) Text(status, color=MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun PdfMergerScreen(){
    val ctx = LocalContext.current
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var status by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()){ uris = it }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("PDF Merger", style=MaterialTheme.typography.titleLarge)
        Button(onClick={ launcher.launch("application/pdf") }, modifier=Modifier.fillMaxWidth()){ Text("Select PDFs (${'$'}{uris.size})") }
        if(uris.isNotEmpty()){
            uris.forEachIndexed{ idx, uri-> Text("${'$'}{idx+1}. ${'$'}{uri.lastPathSegment}") }
            Button(onClick={
                try{
                    val outDoc = PDDocument()
                    uris.forEach{ uri->
                        ctx.contentResolver.openInputStream(uri)?.use{ ins->
                            val doc = PDDocument.load(ins)
                            doc.pages.forEach{ outDoc.addPage(it) }
                            doc.close()
                        }
                    }
                    val values = ContentValues().apply{ put(MediaStore.Files.FileColumns.DISPLAY_NAME, "merged_${'$'}{System.currentTimeMillis()}.pdf"); put(MediaStore.Files.FileColumns.MIME_TYPE,"application/pdf"); put(MediaStore.Files.FileColumns.RELATIVE_PATH,"Documents/Becetti") }
                    val outUri = ctx.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                    outUri?.let{ u-> ctx.contentResolver.openOutputStream(u)?.use{ out-> outDoc.save(out)} }
                    outDoc.close()
                    status="Merged PDF saved to Documents/Becetti (originals preserved)"
                }catch(e: Exception){ status="Error: ${'$'}{e.message}" }
            }, modifier=Modifier.fillMaxWidth()){ Text("Merge and Save") }
            OutlinedButton(onClick={ uris=emptyList() }){ Text("Clear") }
        }
        if(status.isNotEmpty()) Text(status)
        Text("Preserves original files.", style=MaterialTheme.typography.bodySmall)
    }
}
