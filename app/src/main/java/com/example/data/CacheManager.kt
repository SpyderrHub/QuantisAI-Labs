package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class CacheManager(private val context: Context) {
    private val audioCacheDir = File(context.cacheDir, "audio_cache").apply {
        if (!exists()) mkdirs()
    }
    private val MAX_CACHE_SIZE_BYTES = 50 * 1024 * 1024L // 50 MB
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun getCachedAudioUrl(url: String): String = withContext(Dispatchers.IO) {
        if (url.startsWith("file://") || url.startsWith("content://")) {
            return@withContext url
        }
        
        val filename = url.hashCode().toString() + ".mp3"
        val file = File(audioCacheDir, filename)
        
        if (file.exists()) {
            file.setLastModified(System.currentTimeMillis())
            Log.d("CacheManager", "Returning cached file: ${file.absolutePath}")
            return@withContext file.absolutePath
        }

        try {
            val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }
            val requestBuilder = Request.Builder().url(url)
            if (authToken != null) {
                requestBuilder.addHeader("Authorization", "Bearer $authToken")
            }
            val request = requestBuilder.build()
            
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                if (inputStream != null) {
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    outputStream.close()
                    inputStream.close()
                    
                    cleanupOldCacheFiles()
                    
                    return@withContext file.absolutePath
                }
            } else {
                Log.e("CacheManager", "Error downloading audio to cache: HTTP ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("CacheManager", "Error downloading audio to cache", e)
        }
        
        // Fallback to original URL
        return@withContext url
    }

    private fun cleanupOldCacheFiles() {
        val files = audioCacheDir.listFiles() ?: return
        var totalSize = files.sumOf { it.length() }
        
        if (totalSize <= MAX_CACHE_SIZE_BYTES) return
        
        val sortedFiles = files.sortedBy { it.lastModified() }
        for (file in sortedFiles) {
            totalSize -= file.length()
            file.delete()
            if (totalSize <= MAX_CACHE_SIZE_BYTES) break
        }
    }
}
