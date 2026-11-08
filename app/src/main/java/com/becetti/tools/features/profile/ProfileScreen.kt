package com.becetti.tools.features.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.domain.models.ToolRegistry
import com.becetti.tools.features.settings.SettingsViewModel
import com.becetti.tools.features.tools.FilterPill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Profile & settings (spec §2): theme, language, motion, storage, privacy, activity and about.
 * The full v1 Settings screen (privacy policy, ad consent, history) remains reachable from here.
 */
@Composable
fun ProfileScreen(
    app: BecettiToolsApp,
    nav: NavController,
    settingsViewModel: SettingsViewModel
) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val lang by settingsViewModel.language.collectAsState()
    val theme by settingsViewModel.theme.collectAsState()
    val reduceMotion by settingsViewModel.reduceMotion.collectAsState()
    val favorites by app.toolRepository.favorites().collectAsState(initial = emptyList())
    val recents by app.toolRepository.recentTools().collectAsState(initial = emptyList())
    val activity by app.activityStore.entries.collectAsState()

    var storageBytes by remember { mutableStateOf(0L) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        storageBytes = withContext(Dispatchers.IO) { appDataSize(ctx) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        /* -------------------------------- profile header ----------------------------- */
        item {
            Row(Modifier.entrance(0), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, ToolAccents.Projects)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "BT",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(s.profileTitle, style = MaterialTheme.typography.titleLarge, color = palette.textPrimary)
                    Text(s.profileGuest, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text(s.profileGuestHint, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(
                    label = s.profileToolsAvailable,
                    value = "${ToolRegistry.all.size}",
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = s.profileFavoritesCount,
                    value = "${favorites.size}",
                    accent = ToolAccents.Media,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = s.profileStorageUsed,
                    value = FileExporter.humanSize(storageBytes),
                    accent = ToolAccents.Projects,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        /* --------------------------------- appearance -------------------------------- */
        item {
            Panel(accent = MaterialTheme.colorScheme.primary) {
                SectionHeader(s.profileAppearance)
                FieldLabel(s.profileTheme)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterPill(s.profileThemeSystem, theme == "system", MaterialTheme.colorScheme.primary) {
                        settingsViewModel.setTheme("system")
                    }
                    FilterPill(s.profileThemeDark, theme == "dark", MaterialTheme.colorScheme.primary) {
                        settingsViewModel.setTheme("dark")
                    }
                    FilterPill(s.profileThemeLight, theme == "light", MaterialTheme.colorScheme.primary) {
                        settingsViewModel.setTheme("light")
                    }
                }
                Spacer(Modifier.height(4.dp))
                FieldLabel(s.profileLanguage)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("en" to "English", "ar" to "العربية", "fr" to "Français").forEach { (code, label) ->
                        FilterPill(label, lang == code, MaterialTheme.colorScheme.secondary) {
                            settingsViewModel.setLanguage(code)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(s.profileReduceMotion, style = MaterialTheme.typography.labelLarge, color = palette.textPrimary)
                        Text(s.profileReduceMotionDesc, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                    }
                    Switch(
                        checked = reduceMotion,
                        onCheckedChange = { settingsViewModel.setReduceMotion(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        /* ----------------------------------- data ------------------------------------ */
        item {
            Panel(accent = ToolAccents.Projects) {
                SectionHeader(s.profileData)
                KeyValueRow(s.profileStorageUsed, FileExporter.humanSize(storageBytes))
                KeyValueRow(s.homeRecent, "${recents.size}")
                ActionButton(
                    text = s.profileClearCache,
                    onClick = {
                        scope.launch {
                            val freed = withContext(Dispatchers.IO) { FileExporter.clearTempFiles(ctx) }
                            storageBytes = withContext(Dispatchers.IO) { appDataSize(ctx) }
                            toast = "${s.profileCacheCleared} (${FileExporter.humanSize(freed)})"
                        }
                    },
                    accent = ToolAccents.Projects,
                    outlined = true,
                    icon = BecettiGlyphs.FolderOpen
                )
                ActionButton(
                    text = s.profileClearAllData,
                    onClick = { showDeleteDialog = true },
                    accent = palette.error,
                    outlined = true,
                    icon = BecettiGlyphs.ShieldCheck
                )
                if (toast.isNotEmpty()) {
                    SuccessTick(visible = true, message = toast, accent = palette.success)
                }
            }
        }

        /* ---------------------------------- activity --------------------------------- */
        item {
            Panel(accent = ToolAccents.Files) {
                SectionHeader(s.profileActivity)
                val rows = activity.take(8)
                if (rows.isEmpty() && recents.isEmpty()) {
                    Text(s.profileNoActivity, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                } else {
                    rows.forEach { entry ->
                        KeyValueRow(
                            label = entry.label,
                            value = FileExporter.humanDate(entry.at),
                            accent = palette.textPrimary
                        )
                    }
                    recents.take(5).forEach { recent ->
                        val spec = ToolRegistry.byId(recent.toolId)
                        if (spec != null) {
                            KeyValueRow(
                                label = s.groupName(spec.group.id),
                                value = FileExporter.humanDate(recent.lastUsed),
                                accent = palette.textSecondary
                            )
                        }
                    }
                }
            }
        }

        /* ---------------------------------- privacy ---------------------------------- */
        item {
            Panel(accent = ToolAccents.Network) {
                SectionHeader(s.profilePrivacy)
                InfoBanner(s.gLocalOnly, accent = ToolAccents.Business, icon = BecettiGlyphs.ShieldCheck)
                ActionButton(
                    text = s.navSettings,
                    onClick = { nav.navigate(Route.Settings.route) },
                    accent = MaterialTheme.colorScheme.primary,
                    outlined = true,
                    icon = Icons.Filled.Settings
                )
            }
        }

        /* ----------------------------------- about ----------------------------------- */
        item {
            Panel(accent = ToolAccents.Media) {
                SectionHeader(s.profileAbout)
                KeyValueRow("BECETTI TOOLS", s.profileVersion + " 2.0.0 (2)")
                KeyValueRow("Package", "com.becetti.tools")
                KeyValueRow("Min Android", "7.0 (API 24)")
                ActionButton(
                    text = "GitHub",
                    onClick = {
                        runCatching {
                            ctx.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Ahmedbecett/BECETTITOOLS"))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                    },
                    accent = ToolAccents.Media,
                    outlined = true,
                    icon = BecettiGlyphs.GithubMark
                )
            }
        }

        item {
            Text(
                "© 2026 BECETTI TOOLS · ${s.gLocalOnly}",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    ConfirmDialog(
        show = showDeleteDialog,
        title = s.profileClearAllData,
        message = s.profileClearAllConfirm,
        confirmLabel = s.delete,
        dismissLabel = s.cancel,
        accent = palette.error,
        onConfirm = {
            showDeleteDialog = false
            scope.launch {
                withContext(Dispatchers.IO) {
                    app.database.historyDao().clear()
                    app.database.clearAllTables()
                    app.projectStore.clearAll()
                    app.businessStore.clearAll()
                    app.activityStore.clear()
                }
                storageBytes = withContext(Dispatchers.IO) { appDataSize(ctx) }
                toast = s.gAllDataDeleted
            }
        },
        onDismiss = { showDeleteDialog = false }
    )
}

private fun appDataSize(ctx: android.content.Context): Long {
    fun sizeOf(dir: File?): Long = runCatching {
        if (dir == null || !dir.exists()) 0L
        else dir.walkBottomUp().filter { it.isFile }.sumOf { it.length() }
    }.getOrDefault(0L)
    return sizeOf(ctx.filesDir) + sizeOf(ctx.cacheDir) + sizeOf(File(ctx.applicationInfo.dataDir, "databases"))
}
