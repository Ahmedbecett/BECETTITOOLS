package com.becetti.tools.data.local.dao

import androidx.room.*
import com.becetti.tools.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC") fun getAll(): Flow<List<NoteEntity>>
    @Query("SELECT * FROM notes WHERE id=:id") suspend fun getById(id: Long): NoteEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(note: NoteEntity): Long
    @Delete suspend fun delete(note: NoteEntity)
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :q || '%' OR content LIKE '%' || :q || '%'") fun search(q: String): Flow<List<NoteEntity>>
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites") fun getAll(): Flow<List<FavoriteEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun add(fav: FavoriteEntity)
    @Query("DELETE FROM favorites WHERE toolId=:id") suspend fun remove(id: String)
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE toolId=:id)") fun isFav(id: String): Flow<Boolean>
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC LIMIT 100") fun getAll(): Flow<List<CalculationHistoryEntity>>
    @Insert suspend fun insert(e: CalculationHistoryEntity)
    @Query("DELETE FROM calculation_history") suspend fun clear()
}

@Dao
interface RecentToolDao {
    @Query("SELECT * FROM recent_tools ORDER BY lastUsed DESC") fun getAll(): Flow<List<RecentToolEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun markUsed(e: RecentToolEntity)
}
