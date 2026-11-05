package com.becetti.tools.data.local.database

import androidx.room.*
import com.becetti.tools.data.local.dao.*
import com.becetti.tools.data.local.entity.*

@Database(entities = [NoteEntity::class, FavoriteEntity::class, CalculationHistoryEntity::class, RecentToolEntity::class], version = 1, exportSchema = false)
abstract class BecettiDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
    abstract fun recentToolDao(): RecentToolDao
}
