sed -i 's/val request = requestBuilder.build()/val request = requestBuilder.build()/' app/src/main/java/com/example/api/TtsApiManager.kt
sed -i '/                .build()/d' app/src/main/java/com/example/api/TtsApiManager.kt
