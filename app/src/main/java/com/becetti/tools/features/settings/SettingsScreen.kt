package com.becetti.tools.features.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(vm: SettingsViewModel){
    val ctx = LocalContext.current
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/docs/PRIVACY_POLICY.md"))
                ctx.startActivity(intent)
            }){ Text("Privacy Policy") }
            OutlinedButton(onClick={
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Ahmedbecett/BECETTITOOLS"))
                ctx.startActivity(intent)
            }){ Text("GitHub") }
        }
        Text("Privacy policy available in docs/PRIVACY_POLICY.md and in-app.", style=MaterialTheme.typography.bodySmall)
        Text("Open source: Room, DataStore, CameraX, ML Kit, ZXing, PDFBox-Android, Retrofit, Coil.", style=MaterialTheme.typography.bodySmall)
        Text("App version 1.0.0 (1) • com.becetti.tools", style=MaterialTheme.typography.labelSmall)
    }
}
