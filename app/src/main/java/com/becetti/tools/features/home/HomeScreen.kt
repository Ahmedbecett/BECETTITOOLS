package com.becetti.tools.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.R
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.AdPlaceholder
import com.becetti.tools.design.components.BecettiCard
import com.becetti.tools.domain.models.ToolCatalog
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(app: BecettiToolsApp, nav: NavController) {
    val repo = remember { ToolRepository(app.database.favoriteDao(), app.database.historyDao(), app.database.recentToolDao(), app.database.noteDao()) }
    val recents by repo.recentTools().collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("BECETTI TOOLS", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.tagline), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(stringResource(R.string.search_hint)) }, leadingIcon = { Icon(Icons.Filled.Search, null) })
        }
        item {
            if (query.isBlank()) {
                Text("Favorites & Recents", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    recents.take(3).forEach { r ->
                        AssistChip(onClick = { nav.navigate(r.toolId) }, label = { Text(r.toolId) })
                    }
                }
                if (recents.isEmpty()) Text("No recent tools yet", style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            CategoryGrid(nav)
        }
        item {
            Text("All Tools (16+)", style = MaterialTheme.typography.titleMedium)
            val filtered = if (query.isBlank()) ToolCatalog.all else ToolCatalog.all.filter { it.name.contains(query,true) || it.description.contains(query,true)}
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { tool ->
                    BecettiCard(modifier = Modifier.clickable {
                        scope.launch { repo.markRecent(tool.route) }
                        nav.navigate(tool.route)
                    }) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(tool.name, style = MaterialTheme.typography.titleMedium); Text(tool.description, style = MaterialTheme.typography.bodySmall) }
                            Icon(Icons.Filled.ChevronRight, null)
                        }
                    }
                }
            }
        }
        item { AdPlaceholder(modifier = Modifier.clickable { app.adsManager.adsterra.openSponsoredOffers() }) }
    }
}

@Composable
fun CategoryGrid(nav: NavController) {
    val cats = listOf(
        "Calculator & Converters" to Route.UnitConverter.route,
        "QR & Security" to Route.QrGenerator.route,
        "Documents & Files" to Route.ImageToPdf.route,
        "Text Utilities" to Route.TextToolkit.route,
        "Image Utilities" to Route.ImageCompressor.route,
        "Date & Time" to Route.AgeCalculator.route,
        "File Manager (ZArchiver)" to Route.FileManager.route
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cats.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (name, route) ->
                    Card(modifier = Modifier.weight(1f).clickable { nav.navigate(route) },
                        colors = CardDefaults.cardColors(containerColor = if (name.contains("ZArchiver")) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    ) {
                        Box(Modifier.padding(16.dp)) {
                            Text(name, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun CategoriesScreen(nav: NavController) {
    val cats = mapOf(
        "Calculator & Converters" to listOf("calc_scientific","unit_converter","currency","percentage"),
        "QR & Security" to listOf("qr_generator","qr_scanner","password_gen","password_strength"),
        "Documents & Files" to listOf("image_to_pdf","pdf_merger","ocr"),
        "Text Utilities" to listOf("text_toolkit","notes"),
        "Image Utilities" to listOf("image_compressor"),
        "Date & Time" to listOf("age"),
        "File Manager" to listOf("file_manager")
    )
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cats.forEach { (cat, ids) ->
            item { Text(cat, style = MaterialTheme.typography.titleLarge) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ids.forEach { id ->
                        val tool = com.becetti.tools.domain.models.ToolCatalog.all.find { it.id == id } ?: return@forEach
                        Card(modifier = Modifier.fillMaxWidth().clickable { nav.navigate(tool.route) }) {
                            ListItem(headlineContent = { Text(tool.name) }, supportingContent = { Text(tool.description) }, trailingContent = { Icon(Icons.Filled.ArrowForward, null) })
                        }
                    }
                }
            }
        }
    }
}
