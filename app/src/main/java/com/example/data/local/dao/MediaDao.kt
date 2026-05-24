package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.MediaItemEntity

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(media: MediaItemEntity)

    @Query("SELECT * FROM media_history ORDER BY lastPlayedAt DESC")
    suspend fun getHistory(): List<MediaItemEntity>
}
