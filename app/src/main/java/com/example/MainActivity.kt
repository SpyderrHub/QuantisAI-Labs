package com.example

import android.os.Bundle
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit
import com.example.sync.SyncWorker
import com.google.android.gms.ads.MobileAds
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthManager
import com.example.ui.navigation.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.data.FirestoreRepository

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        
        try {
            firebaseAnalytics = Firebase.analytics
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "FirebaseAnalytics initialization failed", e)
        }
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncVoices",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
        
        authManager = AuthManager(this)
        
        val sharedPrefs = getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        
        setContent {
            var appSettings by androidx.compose.runtime.remember { 
                val savedTheme = sharedPrefs.getString("theme", "white") ?: "white"
                androidx.compose.runtime.mutableStateOf(
                    com.example.ui.theme.AppSettings(
                        language = sharedPrefs.getString("language", "English") ?: "English",
                        theme = savedTheme
                    )
                ) 
            }
            
            androidx.compose.runtime.LaunchedEffect(appSettings.language) {
                val code = when (appSettings.language) {
                    "Spanish" -> "es"
                    "French" -> "fr"
                    "German" -> "de"
                    "Italian" -> "it"
                    "Portuguese" -> "pt"
                    "Russian" -> "ru"
                    "Chinese" -> "zh"
                    "Japanese" -> "ja"
                    "Korean" -> "ko"
                    "Arabic" -> "ar"
                    "Hindi" -> "hi"
                    "Indonesian" -> "id"
                    else -> "en"
                }
                val locale = java.util.Locale(code)
                java.util.Locale.setDefault(locale)
                val config = resources.configuration
                config.setLocale(locale)
                @Suppress("DEPRECATION")
                resources.updateConfiguration(config, resources.displayMetrics)
            }

            androidx.compose.runtime.CompositionLocalProvider(
                com.example.ui.theme.LocalAppSettings provides appSettings,
                com.example.ui.theme.LocalAppSettingsUpdater provides { newSettings -> 
                    appSettings = newSettings 
                    sharedPrefs.edit()
                        .putString("language", newSettings.language)
                        .putString("theme", newSettings.theme)
                        .apply()
                }
            ) {
                MyApplicationTheme {
                    QuantisApp(authManager)
                }
            }
        }
    }
}

@Composable
fun QuantisApp(authManager: AuthManager) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = SplashRoute) {
        composable<SplashRoute> {
            SplashScreen(
                authManager = authManager,
                onNavigateNext = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(MainRoute) {
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(IntroRoute) {
                            popUpTo(0)
                        }
                    }
                }
            )
        }
        
        composable<IntroRoute> {
            IntroScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginRoute)
                },
                onNavigateToSignup = {
                    navController.navigate(SignupRoute)
                }
            )
        }
        
        composable<SignupRoute> {
            SignupScreen(
                authManager = authManager,
                onNavigateToMain = {
                    navController.navigate(MainRoute) {
                        popUpTo(0)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<LoginRoute> {
            LoginScreen(
                authManager = authManager,
                onNavigateToSignup = {
                    navController.navigate(SignupRoute)
                },
                onNavigateToMain = {
                    navController.navigate(MainRoute) {
                        popUpTo(0)
                    }
                }
            )
        }
        
        composable<MainRoute> {
            MainScreen(
                authManager = authManager,
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0)
                    }
                },
                onNavigateToWatchAd = {
                    navController.navigate(WatchAdRoute)
                }
            )
        }
        
        composable<WatchAdRoute> {
            WatchAdScreen(
                authManager = authManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
