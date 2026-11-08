package com.becetti.tools.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.*
import com.becetti.tools.domain.models.ToolCatalog
import com.becetti.tools.domain.models.ToolCategory
import com.becetti.tools.features.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    app: BecettiToolsApp,
    nav: NavController,
    settingsViewModel: SettingsViewModel
) {
    val s = LocalAppStrings.current
    val lang by settingsViewModel.language.collectAsState()
    val theme by settingsViewModel.theme.collectAsState()
    val repo = remember {
        ToolRepository(
            app.database.favoriteDao(),
            app.database.historyDao(),
            app.database.recentToolDao(),
            app.database.noteDao()
        )
    }
    val recents by repo.recentTools().collectAsState(initial = emptyList())
    val favorites by repo.favorites().collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ToolCategory?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Glowing Hero Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, PrimaryOrange.copy(alpha = 0.65f)),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    PrimaryOrange.copy(alpha = 0.28f),
                                    SecondaryNavy,
                                    DeepNavy
                                )
                            )
                        )
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PrimaryOrange.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, PrimaryOrange)
                        ) {
                            Text(
                                text = "✦ ${s.heroBadge}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Instant Language Switcher Pills right in the Hero Banner
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("ar" to "العربية", "fr" to "FR", "en" to "EN").forEach { (code, label) ->
                                val active = lang == code
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { settingsViewModel.setLanguage(code) },
                                    color = if (active) PrimaryOrange else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(
                                        1.dp,
                                        if (active) Color.White else Color.White.copy(alpha = 0.25f)
                                    )
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            // Quick Theme Toggle
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        val next = if (theme == "dark") "light" else "dark"
                                        settingsViewModel.setTheme(next)
                                    },
                                color = Color.White.copy(alpha = 0.14f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = if (theme == "dark") "☀️" else "🌙",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = s.appName,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = s.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = GlowEmerald.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GlowEmerald.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "⚡ ${s.toolsCountLabel}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = GlowEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            color = GlowCyan.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GlowCyan.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "🛡️ ${s.offlineReadyLabel}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = GlowCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(s.searchHint, color = Color.White.copy(alpha = 0.65f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = s.searchHint,
                                tint = PrimaryOrange
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.30f)
                        )
                    )
                }
            }
        }

        // Favorites & Quick Access Strip
        if (query.isBlank()) {
            item {
                Text(
                    s.favoritesAndRecents,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                val quickRouteIds = (favorites.map { it.toolId } + recents.map { it.toolId }).distinct().take(6)
                if (quickRouteIds.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        quickRouteIds.forEach { routeId ->
                            val tool = ToolCatalog.all.find { it.route == routeId }
                            val accent = BecettiIcons.colorForTool(tool?.id ?: routeId)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        scope.launch { repo.markRecent(routeId) }
                                        nav.navigate(routeId)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = accent.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = BecettiIcons.iconForTool(tool?.id ?: routeId),
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = tool?.localizedName(s) ?: routeId,
                                        color = accent,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(s.noRecentsYet, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Glowing Category Filter Chips
        item {
            Text(
                s.categoriesTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            val categoryOptions = listOf(
                Triple(null, s.allCategoriesFilter, PrimaryOrange),
                Triple(ToolCategory.CALCULATOR_CONVERTERS, s.catCalcConverters, GlowOrange),
                Triple(ToolCategory.QR_SECURITY, s.catQrSecurity, GlowViolet),
                Triple(ToolCategory.DOCUMENTS_FILES, s.catDocumentsFiles, GlowRose),
                Triple(ToolCategory.TEXT_UTILITIES, s.catTextUtilities, GlowBlue),
                Triple(ToolCategory.IMAGE_UTILITIES, s.catImageUtilities, GlowAmber),
                Triple(ToolCategory.DATE_TIME, s.catDateTimeHealth, GlowTeal)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                categoryOptions.forEach { (cat, label, color) ->
                    val selected = selectedCategory == cat
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = if (selected && cat != null) null else cat },
                        label = {
                            Text(
                                label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.20f),
                            selectedLabelColor = color
                        )
                    )
                }
            }
        }

        // All Tools Glowing Cards
        item {
            Text(
                s.allToolsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            val filtered = ToolCatalog.all.filter { tool ->
                val matchesCat = selectedCategory == null || tool.category == selectedCategory
                val localName = tool.localizedName(s)
                val localDesc = tool.localizedDescription(s)
                val matchesQuery = query.isBlank() ||
                    localName.contains(query, ignoreCase = true) ||
                    localDesc.contains(query, ignoreCase = true) ||
                    tool.name.contains(query, ignoreCase = true)
                matchesCat && matchesQuery
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filtered.forEach { tool ->
                    val accent = BecettiIcons.colorForTool(tool.id)
                    val icon = BecettiIcons.iconForTool(tool.id)
                    val isFav = favorites.any { it.toolId == tool.route }

                    GlowingCard(
                        accentColor = accent,
                        onClick = {
                            scope.launch { repo.markRecent(tool.route) }
                            nav.navigate(tool.route)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            GlowingToolIconBadge(
                                icon = icon,
                                accentColor = accent,
                                size = 50.dp,
                                iconSize = 25.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tool.localizedName(s),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = tool.localizedDescription(s),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = {
                                    scope.launch { repo.toggleFavorite(tool.route, isFav) }
                                }
                            ) {
                                Text(
                                    text = if (isFav) "★" else "☆",
                                    color = if (isFav) GlowAmber else TextGray,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Icon(
                                imageVector = BecettiIcons.ChevronRight,
                                contentDescription = null,
                                tint = accent
                            )
                        }
                    }
                }
            }
        }

        if (app.adsManager.adsterra.isAvailable()) {
            item {
                AdPlaceholder(
                    modifier = Modifier.clickable { app.adsManager.adsterra.openSponsoredOffers() }
                )
            }
        }
    }
}

@Composable
fun CategoriesScreen(nav: NavController) {
    val s = LocalAppStrings.current
    val categoryGroups = listOf(
        Triple(s.catCalcConverters, GlowOrange, listOf("calc_scientific", "unit_converter", "currency", "percentage")),
        Triple(s.catQrSecurity, GlowViolet, listOf("qr_generator", "qr_scanner", "password_gen", "password_strength")),
        Triple(s.catDocumentsFiles, GlowRose, listOf("image_to_pdf", "pdf_merger", "ocr")),
        Triple(s.catTextUtilities, GlowBlue, listOf("text_toolkit", "notes")),
        Triple(s.catImageUtilities, GlowAmber, listOf("image_compressor")),
        Triple(s.catDateTimeHealth, GlowTeal, listOf("age", "bmi_calculator", "stopwatch_timer", "device_info"))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        categoryGroups.forEach { (catTitle, catColor, ids) ->
            item {
                Surface(
                    color = catColor.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, catColor.copy(alpha = 0.50f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✦ $catTitle",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = catColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ids.forEach { id ->
                        val tool = ToolCatalog.all.find { it.id == id } ?: return@forEach
                        val accent = BecettiIcons.colorForTool(tool.id)
                        val icon = BecettiIcons.iconForTool(tool.id)
                        GlowingCard(
                            accentColor = accent,
                            onClick = { nav.navigate(tool.route) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GlowingToolIconBadge(icon = icon, accentColor = accent, size = 46.dp, iconSize = 23.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tool.localizedName(s),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = tool.localizedDescription(s),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Icon(BecettiIcons.ChevronRight, contentDescription = null, tint = accent)
                            }
                        }
                    }
                }
            }
        }
    }
}
