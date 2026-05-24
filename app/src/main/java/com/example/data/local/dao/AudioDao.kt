package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Query("SELECT * FROM audios ORDER BY dateAdded DESC")
    fun getAllAudios(): Flow<List<AudioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudios(audios: List<AudioEntity>)

    @Query("DELETE FROM audios")
    suspend fun clearAll()
}
