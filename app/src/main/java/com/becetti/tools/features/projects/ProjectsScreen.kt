package com.becetti.tools.features.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.core.utils.DateUtils
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.data.local.store.Project
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.features.tools.FilterPill

/** Project manager landing page: portfolio stats, live project cards and creation flow. */
@Composable
fun ProjectsScreen(app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val projects by app.projectStore.projects.collectAsState()
    var showCreate by remember { mutableStateOf(false) }
    var showArchived by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    val visible = projects.filter { it.archived == showArchived }.sortedByDescending { it.createdAt }
    val active = projects.count { !it.archived }
    val openTasks = projects.filter { !it.archived }.sumOf { it.openCount }
    val overdue = projects.filter { !it.archived }.sumOf { it.overdueCount }
    val balance = projects.filter { !it.archived }.sumOf { it.balance }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(Modifier.entrance(0), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(s.toolName("projects"), style = MaterialTheme.typography.headlineSmall, color = palette.textPrimary)
                Text(
                    s.homeProjectsCount.replaceFirstChar { it.uppercase() } + " · ${projects.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile("${s.homeOpenProjects}", "$active", ToolAccents.Projects, Modifier.weight(1f))
                StatTile(s.gOpen, "$openTasks", ToolAccents.Files, Modifier.weight(1f))
                StatTile(s.projOverdue, "$overdue", palette.error, Modifier.weight(1f))
                StatTile(s.projBalance, formatMoney(balance), ToolAccents.Business, Modifier.weight(1f))
            }
        }

        item {
            ActionButton(
                text = s.projNew,
                onClick = { showCreate = true },
                accent = ToolAccents.Projects,
                icon = BecettiGlyphs.Kanban
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterPill(s.projActiveFilter, !showArchived, ToolAccents.Projects) { showArchived = false } }
                item { FilterPill(s.projArchived, showArchived, ToolAccents.Files) { showArchived = true } }
            }
        }

        if (visible.isEmpty()) {
            item {
                EmptyState(
                    title = if (showArchived) s.projNoArchived else s.homeNoProjects,
                    message = s.projEmptyHint,
                    icon = BecettiGlyphs.Kanban,
                    accent = ToolAccents.Projects,
                    actionLabel = if (showArchived) null else s.projNew,
                    onAction = if (showArchived) null else ({ showCreate = true })
                )
            }
        } else {
            items(visible, key = { it.id }) { project ->
                ProjectCard(project = project) {
                    nav.navigate(Route.ProjectDetail.create(project.id))
                }
            }
        }
    }

    BecettiSheet(show = showCreate, onDismiss = { showCreate = false }, title = s.projNew) {
        BecettiField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = s.projName,
            placeholder = s.projNameHint,
            isError = nameError,
            supporting = if (nameError) s.gInvalidInput else null,
            accent = ToolAccents.Projects
        )
        BecettiField(
            value = description,
            onValueChange = { description = it },
            label = s.projDescription,
            singleLine = false,
            minLines = 2,
            accent = ToolAccents.Projects
        )
        BecettiField(
            value = deadline,
            onValueChange = { deadline = it },
            label = s.projDeadline,
            placeholder = "2026-12-31",
            supporting = s.projDateHint,
            accent = ToolAccents.Projects
        )
        ActionButton(
            text = s.gAdd,
            onClick = {
                if (name.isBlank()) {
                    nameError = true
                } else {
                    val created = app.projectStore.create(name, description, DateUtils.parseIsoDate(deadline) ?: 0L)
                    app.activityStore.log("project", "${s.projCreated}: ${created.name}")
                    name = ""
                    description = ""
                    deadline = ""
                    showCreate = false
                    nav.navigate(Route.ProjectDetail.create(created.id))
                }
            },
            accent = ToolAccents.Projects
        )
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    Panel(accent = ToolAccents.Projects, onClick = onClick) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    project.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (project.description.isNotBlank()) {
                    Text(
                        project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                "${(project.progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                color = ToolAccents.Projects
            )
        }
        LinearProgressIndicator(
            progress = { project.progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = ToolAccents.Projects,
            trackColor = ToolAccents.Projects.copy(alpha = 0.16f)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "${project.doneCount}/${project.tasks.size} · ${s.projTasks}",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
            if (project.overdueCount > 0) {
                Text(
                    "${project.overdueCount} ${s.projOverdue}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.error
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (project.deadlineAt > 0L) {
                DeadlineChip(project.deadlineAt)
            }
            if (project.budget.isNotEmpty()) {
                Text(
                    "${s.projBalance}: ${formatMoney(project.balance)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (project.balance >= 0) ToolAccents.Business else palette.error
                )
            }
            if (project.attachments.isNotEmpty()) {
                Text(
                    "${project.attachments.size} ${s.projFiles}",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textSecondary
                )
            }
        }
    }
}

@Composable
fun DeadlineChip(deadlineAt: Long) {
    val palette = BecettiTheme.palette
    val days = DateUtils.daysUntil(deadlineAt)
    val color = when {
        days < 0 -> palette.error
        days <= 3 -> palette.warning
        else -> palette.textSecondary
    }
    Text(
        text = "${DateUtils.humanDate(deadlineAt)} · ${if (days < 0) "${-days}d ${LocalAppStrings.current.projLate}" else "${days}d"}",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

fun formatMoney(value: Double): String = String.format(java.util.Locale.US, "%.2f", value)
