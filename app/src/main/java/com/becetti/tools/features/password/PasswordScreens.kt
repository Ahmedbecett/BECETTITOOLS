package com.becetti.tools.features.password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.security.SecurityUtils

@Composable
fun PasswordGenScreen(){
    var len by remember { mutableStateOf(16f) }
    var upper by remember { mutableStateOf(true) }
    var lower by remember { mutableStateOf(true) }
    var digits by remember { mutableStateOf(true) }
    var symbols by remember { mutableStateOf(true) }
    var pwd by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Password Generator", style=MaterialTheme.typography.titleLarge)
        Text("Length: ${'$'}{len.toInt()}")
        Slider(value=len, onValueChange={len=it}, valueRange=4f..64f, steps=59)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ FilterChip(selected=upper, onClick={upper=!upper}, label={Text("ABC")}); FilterChip(selected=lower, onClick={lower=!lower}, label={Text("abc")}); FilterChip(selected=digits, onClick={digits=!digits}, label={Text("123")}); FilterChip(selected=symbols, onClick={symbols=!symbols}, label={Text("#@!")}) }
        Button(onClick={ pwd = SecurityUtils.secureRandomPassword(len.toInt(), upper, lower, digits, symbols) }, modifier=Modifier.fillMaxWidth()){ Text("Generate Locally") }
        if(pwd.isNotEmpty()){
            Card(Modifier.fillMaxWidth()){ Box(Modifier.padding(16.dp)){ Text(pwd) } }
            Button(onClick={ clipboard.setText(AnnotatedString(pwd)) }, modifier=Modifier.fillMaxWidth()){ Text("Copy") }
        }
        Text("Generated on-device, never sent.", style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PasswordStrengthScreen(){
    var input by remember { mutableStateOf("") }
    val score = remember(input){ SecurityUtils.passwordStrength(input) }
    val label = when(score){ 0->"Very Weak";1->"Weak";2->"Fair";3->"Good";4->"Strong";5->"Very Strong"; else->""}
    val color = when(score){ 0,1->MaterialTheme.colorScheme.error; 2->MaterialTheme.colorScheme.tertiary; 3,4->MaterialTheme.colorScheme.primary; 5->MaterialTheme.colorScheme.primary; else->MaterialTheme.colorScheme.primary}
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Password Strength Checker", style=MaterialTheme.typography.titleLarge)
        OutlinedTextField(value=input, onValueChange={input=it}, label={Text("Enter password")}, modifier=Modifier.fillMaxWidth())
        LinearProgressIndicator(progress=score/5f, modifier=Modifier.fillMaxWidth(), color=color)
        Text(label, style=MaterialTheme.typography.titleMedium, color=color)
        Text("Analysis runs locally. Passwords are never stored or uploaded.", style=MaterialTheme.typography.bodySmall)
        Card(Modifier.fillMaxWidth()){ Column(Modifier.padding(12.dp), verticalArrangement=Arrangement.spacedBy(4.dp)){
            Text("Tips: use 12+ characters, mix upper/lower, digits, symbols.", style=MaterialTheme.typography.bodySmall)
            Text("Do not reuse passwords.", style=MaterialTheme.typography.bodySmall)
        }}
    }
}
