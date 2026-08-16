package com.example.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

object TtsApiManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
        
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun generateSpeech(text: String, referenceAudio: String, referenceText: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            RemoteConfigManager.initialize(force = true)
            val url = RemoteConfigManager.getTtsApiUrl()
            if (url.isEmpty()) {
                return@withContext Result.failure(Exception("TTS API URL is not configured."))
            }

            val jsonBody = JSONObject().apply {
                put("text", text)
                put("reference_audio", referenceAudio)
                put("reference_text", referenceText)
            }

            val body = jsonBody.toString().toRequestBody(JSON)
            val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }
            val requestBuilder = Request.Builder().url(url).post(body)
            if (authToken != null) { requestBuilder.addHeader("Authorization", "Bearer $authToken") }
            val request = requestBuilder.build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }

            val responseData = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body"))
            val responseJson = JSONObject(responseData)
            
            if (responseJson.has("audio_download_url")) {
                val audioUrl = responseJson.getString("audio_download_url")
                
                // Credit deduction
                val userId = Firebase.auth.currentUser?.uid
                if (userId != null) {
                    val creditsToDeduct = text.length
                    com.example.data.FirestoreRepository().addCredits(userId, -creditsToDeduct)
                }

                return@withContext Result.success(audioUrl)
            } else {
                return@withContext Result.failure(Exception("Missing audio_download_url in response"))
            }
        } catch (e: Exception) {
            Log.e("TtsApiManager", "Error generating speech", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun generateVoiceDesign(text: String, instruct: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            RemoteConfigManager.initialize(force = true)
            val url = RemoteConfigManager.getVoiceDesignerApiUrl()
            if (url.isEmpty()) {
                return@withContext Result.failure(Exception("Voice Designer API URL is not configured."))
            }

            val jsonBody = JSONObject().apply {
                put("text", text)
                put("instruct", instruct)
            }

            val body = jsonBody.toString().toRequestBody(JSON)
            val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }
            val requestBuilder = Request.Builder().url(url).post(body)
            if (authToken != null) { requestBuilder.addHeader("Authorization", "Bearer $authToken") }
            val request = requestBuilder.build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }

            val responseData = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body"))
            val responseJson = JSONObject(responseData)
            
            if (responseJson.has("audio_download_url")) {
                val audioUrl = responseJson.getString("audio_download_url")
                
                // Credit deduction: deduct sum of text length and instruct (referenceText) length
                val userId = Firebase.auth.currentUser?.uid
                if (userId != null) {
                    val creditsToDeduct = text.length + instruct.length
                    com.example.data.FirestoreRepository().addCredits(userId, -creditsToDeduct)
                }

                return@withContext Result.success(audioUrl)
            } else {
                return@withContext Result.failure(Exception("Missing audio_download_url in response"))
            }
        } catch (e: Exception) {
            Log.e("TtsApiManager", "Error generating voice design", e)
            return@withContext Result.failure(e)
        }
    }
}
