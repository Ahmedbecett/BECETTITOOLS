package com.becetti.tools.features.text

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun TextToolkitScreen(){
    var input by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current
    val words = remember(input){ input.trim().split(Regex("\\s+")).filter{it.isNotEmpty()}.size }
    val chars = input.length
    val charsNoSpaces = input.replace(" ","").length
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)){
        Text("Text Toolkit", style=MaterialTheme.typography.titleLarge)
        OutlinedTextField(value=input, onValueChange={input=it}, modifier=Modifier.fillMaxWidth().height(160.dp), placeholder={Text("Enter text...")})
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ Text("Words: $words"); Text("Chars: $chars"); Text("NoSpaces: $charsNoSpaces") }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()){
            Button(onClick={ input=input.uppercase() }){ Text("UPPER") }
            Button(onClick={ input=input.lowercase() }){ Text("lower") }
            Button(onClick={ input=input.replace(Regex(" +")," ") }){ Text("De-dup spaces") }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Button(onClick={ input=input.lines().filter{it.isNotBlank()}.joinToString("\n") }){ Text("Remove empty lines") }
            Button(onClick={ input=input.reversed() }){ Text("Reverse") }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Button(onClick={ clipboard.setText(AnnotatedString(input)) }){ Text("Copy") }
            OutlinedButton(onClick={ input="" }){ Text("Clear") }
        }
    }
}
