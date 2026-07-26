package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.api.RemoteConfigManager

class AuthManager(private val context: Context) {
    private val auth: FirebaseAuth? = try {
        Firebase.auth
    } catch (e: Exception) {
        Log.e("AuthManager", "Firebase initialization failed.", e)
        null
    }

    private val _currentUser = MutableStateFlow(auth?.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()
    
    val isFirebaseConfigured: Boolean
        get() = FirebaseApp.getApps(context).isNotEmpty()

    suspend fun signInWithGoogle(): Result<Unit> {
        if (auth == null) return Result.failure(Exception("Firebase is not configured. Please add google-services.json."))
        
        RemoteConfigManager.initialize()

        val credentialManager = CredentialManager.create(context)
        val webClientId = RemoteConfigManager.getWebClientId()
        if (webClientId.isEmpty() || webClientId == "YOUR_WEB_CLIENT_ID") {
            return Result.failure(Exception("WEB_CLIENT_ID is not set in Firebase Remote Config"))
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            
            if (credential is GoogleIdTokenCredential) {
                val googleIdToken = credential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                _currentUser.value = authResult.user
                Result.success(Unit)
            } else {
                Result.failure(Exception("Unexpected credential type."))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> {
        if (auth == null) return Result.failure(Exception("Firebase is not configured."))
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            _currentUser.value = authResult.user
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        if (auth == null) return Result.failure(Exception("Firebase is not configured."))
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            _currentUser.value = authResult.user
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }
}
