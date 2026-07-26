sed -i '/\.url(url)/d' app/src/main/java/com/example/api/TtsApiManager.kt
sed -i '/\.post(body)/d' app/src/main/java/com/example/api/TtsApiManager.kt
sed -i 's/val requestBuilder = Request.Builder()/val requestBuilder = Request.Builder().url(url).post(body)/' app/src/main/java/com/example/api/TtsApiManager.kt
