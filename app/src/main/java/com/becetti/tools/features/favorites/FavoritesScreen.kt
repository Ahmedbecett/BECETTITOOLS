package com.becetti.tools.features.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.domain.models.ToolCatalog
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(app: BecettiToolsApp, nav: NavController){
    val repo = remember{ ToolRepository(app.database.favoriteDao(), app.database.historyDao(), app.database.recentToolDao(), app.database.noteDao()) }
    val favs by repo.favorites().collectAsState(initial= emptyList())
    val scope = rememberCoroutineScope()
    var showAll by remember{ mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("Favorites", style=MaterialTheme.typography.titleLarge)
        if(favs.isEmpty()) Text("No favorites yet. Tap star on tools to add.")
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(favs){ f->
                val tool = ToolCatalog.all.find{ it.route==f.toolId }
                Card(Modifier.fillMaxWidth().clickable{ tool?.let{ nav.navigate(it.route) } }){ ListItem(headlineContent={Text(tool?.name?:f.toolId)}, supportingContent={Text(tool?.description?:"")}) }
            }
        }
        Divider()
        Text("All Tools — tap to toggle favorite", style=MaterialTheme.typography.titleMedium)
        ToolCatalog.all.forEach{ tool->
            val isFav = favs.any{ it.toolId==tool.route }
            Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){
                ListItem(headlineContent={Text(tool.name)}, trailingContent={
                    IconButton(onClick={ scope.launch{ repo.toggleFavorite(tool.route, isFav) } }){ Text(if(isFav) "★" else "☆") }
                })
            }
        }
    }
}
