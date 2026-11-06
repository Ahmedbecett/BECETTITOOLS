package com.becetti.tools.data.repository

import com.becetti.tools.data.local.dao.*
import com.becetti.tools.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class ToolRepository(
    private val favDao: FavoriteDao,
    private val histDao: HistoryDao,
    private val recentDao: RecentToolDao,
    private val noteDao: NoteDao
) {
    fun favorites() = favDao.getAll()
    suspend fun toggleFavorite(toolId: String, isFav: Boolean) {
        if (isFav) favDao.remove(toolId) else favDao.add(FavoriteEntity(toolId))
    }
    fun history() = histDao.getAll()
    suspend fun addHistory(expr: String, res: String) = histDao.insert(CalculationHistoryEntity(expression = expr, result = res))
    suspend fun clearHistory() = histDao.clear()
    fun recentTools() = recentDao.getAll()
    suspend fun markRecent(toolId: String) = recentDao.markUsed(RecentToolEntity(toolId, System.currentTimeMillis()))

    // notes
    fun notes() = noteDao.getAll()
    fun searchNotes(q: String) = noteDao.search(q)
    suspend fun saveNote(e: NoteEntity) = noteDao.insert(e)
    suspend fun deleteNote(e: NoteEntity) = noteDao.delete(e)
    suspend fun getNote(id: Long) = noteDao.getById(id)
}
