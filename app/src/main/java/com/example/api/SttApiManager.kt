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

object SttApiManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun generateText(audioPath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            RemoteConfigManager.initialize(force = true)
            val url = RemoteConfigManager.getSttApiUrl()
            if (url.isEmpty()) {
                return@withContext Result.failure(Exception("STT API URL is not configured."))
            }

            val jsonBody = JSONObject().apply {
                put("audio_path", audioPath)
            }
            val body = jsonBody.toString().toRequestBody(JSON)
            
            val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }
            val requestBuilder = Request.Builder().url(url).post(body)
            if (authToken != null) {
                requestBuilder.addHeader("Authorization", "Bearer $authToken")
            }
            val request = requestBuilder.build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
            }

            val responseData = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body"))
            val responseJson = JSONObject(responseData)
            
            if (responseJson.has("txt_download_url")) {
                val txtUrl = responseJson.getString("txt_download_url")
                
                // Fetch the text from the txtUrl
                val txtRequest = Request.Builder().url(txtUrl).build()
                val txtResponse = client.newCall(txtRequest).execute()
                if (txtResponse.isSuccessful) {
                    val transcribedText = txtResponse.body?.string() ?: "Transcription successfully completed."
                    return@withContext Result.success(transcribedText)
                } else {
                     return@withContext Result.success("Transcription generated, but could not download text. URL: $txtUrl")
                }
            } else {
                return@withContext Result.failure(Exception("Missing txt_download_url in response"))
            }
        } catch (e: Exception) {
            Log.e("SttApiManager", "Error generating text", e)
            return@withContext Result.failure(e)
        }
    }
}
