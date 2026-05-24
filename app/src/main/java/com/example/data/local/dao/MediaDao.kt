package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.HistoryEntity

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(media: HistoryEntity)

    @Query("SELECT * FROM playback_history ORDER BY lastPlayedAt DESC")
    suspend fun getHistory(): List<HistoryEntity>
}
