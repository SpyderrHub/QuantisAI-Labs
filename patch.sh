sed -i 's/kotlinx.coroutines.channels.awaitClose/awaitClose/g' app/src/main/java/com/example/data/FirestoreRepository.kt
sed -i '1i import kotlinx.coroutines.channels.awaitClose' app/src/main/java/com/example/data/FirestoreRepository.kt
