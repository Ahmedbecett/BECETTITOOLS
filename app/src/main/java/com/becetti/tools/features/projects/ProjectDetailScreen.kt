package com.becetti.tools.features.projects

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.core.utils.Csv
import com.becetti.tools.core.utils.DateUtils
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.core.utils.PdfReport
import com.becetti.tools.data.local.store.Project
import com.becetti.tools.data.local.store.ProjectTask
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.features.tools.FilterPill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Project workspace: overview (edit, report export, lifecycle), kanban board with real task moves,
 * budget ledger and notes/attachments.
 */
@Composable
fun ProjectDetailScreen(app: BecettiToolsApp, nav: NavController, projectId: String) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val projects by app.projectStore.projects.collectAsState()
    val project = projects.firstOrNull { it.id == projectId }

    var tab by remember { mutableStateOf(0) }
    var toast by remember { mutableStateOf("") }
    var showDelete by remember { mutableStateOf(false) }

    if (project == null) {
        EmptyState(
            title = s.projNotFound,
            message = s.gEmpty,
            icon = BecettiGlyphs.Kanban,
            accent = ToolAccents.Projects,
            actionLabel = s.back,
            onAction = { nav.popBackStack() }
        )
        return
    }

    val tabs = listOf(s.projTabOverview, s.projTabBoard, s.projTabBudget, s.projTabNotes)

    Column(Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = tab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = ToolAccents.Projects,
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index },
                    text = {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (tab == index) ToolAccents.Projects else palette.textSecondary,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (tab) {
                0 -> OverviewTab(app, project, toast) { toast = it }
                1 -> BoardTab(app, project)
                2 -> BudgetTab(app, project)
                else -> NotesTab(app, project)
            }

            if (toast.isNotEmpty()) {
                SuccessTick(visible = true, message = toast, accent = palette.success)
            }

            if (tab == 0) {
                ActionButton(
                    text = if (project.archived) s.projUnarchive else s.projArchive,
                    onClick = { app.projectStore.archive(project.id, !project.archived) },
                    accent = ToolAccents.Files,
                    outlined = true,
                    icon = BecettiGlyphs.ArchiveZip
                )
                ActionButton(
                    text = s.delete,
                    onClick = { showDelete = true },
                    accent = palette.error,
                    outlined = true,
                    icon = Icons.Filled.Clear
                )
            }
        }
    }

    ConfirmDialog(
        show = showDelete,
        title = s.delete,
        message = s.projDeleteConfirm,
        confirmLabel = s.delete,
        dismissLabel = s.cancel,
        accent = palette.error,
        onConfirm = {
            showDelete = false
            app.projectStore.delete(project.id)
            nav.popBackStack()
        },
        onDismiss = { showDelete = false }
    )

    LaunchedEffect(project.id) {
        if (project.archived) toast = s.projArchived
    }
}

/* ------------------------------------------- overview -------------------------------------- */

@Composable
private fun OverviewTab(
    app: BecettiToolsApp,
    project: Project,
    toast: String,
    onToast: (String) -> Unit
) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val ctx = LocalContext.current
    var name by remember(project.id) { mutableStateOf(project.name) }
    var description by remember(project.id) { mutableStateOf(project.description) }
    var deadline by remember(project.id) { mutableStateOf(if (project.deadlineAt > 0) DateUtils.toIsoDate(project.deadlineAt) else "") }

    Panel(accent = ToolAccents.Projects) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(project.name, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary, modifier = Modifier.weight(1f))
            Text("${(project.progress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = ToolAccents.Projects)
        }
        LinearProgressIndicator(
            progress = { project.progress },
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(4.dp)),
            color = ToolAccents.Projects,
            trackColor = ToolAccents.Projects.copy(alpha = 0.16f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(s.projTasks, "${project.tasks.size}", ToolAccents.Files, Modifier.weight(1f))
            StatTile(s.projDone, "${project.doneCount}", ToolAccents.Business, Modifier.weight(1f))
            StatTile(s.projOverdue, "${project.overdueCount}", palette.error, Modifier.weight(1f))
        }
        if (project.deadlineAt > 0L) DeadlineChip(project.deadlineAt)
        KeyValueRow(s.projEarned, formatMoney(project.totalIncome), accent = ToolAccents.Business)
        KeyValueRow(s.projSpent, formatMoney(project.totalExpenses), accent = palette.error)
        KeyValueRow(s.projBalance, formatMoney(project.balance), accent = if (project.balance >= 0) ToolAccents.Business else palette.error)
    }

    Panel(accent = ToolAccents.Files) {
        SectionHeader(s.gEdit)
        BecettiField(value = name, onValueChange = { name = it }, label = s.projName, accent = ToolAccents.Projects)
        BecettiField(value = description, onValueChange = { description = it }, label = s.projDescription, singleLine = false, minLines = 2, accent = ToolAccents.Projects)
        BecettiField(value = deadline, onValueChange = { deadline = it }, label = s.projDeadline, placeholder = "2026-12-31", supporting = s.projDateHint, accent = ToolAccents.Projects)
        ActionButton(
            text = s.projSaveChanges,
            onClick = {
                app.projectStore.rename(project.id, name, description, DateUtils.parseIsoDate(deadline) ?: 0L)
                onToast(s.gSaved)
            },
            accent = ToolAccents.Projects
        )
    }

    Panel(accent = ToolAccents.Media) {
        SectionHeader(s.gExport)
        ActionButton(
            text = s.projExportCsv,
            onClick = {
                val rows = mutableListOf<List<Any?>>(
                    listOf("Project", "Task", "Stage", "Due", "Priority"),
                )
                project.tasks.forEach { task ->
                    rows.add(
                        listOf(
                            project.name,
                            task.title,
                            task.column,
                            if (task.dueAt > 0) DateUtils.toIsoDate(task.dueAt) else "",
                            task.priority
                        )
                    )
                }
                rows.add(listOf("Budget line", "Amount", "Kind"))
                project.budget.forEach { entry -> rows.add(listOf(entry.label, entry.amount, entry.kind)) }
                val csv = Csv.document(rows)
                val path = runCatching {
                    FileExporter.saveText(ctx, "project_${project.id.take(6)}.csv", "text/csv", csv)
                }.getOrElse { "error: ${it.message}" }
                onToast("${s.savedPathPrefix} $path")
            },
            accent = ToolAccents.Media,
            outlined = true,
            icon = BecettiGlyphs.Invoice
        )
        ActionButton(
            text = s.projExportPdf,
            onClick = {
                val lines = mutableListOf<String>()
                lines.add("#${project.name}")
                if (project.description.isNotBlank()) lines.add(project.description)
                lines.add("")
                lines.add("#${s.gDetails}")
                lines.add("${s.projTasks}: ${project.tasks.size} · ${s.projDone}: ${project.doneCount}")
                lines.add("${s.projEarned}: ${formatMoney(project.totalIncome)}")
                lines.add("${s.projSpent}: ${formatMoney(project.totalExpenses)}")
                lines.add("${s.projBalance}: ${formatMoney(project.balance)}")
                lines.add("")
                lines.add("#${s.projTabBoard}")
                project.tasks.forEach { task ->
                    lines.add("• [${task.column}] ${task.title}")
                }
                lines.add("")
                lines.add("#${s.projTabBudget}")
                project.budget.forEach { entry ->
                    lines.add("• ${entry.label} — ${formatMoney(entry.amount)} (${if (entry.isExpense) s.projExpense else s.projIncome})")
                }
                val path = runCatching {
                    PdfReport.exportTextPdf(ctx, s.projReportTitle, lines, "project_${project.id.take(6)}.pdf")
                }.getOrElse { "error: ${it.message}" }
                onToast("${s.savedPathPrefix} $path")
            },
            accent = ToolAccents.Media,
            outlined = true,
            icon = BecettiGlyphs.Invoice
        )
    }
}

/* --------------------------------------------- board -------------------------------------- */

@Composable
private fun BoardTab(app: BecettiToolsApp, project: Project) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    var newTask by remember { mutableStateOf("") }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.weight(1f)) {
            BecettiField(
                value = newTask,
                onValueChange = { newTask = it },
                placeholder = s.projTaskHint,
                accent = ToolAccents.Projects
            )
        }
        IconButton(
            onClick = {
                if (newTask.isNotBlank()) {
                    app.projectStore.addTask(project.id, newTask)
                    newTask = ""
                }
            },
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ToolAccents.Projects)
        ) {
            Icon(Icons.Filled.Add, contentDescription = s.projNewTask, tint = androidx.compose.ui.graphics.Color.White)
        }
    }

    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val columns = listOf(
            ProjectTask.COLUMN_TODO to s.projTodo,
            ProjectTask.COLUMN_DOING to s.projDoing,
            ProjectTask.COLUMN_DONE to s.projDone
        )
        columns.forEach { (columnId, label) ->
            val tasks = project.tasks.filter { it.column == columnId }
            Column(
                Modifier
                    .width(232.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.chipSurface)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, style = MaterialTheme.typography.labelLarge, color = ToolAccents.Projects)
                    Text("${tasks.size}", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                }
                if (tasks.isEmpty()) {
                    Text(s.gEmpty, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                }
                tasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onAdvance = {
                            val next = when (task.column) {
                                ProjectTask.COLUMN_TODO -> ProjectTask.COLUMN_DOING
                                ProjectTask.COLUMN_DOING -> ProjectTask.COLUMN_DONE
                                else -> ProjectTask.COLUMN_TODO
                            }
                            app.projectStore.moveTask(project.id, task.id, next)
                        },
                        onDelete = { app.projectStore.deleteTask(project.id, task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: ProjectTask, onAdvance: () -> Unit, onDelete: () -> Unit) {
    val palette = BecettiTheme.palette
    val s = LocalAppStrings.current
    val done = task.column == ProjectTask.COLUMN_DONE
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            task.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (done) palette.textSecondary else palette.textPrimary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        if (task.dueAt > 0L) DeadlineChip(task.dueAt)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (done) s.projDone else s.projMoveNext,
                style = MaterialTheme.typography.labelSmall,
                color = ToolAccents.Projects,
                modifier = Modifier.clickable(onClick = onAdvance)
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Clear, contentDescription = s.delete, tint = palette.textSecondary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/* --------------------------------------------- budget ------------------------------------- */

@Composable
private fun BudgetTab(app: BecettiToolsApp, project: Project) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    var label by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf("expense") }
    var error by remember { mutableStateOf(false) }

    Panel(accent = ToolAccents.Business) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(s.projEarned, formatMoney(project.totalIncome), ToolAccents.Business, Modifier.weight(1f))
            StatTile(s.projSpent, formatMoney(project.totalExpenses), palette.error, Modifier.weight(1f))
            StatTile(s.projBalance, formatMoney(project.balance), ToolAccents.Projects, Modifier.weight(1f))
        }
        BecettiField(value = label, onValueChange = { label = it; error = false }, label = s.projAddBudget, accent = ToolAccents.Business)
        BecettiField(
            value = amount,
            onValueChange = { amount = it; error = false },
            label = s.projAmount,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
            isError = error,
            accent = ToolAccents.Business
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterPill(s.projExpense, kind == "expense", palette.error) { kind = "expense" }
            FilterPill(s.projIncome, kind == "income", ToolAccents.Business) { kind = "income" }
        }
        ActionButton(
            text = s.gAdd,
            onClick = {
                val value = amount.replace(',', '.').toDoubleOrNull()
                if (label.isBlank() || value == null || value <= 0.0) {
                    error = true
                } else {
                    app.projectStore.addBudget(project.id, label, value, kind)
                    label = ""
                    amount = ""
                }
            },
            accent = ToolAccents.Business
        )
    }

    Panel(accent = ToolAccents.Files) {
        SectionHeader("${s.projTabBudget} (${project.budget.size})")
        if (project.budget.isEmpty()) {
            Text(s.gEmpty, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
        } else {
            project.budget.sortedByDescending { it.at }.forEach { entry ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        entry.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        (if (entry.isExpense) "−" else "+") + formatMoney(entry.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (entry.isExpense) palette.error else ToolAccents.Business
                    )
                    IconButton(
                        onClick = { app.projectStore.deleteBudget(project.id, entry.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = s.delete, tint = palette.textSecondary, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

/* --------------------------------------------- notes -------------------------------------- */

@Composable
private fun NotesTab(app: BecettiToolsApp, project: Project) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val ctx = LocalContext.current
    var notes by remember(project.id) { mutableStateOf(project.notes) }
    var saved by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            val name = FileExporter.displayName(ctx, uri)
            val size = FileExporter.sizeOf(ctx, uri)
            app.projectStore.addAttachment(project.id, name, uri.toString(), size)
        }
    }

    Panel(accent = ToolAccents.Files) {
        SectionHeader(s.projTabNotes)
        BecettiField(
            value = notes,
            onValueChange = { notes = it; saved = false },
            placeholder = s.projNotesHint,
            singleLine = false,
            minLines = 5,
            maxLines = 14,
            accent = ToolAccents.Files
        )
        ActionButton(
            text = s.save,
            onClick = {
                app.projectStore.setNotes(project.id, notes)
                saved = true
            },
            accent = ToolAccents.Files
        )
        SuccessTick(visible = saved, message = s.gSaved, accent = palette.success)
    }

    Panel(accent = ToolAccents.Media) {
        SectionHeader("${s.projFiles} (${project.attachments.size})")
        ActionButton(
            text = s.projAttach,
            onClick = { picker.launch(arrayOf("*/*")) },
            accent = ToolAccents.Media,
            outlined = true,
            icon = BecettiGlyphs.FolderOpen
        )
        if (project.attachments.isEmpty()) {
            Text(s.gNoFileSelected, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
        } else {
            project.attachments.forEach { attachment ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(BecettiGlyphs.FolderOpen, contentDescription = null, tint = ToolAccents.Media, modifier = Modifier.size(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(attachment.name, style = MaterialTheme.typography.bodySmall, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(FileExporter.humanSize(attachment.size), style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    }
                    IconButton(
                        onClick = { app.projectStore.removeAttachment(project.id, attachment.uri) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = s.delete, tint = palette.textSecondary, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}
