package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_history")
data class HistoryEntity(
    @PrimaryKey val mediaId: String,
    val uri: String,
    val lastPositionMs: Long,
    val totalDurationMs: Long,
    val lastPlayedAt: Long
)
