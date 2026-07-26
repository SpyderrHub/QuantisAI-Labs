package com.example.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.VoiceRepository

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val voiceRepository = VoiceRepository(applicationContext)
        return try {
            voiceRepository.forceSync()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
