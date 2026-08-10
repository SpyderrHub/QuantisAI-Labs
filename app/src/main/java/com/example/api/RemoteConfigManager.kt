package com.example.api

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigManager {

    private var isInitialized = false

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig
    }

    suspend fun initialize(force: Boolean = false) {
        if (isInitialized && !force) return
        
        try {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (force) 0 else 604800 // 0s if forced, else 7 days
            }
            remoteConfig.setConfigSettingsAsync(configSettings).await()
            
            // Set default values if needed, otherwise it will return empty string or default types
            // remoteConfig.setDefaultsAsync(mapOf("API_KEY" to "default_key")).await()

            // Fetch and activate the config
            val updated = remoteConfig.fetchAndActivate().await()
            if (updated) {
                Log.d("RemoteConfigManager", "Remote config updated and cached")
            } else {
                Log.d("RemoteConfigManager", "Remote config already up to date, leaving as is")
            }
            isInitialized = true
        } catch (e: Exception) {
            Log.e("RemoteConfigManager", "Failed to initialize Firebase Remote Config", e)
        }
    }

    fun getApiKey(keyName: String): String {
        return try { remoteConfig.getString(keyName) } catch (e: Exception) { "" }
    }

    fun getWebClientId(): String {
        return try { remoteConfig.getString("WEB_CLIENT_ID") } catch (e: Exception) { "" }
    }

    fun getGeminiApiKey(): String {
        return try { remoteConfig.getString("GEMINI_API_KEY") } catch (e: Exception) { "" }
    }

    fun getTtsApiUrl(): String {
        return try { remoteConfig.getString("TTS_API_URL") } catch (e: Exception) { "" }
    }

    fun getVoiceDesignerApiUrl(): String {
        return try { remoteConfig.getString("VOICE_DESIGNER_API_URL") } catch (e: Exception) { "" }
    }

    fun getSttApiUrl(): String {
        return try { remoteConfig.getString("STT_API_URL") } catch (e: Exception) { "" }
    }

    fun getPlanPrice(keyName: String): String {
        return try { remoteConfig.getString(keyName) } catch (e: Exception) { "" }
    }

    fun getRazorpayKeyId(): String {
        return try { remoteConfig.getString("RAZORPAY_KEY_ID") } catch (e: Exception) { "" }
    }
}
