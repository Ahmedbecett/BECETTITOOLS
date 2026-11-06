package com.becetti.tools.features.settings

import android.app.Activity
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
    var showRestartDialog by remember { mutableStateOf(false) }
    var pendingLang by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Settings", style=MaterialTheme.typography.titleLarge)
        Text("Language")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
            listOf("en" to "English","fr" to "Français","ar" to "العربية").forEach{ (code,label)->
                FilterChip(selected=lang==code, onClick={
                    if (lang != code) {
                        vm.setLanguage(code)
                        pendingLang = code
                        showRestartDialog = true
                    }
                }, label={Text(label)})
            }
        }
        if(lang=="ar") Text("RTL enabled — layout mirrored for Arabic", style=MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        if (showRestartDialog) {
            AlertDialog(
                onDismissRequest = { showRestartDialog = false },
                title = { Text(when(pendingLang) { "ar" -> "تم تغيير اللغة" "fr" -> "Langue changée" else -> "Language changed" }) },
                text = { Text(when(pendingLang) { "ar" -> "سيتم إعادة تشغيل التطبيق لتطبيق العربية بشكل كامل" "fr" -> "L'application va redémarrer pour appliquer le français" else -> "App will restart to apply language" }) },
                confirmButton = {
                    Button(onClick = {
                        showRestartDialog = false
                        // Restart via intent
                        val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
                        intent?.let {
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            ctx.startActivity(it)
                            (ctx as? Activity)?.finishAffinity()
                            Runtime.getRuntime().exit(0)
                        }
                    }) { Text("Restart Now") }
                },
                dismissButton = { TextButton(onClick = { showRestartDialog = false }) { Text("Later") } }
            )
        }
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
        Text("Version 1.1.0 • Package com.becetti.tools • Everything you need. One smart toolbox.")
        Text("Huge Edition: + File Manager ZArchiver + Full file access + Share")
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
        Text("Open source: Room, DataStore, CameraX, ML Kit, ZXing, PDFBox-Android, Retrofit, Coil, CommonsCompress, Zip4j.", style=MaterialTheme.typography.bodySmall)
        Text("App version 1.1.0 (2) • com.becetti.tools", style=MaterialTheme.typography.labelSmall)
    }
}
