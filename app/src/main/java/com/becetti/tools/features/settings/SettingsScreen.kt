package com.becetti.tools.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

@Composable
fun SettingsScreen(vm: SettingsViewModel){
    val lang by vm.language.collectAsState()
    val theme by vm.theme.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Settings", style=MaterialTheme.typography.titleLarge)
        Text("Language")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            listOf("en" to "English","fr" to "Français","ar" to "العربية").forEach{ (code,label)->
                FilterChip(selected=lang==code, onClick={ vm.setLanguage(code) }, label={Text(label)})
            }
        }
        if(lang=="ar") Text("RTL layout will activate on restart (android:supportsRtl=true).", style=MaterialTheme.typography.bodySmall)
        Text("Theme")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            listOf("light","dark","system").forEach{ t-> FilterChip(selected=theme==t, onClick={vm.setTheme(t)}, label={Text(t)}) }
        }
        Divider()
        Text("Privacy & Ads", style=MaterialTheme.typography.titleMedium)
        Text("Advertising SDKs: Monetag & Adsterra SmartLink (optional Sponsored Offers). No ads on calculator. Consent required for personalized ads.")
        Button(onClick={ vm.clearHistory() }, modifier=Modifier.fillMaxWidth()){ Text("Clear History") }
        Divider()
        Text("About BECETTI TOOLS", style=MaterialTheme.typography.titleMedium)
        Text("Version 1.0.0 • Package com.becetti.tools • Everything you need. One smart toolbox.")
        Text("Feedback: contact@becetti.tools")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            OutlinedButton(onClick={
                val c = LocalContext.current
                // placeholder - cannot access LocalContext here easily, navigate via intent in real app
            }){ Text("Privacy Policy") }
        }
        Text("Privacy policy available in docs/PRIVACY_POLICY.md and in-app.", style=MaterialTheme.typography.bodySmall)
        Text("Open source libs: Room, DataStore, CameraX, ML Kit, ZXing, PDFBox-Android, Retrofit, Coil.", style=MaterialTheme.typography.bodySmall)
    }
}
