package com.example.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.VoiceDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VoiceRepository(context: Context) {
    private val firestoreRepository = FirestoreRepository()
    private val voiceDao: VoiceDao = AppDatabase.getDatabase(context).voiceDao()
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val sharedPrefs = context.getSharedPreferences("cache_prefs", Context.MODE_PRIVATE)
    
    // Configurable TTL (7 days)
    private val CACHE_TTL_MS = 7L * 24L * 60L * 60L * 1000L

    val allVoices: Flow<List<VoiceEntity>> = voiceDao.getAllVoices()

    suspend fun syncVoicesIfNeeded() {
        val lastSync = sharedPrefs.getLong("last_voice_sync", 0L)
        val now = System.currentTimeMillis()
        
        val localCount = withContext(Dispatchers.IO) { voiceDao.getVoicesCount() }
        
        val needsSync = (now - lastSync) > CACHE_TTL_MS || localCount == 0
        
        if (needsSync && isInternetAvailable()) {
            try {
                Log.d("VoiceRepository", "Syncing voices from Firestore...")
                val remoteVoices = withContext(Dispatchers.IO) {
                    firestoreRepository.getVoices()
                }
                
                if (remoteVoices.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        voiceDao.replaceAll(remoteVoices)
                    }
                    sharedPrefs.edit().putLong("last_voice_sync", now).apply()
                    Log.d("VoiceRepository", "Sync complete.")
                }
            } catch (e: Exception) {
                Log.e("VoiceRepository", "Failed to sync voices", e)
            }
        }
    }
    
    suspend fun forceSync() {
        if (isInternetAvailable()) {
            try {
                Log.d("VoiceRepository", "Force syncing voices from Firestore...")
                val remoteVoices = withContext(Dispatchers.IO) {
                    firestoreRepository.getVoices()
                }
                
                if (remoteVoices.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        voiceDao.replaceAll(remoteVoices)
                    }
                    sharedPrefs.edit().putLong("last_voice_sync", System.currentTimeMillis()).apply()
                    Log.d("VoiceRepository", "Force sync complete.")
                }
            } catch (e: Exception) {
                Log.e("VoiceRepository", "Failed to force sync voices", e)
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
