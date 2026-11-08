package com.becetti.tools.data.local.store

import android.content.Context
import com.becetti.tools.core.utils.LocalStore
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/* ------------------------------------------ models ----------------------------------------- */

data class ProjectTask(
    val id: String = "",
    val title: String = "",
    val notes: String = "",
    val column: String = COLUMN_TODO,
    val dueAt: Long = 0L,
    val createdAt: Long = 0L,
    val priority: Int = 1
) {
    companion object {
        const val COLUMN_TODO = "todo"
        const val COLUMN_DOING = "doing"
        const val COLUMN_DONE = "done"
    }
}

data class BudgetEntry(
    val id: String = "",
    val label: String = "",
    val amount: Double = 0.0,
    val kind: String = "expense",
    val at: Long = 0L
) {
    val isExpense: Boolean get() = kind == "expense"
}

data class ProjectAttachment(
    val name: String = "",
    val uri: String = "",
    val size: Long = 0L,
    val addedAt: Long = 0L
)

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val deadlineAt: Long = 0L,
    val createdAt: Long = 0L,
    val notes: String = "",
    val archived: Boolean = false,
    val tasks: List<ProjectTask> = emptyList(),
    val budget: List<BudgetEntry> = emptyList(),
    val attachments: List<ProjectAttachment> = emptyList()
) {
    val doneCount: Int get() = tasks.count { it.column == ProjectTask.COLUMN_DONE }
    val openCount: Int get() = tasks.count { it.column != ProjectTask.COLUMN_DONE }
    val progress: Float get() = if (tasks.isEmpty()) 0f else doneCount.toFloat() / tasks.size
    val overdueCount: Int get() {
        val now = System.currentTimeMillis()
        return tasks.count { it.column != ProjectTask.COLUMN_DONE && it.dueAt in 1..now }
    }
    val totalExpenses: Double get() = budget.filter { it.isExpense }.sumOf { it.amount }
    val totalIncome: Double get() = budget.filter { !it.isExpense }.sumOf { it.amount }
    val balance: Double get() = totalIncome - totalExpenses
}

/* ------------------------------------------- store ----------------------------------------- */

/**
 * Project manager persistence: projects, kanban tasks, deadlines, budget lines, notes and attachment
 * references. Everything is stored locally as a single JSON document (see [LocalStore]).
 */
class ProjectStore(context: Context) {

    private val store = LocalStore<List<Project>>(
        context = context,
        fileName = "becetti_projects",
        type = LocalStore.listType(Project::class.java),
        empty = { emptyList() }
    )

    val projects: StateFlow<List<Project>> = store.data

    fun all(): List<Project> = store.current.sortedByDescending { it.createdAt }

    fun byId(id: String): Project? = store.current.firstOrNull { it.id == id }

    fun create(name: String, description: String, deadlineAt: Long): Project {
        val project = Project(
            id = UUID.randomUUID().toString(),
            name = name.trim().ifBlank { "Untitled project" },
            description = description.trim(),
            deadlineAt = deadlineAt,
            createdAt = System.currentTimeMillis()
        )
        store.mutate { it + project }
        return project
    }

    fun update(project: Project) {
        store.mutate { list -> list.map { if (it.id == project.id) project else it } }
    }

    fun delete(id: String) {
        store.mutate { list -> list.filterNot { it.id == id } }
    }

    fun archive(id: String, archived: Boolean) {
        update2(id) { it.copy(archived = archived) }
    }

    private fun update2(id: String, block: (Project) -> Project) {
        store.mutate { list -> list.map { if (it.id == id) block(it) else it } }
    }

    fun rename(id: String, name: String, description: String, deadlineAt: Long) = update2(id) {
        it.copy(name = name.trim().ifBlank { it.name }, description = description.trim(), deadlineAt = deadlineAt)
    }

    fun setNotes(id: String, notes: String) = update2(id) { it.copy(notes = notes) }

    fun addTask(projectId: String, title: String, dueAt: Long = 0L, priority: Int = 1) = update2(projectId) { project ->
        project.copy(
            tasks = project.tasks + ProjectTask(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                dueAt = dueAt,
                priority = priority,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun moveTask(projectId: String, taskId: String, column: String) = update2(projectId) { project ->
        project.copy(tasks = project.tasks.map { if (it.id == taskId) it.copy(column = column) else it })
    }

    fun deleteTask(projectId: String, taskId: String) = update2(projectId) { project ->
        project.copy(tasks = project.tasks.filterNot { it.id == taskId })
    }

    fun addBudget(projectId: String, label: String, amount: Double, kind: String) = update2(projectId) { project ->
        project.copy(
            budget = project.budget + BudgetEntry(
                id = UUID.randomUUID().toString(),
                label = label.trim(),
                amount = amount,
                kind = kind,
                at = System.currentTimeMillis()
            )
        )
    }

    fun deleteBudget(projectId: String, entryId: String) = update2(projectId) { project ->
        project.copy(budget = project.budget.filterNot { it.id == entryId })
    }

    fun addAttachment(projectId: String, name: String, uri: String, size: Long) = update2(projectId) { project ->
        project.copy(
            attachments = project.attachments + ProjectAttachment(
                name = name, uri = uri, size = size, addedAt = System.currentTimeMillis()
            )
        )
    }

    fun removeAttachment(projectId: String, uri: String) = update2(projectId) { project ->
        project.copy(attachments = project.attachments.filterNot { it.uri == uri })
    }

    fun clearAll() = store.delete()
}

/* ---------------------------------------- activity log ------------------------------------- */

data class ActivityEntry(
    val id: String = "",
    val kind: String = "tool",
    val label: String = "",
    val at: Long = 0L
)

/** Lightweight audit trail powering the "recent activity" surfaces. */
class ActivityStore(context: Context) {

    private val store = LocalStore<List<ActivityEntry>>(
        context = context,
        fileName = "becetti_activity",
        type = LocalStore.listType(ActivityEntry::class.java),
        empty = { emptyList() }
    )

    val entries: StateFlow<List<ActivityEntry>> = store.data

    fun log(kind: String, label: String) {
        store.mutate { current ->
            (listOf(
                ActivityEntry(UUID.randomUUID().toString(), kind, label, System.currentTimeMillis())
            ) + current).take(80)
        }
    }

    fun clear() = store.delete()
}
