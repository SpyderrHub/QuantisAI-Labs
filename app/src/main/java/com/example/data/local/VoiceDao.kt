package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.VoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Query("SELECT * FROM voices ORDER BY voiceName ASC")
    fun getAllVoices(): Flow<List<VoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoices(voices: List<VoiceEntity>)

    @Query("DELETE FROM voices")
    suspend fun clearAll()
    
    @Query("SELECT COUNT(*) FROM voices")
    suspend fun getVoicesCount(): Int
    
    @Transaction
    suspend fun replaceAll(voices: List<VoiceEntity>) {
        clearAll()
        insertVoices(voices)
    }
}
