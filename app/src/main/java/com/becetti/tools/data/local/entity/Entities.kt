package com.becetti.tools.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val content: String, val updatedAt: Long = System.currentTimeMillis())

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val toolId: String, val addedAt: Long = System.currentTimeMillis())

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val expression: String, val result: String, val timestamp: Long = System.currentTimeMillis())

@Entity(tableName = "recent_tools")
data class RecentToolEntity(@PrimaryKey val toolId: String, val lastUsed: Long = System.currentTimeMillis())
