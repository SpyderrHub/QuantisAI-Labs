sed -i '1d' app/src/main/java/com/example/data/FirestoreRepository.kt
sed -i '/package com.example.data/a import kotlinx.coroutines.channels.awaitClose' app/src/main/java/com/example/data/FirestoreRepository.kt
