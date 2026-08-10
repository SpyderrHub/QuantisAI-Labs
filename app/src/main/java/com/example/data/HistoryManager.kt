package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class HistoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("generation_history_prefs", Context.MODE_PRIVATE)
    private val firestoreRepository = FirestoreRepository()

    fun getLocalHistory(): List<GenerationHistory> {
        val json = prefs.getString("history_list", null) ?: return emptyList()
        val list = mutableListOf<GenerationHistory>()
        return try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GenerationHistory(
                        id = obj.optString("id", ""),
                        text = obj.optString("text", ""),
                        type = obj.optString("type", "TTS"),
                        date = obj.optLong("date", System.currentTimeMillis()),
                        voiceName = obj.optString("voiceName", ""),
                        duration = obj.optString("duration", ""),
                        creditsUsed = obj.optInt("creditsUsed", 0),
                        audioUrl = obj.optString("audioUrl", ""),
                        imageUrl = obj.optString("imageUrl", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveLocalHistory(list: List<GenerationHistory>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("text", item.text)
                    put("type", item.type)
                    put("date", item.date)
                    put("voiceName", item.voiceName)
                    put("duration", item.duration)
                    put("creditsUsed", item.creditsUsed)
                    put("audioUrl", item.audioUrl)
                    put("imageUrl", item.imageUrl)
                }
                array.put(obj)
            }
            prefs.edit().putString("history_list", array.toString()).apply()
        } catch (e: Exception) {
            // ignore
        }
    }

    suspend fun saveHistoryItem(userId: String?, history: GenerationHistory) {
        val current = getLocalHistory().toMutableList()
        current.removeAll { it.id == history.id || (it.audioUrl == history.audioUrl && history.audioUrl.isNotEmpty()) }
        current.add(0, history)
        saveLocalHistory(current)

        if (!userId.isNullOrEmpty()) {
            withContext(Dispatchers.IO) {
                try {
                    firestoreRepository.saveGeneration(userId, history)
                } catch (e: Exception) {
                    // Ignore remote failure
                }
            }
        }
    }

    suspend fun deleteHistoryItem(userId: String?, item: GenerationHistory) {
        val current = getLocalHistory().toMutableList()
        current.removeAll { it.id == item.id || (it.audioUrl == item.audioUrl && item.audioUrl.isNotEmpty()) }
        saveLocalHistory(current)
        if (!userId.isNullOrEmpty() && item.id.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                try {
                    firestoreRepository.deleteHistoryItem(userId, item.id)
                } catch (e: Exception) {
                }
            }
        }
    }

    suspend fun fetchHistory(userId: String?): List<GenerationHistory> {
        val local = getLocalHistory()
        if (userId.isNullOrEmpty()) return local
        return withContext(Dispatchers.IO) {
            try {
                val remote = firestoreRepository.getHistory(userId)
                if (remote.isNotEmpty()) {
                    val map = LinkedHashMap<String, GenerationHistory>()
                    for (item in local) {
                        val key = if (item.id.isNotEmpty()) item.id else item.audioUrl
                        if (key.isNotEmpty()) map[key] = item
                    }
                    for (item in remote) {
                        val key = if (item.id.isNotEmpty()) item.id else item.audioUrl
                        if (key.isNotEmpty()) map[key] = item
                    }
                    val sorted = map.values.sortedByDescending { it.date }
                    saveLocalHistory(sorted)
                    sorted
                } else {
                    local
                }
            } catch (e: Exception) {
                local
            }
        }
    }
}
