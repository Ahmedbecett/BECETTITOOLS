package com.becetti.tools.features.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.data.local.entity.NoteEntity
import com.becetti.tools.data.repository.ToolRepository
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(app: BecettiToolsApp, nav: NavController){
    val repo = remember{ ToolRepository(app.database.favoriteDao(), app.database.historyDao(), app.database.recentToolDao(), app.database.noteDao()) }
    val notes by repo.notes().collectAsState(initial= emptyList())
    var query by remember{ mutableStateOf("") }
    val filtered = remember(notes, query){
        if(query.isBlank()) notes else notes.filter{ it.title.contains(query,true) || it.content.contains(query,true)}
    }
    var showDialog by remember{ mutableStateOf(false) }
    var editNote by remember{ mutableStateOf<NoteEntity?>(null) }
    val scope = rememberCoroutineScope()
    Scaffold(floatingActionButton={ FloatingActionButton(onClick={ editNote=null; showDialog=true }){ Icon(Icons.Filled.Add,null)} } ){ pad->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
            Text("Notes Manager", style=MaterialTheme.typography.titleLarge)
            OutlinedTextField(value=query, onValueChange={query=it}, label={Text("Search notes")}, leadingIcon={Icon(Icons.Filled.Search,null)}, modifier=Modifier.fillMaxWidth())
            LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){
                items(filtered){ n->
                    Card(modifier=Modifier.fillMaxWidth().clickable{ editNote=n; showDialog=true }){
                        ListItem(headlineContent={Text(n.title.ifEmpty{ "(No title)"})}, supportingContent={Text(n.content.take(100))}, trailingContent={ IconButton(onClick={ scope.launch{ repo.deleteNote(n) } }){ Icon(Icons.Filled.Delete,null)} })
                    }
                }
            }
        }
    }
    if(showDialog){
        var title by remember(editNote){ mutableStateOf(editNote?.title?:"") }
        var content by remember(editNote){ mutableStateOf(editNote?.content?:"") }
        AlertDialog(onDismissRequest={showDialog=false}, title={Text(if(editNote==null) "New Note" else "Edit Note")}, text={
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
                OutlinedTextField(value=title, onValueChange={title=it}, label={Text("Title")})
                OutlinedTextField(value=content, onValueChange={content=it}, label={Text("Content")}, modifier=Modifier.height(120.dp))
            }
        }, confirmButton={
            Button(onClick={ scope.launch{ repo.saveNote(NoteEntity(id=editNote?.id?:0, title=title, content=content)); showDialog=false } }){ Text("Save") }
        }, dismissButton={ OutlinedButton(onClick={showDialog=false}){ Text("Cancel") } })
    }
}
