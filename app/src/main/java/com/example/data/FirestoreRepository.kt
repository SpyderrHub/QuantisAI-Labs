package com.example.data
import kotlinx.coroutines.channels.awaitClose

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val email: String = "",
    var name: String = "",
    var avatarUrl: String = "",
    val credits: Int = 3000,
    var savedVoices: List<String> = emptyList(),
    var subscriptionPlan: String = "free",
    var subscriptionType: String = "",
    var subscriptionExpiry: Long = 0L,
    var adsWatchedToday: Int = 0,
    var lastAdDate: String = ""
)

data class GenerationHistory(
    val id: String = "",
    val text: String = "",
    val type: String = "TTS",
    val date: Long = System.currentTimeMillis(),
    val voiceName: String = "",
    val duration: String = "",
    val creditsUsed: Int = 0,
    val audioUrl: String = "",
    val imageUrl: String = ""
)

@Entity(tableName = "voices")
data class VoiceEntity(
    @PrimaryKey
    @get:com.google.firebase.firestore.PropertyName("voiceName")
    @set:com.google.firebase.firestore.PropertyName("voiceName")
    var voiceName: String = "",
    var language: String = "",
    var gender: String = "",
    @get:com.google.firebase.firestore.PropertyName("isPro")
    @set:com.google.firebase.firestore.PropertyName("isPro")
    var isPro: Boolean = false,
    var description: String = "",
    var imageUrl: String = "",
    var avatarUrl: String = "",
    var audioUrl: String = "",
    var referenceText: String = "",
    var lastUpdated: Long = 0L
)

class FirestoreRepository {
    private val db = try { Firebase.firestore } catch (e: Exception) { null }
    
    fun getUserProfileFlow(userId: String, email: String): kotlinx.coroutines.flow.Flow<UserProfile> = kotlinx.coroutines.flow.callbackFlow {
        if (db == null) {
            trySend(UserProfile(email))
            close()
            return@callbackFlow
        }
        val listener = db.collection("users").document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(UserProfile(email))
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java) ?: UserProfile(email)
                trySend(profile)
            } else {
                val profile = UserProfile(email)
                db.collection("users").document(userId).set(profile)
                trySend(profile)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getUserProfile(userId: String, email: String): UserProfile {
        if (db == null) return UserProfile(email)
        return try {
            val doc = db.collection("users").document(userId).get().await()
            if (doc.exists()) {
                doc.toObject(UserProfile::class.java) ?: UserProfile(email)
            } else {
                val profile = UserProfile(email)
                db.collection("users").document(userId).set(profile).await()
                profile
            }
        } catch (e: Exception) {
            UserProfile(email)
        }
    }

    suspend fun saveUserProfile(userId: String, profile: UserProfile): Boolean {
        if (db == null) return false
        return try {
            db.collection("users").document(userId).set(profile).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addCredits(userId: String, amount: Int): Boolean {
        if (db == null) return false
        return try {
            val docRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val newCredits = (snapshot.getLong("credits") ?: 100) + amount
                transaction.update(docRef, "credits", newCredits)
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateSubscription(userId: String, plan: String, type: String, creditsToAdd: Int): Boolean {
        if (db == null) return false
        return try {
            val docRef = db.collection("users").document(userId)
            val subRef = db.collection("user_subscriptions").document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentCredits = snapshot.getLong("credits") ?: 100L
                val newCredits = currentCredits + creditsToAdd
                transaction.update(docRef, mapOf(
                    "subscriptionPlan" to plan,
                    "subscriptionType" to type,
                    "credits" to newCredits
                ))
                transaction.set(subRef, mapOf(
                    "userId" to userId,
                    "plan" to plan,
                    "type" to type,
                    "credits" to creditsToAdd,
                    "updatedAt" to System.currentTimeMillis()
                ))
            }.await()
            true
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Failed to update subscription", e)
            false
        }
    }

    suspend fun updateAdsQuota(userId: String, adsWatchedToday: Int, lastAdDate: String): Boolean {
        if (db == null) return false
        return try {
            val docRef = db.collection("users").document(userId)
            val updates = mapOf(
                "adsWatchedToday" to adsWatchedToday,
                "lastAdDate" to lastAdDate
            )
            docRef.update(updates).await()
            true
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Failed to update ads quota", e)
            false
        }
    }

    suspend fun saveGeneration(userId: String, history: GenerationHistory) {
        if (db == null) return
        val docId = history.id.ifEmpty { java.util.UUID.randomUUID().toString() }
        val omniData = mapOf(
            "id" to docId,
            "text" to history.text,
            "type" to history.type,
            "date" to history.date,
            "createdAt" to history.date,
            "voiceName" to history.voiceName,
            "duration" to history.duration,
            "creditsUsed" to history.creditsUsed,
            "audioUrl" to history.audioUrl,
            "audio_storage_path" to history.audioUrl,
            "imageUrl" to history.imageUrl
        )
        try {
            db.collection("users").document(userId).collection("omnivoice_generations")
                .document(docId)
                .set(omniData)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error saving to omnivoice_generations", e)
        }
        try {
            db.collection("users").document(userId).collection("history")
                .document(docId)
                .set(history)
                .await()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error saving to history", e)
        }
    }
    
    suspend fun getHistory(userId: String): List<GenerationHistory> {
        if (db == null) return emptyList()
        val resultList = mutableListOf<GenerationHistory>()
        
        // 1. Fetch from users/{user_id}/omnivoice_generations
        try {
            val omniSnapshot = db.collection("users").document(userId).collection("omnivoice_generations")
                .get().await()
            for (doc in omniSnapshot.documents) {
                val audioPath = doc.getString("audio_storage_path") 
                    ?: doc.getString("audioUrl") 
                    ?: doc.getString("audio_url") 
                    ?: ""
                val text = doc.getString("text") 
                    ?: doc.getString("prompt") 
                    ?: doc.getString("lyrics") 
                    ?: ""
                val voiceName = doc.getString("voiceName") 
                    ?: doc.getString("voice_name") 
                    ?: doc.getString("title") 
                    ?: "Generated Audio"
                val type = doc.getString("type") ?: "OmniVoice"
                val date = doc.getLong("date") 
                    ?: doc.getLong("createdAt") 
                    ?: doc.getLong("timestamp") 
                    ?: System.currentTimeMillis()
                val imageUrl = doc.getString("imageUrl") 
                    ?: doc.getString("image_url") 
                    ?: ""
                val duration = doc.getString("duration") ?: ""
                val creditsUsed = doc.getLong("creditsUsed")?.toInt() ?: 0

                if (audioPath.isNotEmpty()) {
                    resultList.add(
                        GenerationHistory(
                            id = doc.id,
                            text = text,
                            type = type,
                            date = date,
                            voiceName = voiceName,
                            duration = duration,
                            creditsUsed = creditsUsed,
                            audioUrl = audioPath,
                            imageUrl = imageUrl
                        )
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error fetching omnivoice_generations", e)
        }

        // 2. Fetch from users/{user_id}/history
        try {
            val historySnapshot = db.collection("users").document(userId).collection("history")
                .get().await()
            val historyItems = historySnapshot.toObjects(GenerationHistory::class.java)
            resultList.addAll(historyItems)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepository", "Error fetching history", e)
        }

        // Deduplicate by ID or audioUrl and sort by date descending
        val uniqueMap = LinkedHashMap<String, GenerationHistory>()
        for (item in resultList) {
            val key = if (item.id.isNotEmpty()) item.id else item.audioUrl
            if (key.isNotEmpty() && !uniqueMap.containsKey(key)) {
                uniqueMap[key] = item
            }
        }

        return uniqueMap.values.sortedByDescending { it.date }
    }

    suspend fun getVoices(): List<VoiceEntity> {
        if (db == null) return emptyList()
        return try {
            val defaultVoices = listOf(
                VoiceEntity("Aria", "English (US)", "Female", true, "Friendly", "", "https://i.pravatar.cc/150?u=Aria", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Roger", "English (UK)", "Male", false, "Professional", "", "https://i.pravatar.cc/150?u=Roger", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Sarah", "English (US)", "Female", false, "Warm", "", "https://i.pravatar.cc/150?u=Sarah", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Laura", "French", "Female", true, "Elegant", "", "https://i.pravatar.cc/150?u=Laura", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Mateo", "Spanish", "Male", false, "Energetic", "", "https://i.pravatar.cc/150?u=Mateo", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Yuki", "Japanese", "Female", true, "Calm", "", "https://i.pravatar.cc/150?u=Yuki", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Hans", "German", "Male", false, "Authoritative", "", "https://i.pravatar.cc/150?u=Hans", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Chloe", "English (AU)", "Female", true, "Bright", "", "https://i.pravatar.cc/150?u=Chloe", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Raj", "Hindi", "Male", false, "Clear", "", "https://i.pravatar.cc/150?u=Raj", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Sofia", "Italian", "Female", true, "Expressive", "", "https://i.pravatar.cc/150?u=Sofia", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Wei", "Chinese", "Male", false, "Neutral", "", "https://i.pravatar.cc/150?u=Wei", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Isabella", "Spanish (MX)", "Female", false, "Sweet", "", "https://i.pravatar.cc/150?u=Isabella", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Liam", "English (IE)", "Male", true, "Conversational", "", "https://i.pravatar.cc/150?u=Liam", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Emma", "English (US)", "Female", false, "Narrative", "", "https://i.pravatar.cc/150?u=Emma", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Noah", "English (CA)", "Male", true, "Deep", "", "https://i.pravatar.cc/150?u=Noah", "https://actions.google.com/sounds/v1/speech/test_tone.ogg"),
                VoiceEntity("Mia", "English (UK)", "Female", false, "Childlike", "", "https://i.pravatar.cc/150?u=Mia", "https://actions.google.com/sounds/v1/speech/test_tone.ogg")
            )
            
            val snapshot = db.collection("voices").get().await()
            if (snapshot.isEmpty) {
                defaultVoices.forEach {
                    db.collection("voices").document(it.voiceName).set(it).await()
                }
                defaultVoices
            } else {
                val voices = snapshot.toObjects(VoiceEntity::class.java)
                if (voices.any { it.voiceName.isEmpty() || it.avatarUrl.isEmpty() }) {
                    defaultVoices.forEach {
                        db.collection("voices").document(it.voiceName).set(it).await()
                    }
                    defaultVoices
                } else {
                    voices
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

