#!/bin/bash
sed -i '55a\
    fun getUserProfileFlow(userId: String, email: String): kotlinx.coroutines.flow.Flow<UserProfile> = kotlinx.coroutines.channels.awaitClose { } /* dummy to be replaced */' app/src/main/java/com/example/data/FirestoreRepository.kt
