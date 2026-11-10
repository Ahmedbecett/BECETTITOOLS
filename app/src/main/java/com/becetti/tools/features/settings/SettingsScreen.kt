package com.becetti.tools.features.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.BecettiIcons
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolIconBadge
import com.becetti.tools.design.theme.*
import com.becetti.tools.monetization.AdsManager

@Composable
fun SettingsScreen(vm: SettingsViewModel, adsManager: AdsManager) {
    val ctx = LocalContext.current
    val activity = ctx as? Activity
    val s = LocalAppStrings.current
    val lang by vm.language.collectAsState()
    val theme by vm.theme.collectAsState()
    val privacyOptionsRequired by adsManager.consent.privacyOptionsRequired.collectAsState()
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var historyCleared by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Glowing Settings Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(1.5.dp, PrimaryOrange.copy(alpha = 0.65f)),
            colors = CardDefaults.cardColors(containerColor = DeepNavy)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                PrimaryOrange.copy(alpha = 0.28f),
                                SecondaryNavy,
                                DeepNavy
                            )
                        )
                    )
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrange.copy(alpha = 0.25f))
                        .border(1.5.dp, PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = s.settingsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = s.settingsSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                }
            }
        }

        // 1. Glowing Language Selector Section
        GlowingCard(accentColor = GlowCyan) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowingToolIconBadge(
                    icon = BecettiIcons.LanguageGlobe,
                    accentColor = GlowCyan,
                    size = 44.dp,
                    iconSize = 22.dp
                )
                Column {
                    Text(
                        s.settingsLanguage,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlowCyan
                    )
                    Text(s.settingsLanguageDesc, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Triple("ar", "العربية", "🇩🇿"),
                    Triple("fr", "Français", "🇫🇷"),
                    Triple("en", "English", "🇬🇧")
                ).forEach { (code, label, flag) ->
                    val selected = lang == code
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { vm.setLanguage(code) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) GlowCyan.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            if (selected) 2.dp else 1.dp,
                            if (selected) GlowCyan else GlowCyan.copy(alpha = 0.30f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(flag, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) GlowCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 2. Glowing Theme Selector Section
        GlowingCard(accentColor = GlowAmber) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowingToolIconBadge(
                    icon = BecettiIcons.SparklePalette,
                    accentColor = GlowAmber,
                    size = 44.dp,
                    iconSize = 22.dp
                )
                Column {
                    Text(
                        s.settingsTheme,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlowAmber
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Triple("light", s.settingsThemeLight, "☀️"),
                    Triple("dark", s.settingsThemeDark, "🌙"),
                    Triple("system", s.settingsThemeSystem, "⚙️")
                ).forEach { (code, label, emoji) ->
                    val selected = theme == code
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { vm.setTheme(code) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) GlowAmber.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            if (selected) 2.dp else 1.dp,
                            if (selected) GlowAmber else GlowAmber.copy(alpha = 0.30f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) GlowAmber else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 3. Glowing Privacy & Security Section
        GlowingCard(accentColor = GlowEmerald) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlowingToolIconBadge(
                    icon = BecettiIcons.ShieldSecurity,
                    accentColor = GlowEmerald,
                    size = 44.dp,
                    iconSize = 22.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        s.settingsPrivacySection,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlowEmerald
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(s.settingsAdsConsentTitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(s.settingsAdsConsentDesc, style = MaterialTheme.typography.bodySmall)
                }
                if (privacyOptionsRequired && activity != null) {
                    TextButton(onClick = { adsManager.showPrivacyOptions(activity) }) {
                        Text(s.settingsAdPrivacyOptionsBtn)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    vm.clearHistory()
                    historyCleared = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
            ) {
                Text(
                    if (historyCleared) s.settingsHistoryCleared else s.settingsClearHistoryBtn,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showPrivacyDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowViolet)
            ) {
                Text("🛡️ ${s.settingsPrivacyBtn}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // 4. Glowing About & Amazon Appstore Badge Section
        GlowingCard(accentColor = GlowViolet) {
            Text(
                s.settingsAboutSection,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlowViolet
            )
            Spacer(Modifier.height(6.dp))
            Text(s.settingsAboutDesc, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Surface(
                color = GlowOrange.copy(alpha = 0.16f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GlowOrange.copy(alpha = 0.55f))
            ) {
                Text(
                    text = "✓ ${s.settingsAmazonBadge}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = GlowOrange,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showPrivacyDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(s.settingsPrivacyBtn)
                }
                OutlinedButton(
                    onClick = {
                        runCatching {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/Ahmedbecett/BECETTITOOLS")
                            )
                            ctx.startActivity(intent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(s.settingsGithubBtn)
                }
            }
        }
    }

    // Glowing In-App Privacy Policy Modal Dialog (Fully Localized in AR/FR/EN)
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlowingToolIconBadge(
                        icon = BecettiIcons.ShieldSecurity,
                        accentColor = GlowEmerald,
                        size = 40.dp,
                        iconSize = 20.dp
                    )
                    Text(
                        s.privacyDialogTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlowEmerald
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(s.privacyIntro, style = MaterialTheme.typography.bodyMedium)

                    val sections = listOf(
                        Triple(s.privacyItem1Title, s.privacyItem1Body, GlowEmerald),
                        Triple(s.privacyItem2Title, s.privacyItem2Body, GlowCyan),
                        Triple(s.privacyItem3Title, s.privacyItem3Body, GlowViolet),
                        Triple(s.privacyItem4Title, s.privacyItem4Body, GlowOrange),
                        Triple(s.privacyItem5Title, s.privacyItem5Body, GlowAmber)
                    )
                    sections.forEach { (title, body, color) ->
                        Surface(
                            color = color.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.40f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = color,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(body, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GlowEmerald)
                ) {
                    Text(s.privacyCloseBtn, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showPrivacyDialog = false
                        runCatching {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/docs/PRIVACY_POLICY.md")
                            )
                            ctx.startActivity(intent)
                        }
                    }
                ) {
                    Text(s.privacyOpenWebBtn)
                }
            }
        )
    }
}
