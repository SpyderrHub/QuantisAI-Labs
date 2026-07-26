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
                minimumFetchIntervalInSeconds = 604800 // 7 days
            }
            remoteConfig.setConfigSettingsAsync(configSettings).await()
            
            // Set default values if needed, otherwise it will return empty string or default types
            // remoteConfig.setDefaultsAsync(mapOf("API_KEY" to "default_key")).await()

            // Fetch and activate the config
            remoteConfig.fetchAndActivate().await()
            isInitialized = true
        } catch (e: Exception) {
            Log.e("RemoteConfigManager", "Failed to initialize Firebase Remote Config", e)
        }
    }

    fun getApiKey(keyName: String): String {
        return remoteConfig.getString(keyName)
    }

    fun getWebClientId(): String {
        return remoteConfig.getString("WEB_CLIENT_ID")
    }

    fun getGeminiApiKey(): String {
        return remoteConfig.getString("GEMINI_API_KEY")
    }

    fun getTtsApiUrl(): String {
        return remoteConfig.getString("TTS_API_URL")
    }

    fun getVoiceDesignerApiUrl(): String {
        return remoteConfig.getString("VOICE_DESIGNER_API_URL")
    }

    fun getSttApiUrl(): String {
        return remoteConfig.getString("STT_API_URL")
    }

    fun getPlanPrice(keyName: String): String {
        return remoteConfig.getString(keyName)
    }

    fun getRazorpayKeyId(): String {
        return remoteConfig.getString("RAZORPAY_KEY_ID")
    }
}
