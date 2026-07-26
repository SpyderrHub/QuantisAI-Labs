sed -i 's/import org.json.JSONObject/import org.json.JSONObject\nimport com.google.firebase.Firebase\nimport com.google.firebase.auth.auth\nimport kotlinx.coroutines.tasks.await/' app/src/main/java/com/example/api/SttApiManager.kt

sed -i 's/            val url = RemoteConfigManager.getSttApiUrl()/            RemoteConfigManager.initialize(force = true)\n            val url = RemoteConfigManager.getSttApiUrl()/' app/src/main/java/com/example/api/SttApiManager.kt

sed -i 's/            val request = Request.Builder()/            val authToken = try { Firebase.auth.currentUser?.getIdToken(false)?.await()?.token } catch (e: Exception) { null }\n            val requestBuilder = Request.Builder().url(url).post(body)\n            if (authToken != null) { requestBuilder.addHeader("Authorization", "Bearer $authToken") }\n            val request = requestBuilder.build()/' app/src/main/java/com/example/api/SttApiManager.kt

sed -i '/\.url(url)/d' app/src/main/java/com/example/api/SttApiManager.kt
sed -i '/\.post(body)/d' app/src/main/java/com/example/api/SttApiManager.kt
