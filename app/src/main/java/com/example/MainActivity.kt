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
import com.razorpay.PaymentResultListener
import android.widget.Toast
import com.example.data.FirestoreRepository

class MainActivity : ComponentActivity(), PaymentResultListener {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this)
        
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
                androidx.compose.runtime.mutableStateOf(
                    com.example.ui.theme.AppSettings(
                        language = sharedPrefs.getString("language", "English") ?: "English",
                        theme = sharedPrefs.getString("theme", "default") ?: "default"
                    )
                ) 
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

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        val currentUserId = authManager.currentUser.value?.uid
        if (currentUserId != null) {
            val plan = com.example.ui.screens.PendingSubscription.pendingPlan
            val type = com.example.ui.screens.PendingSubscription.pendingType
            val credits = com.example.ui.screens.PendingSubscription.pendingCredits
            
            if (plan.isNotEmpty() && credits > 0) {
                lifecycleScope.launch {
                    val repository = FirestoreRepository()
                    val success = repository.updateSubscription(currentUserId, plan, type, credits)
                    if (success) {
                        Toast.makeText(
                            this@MainActivity,
                            "Payment Successful! Activated ${plan.uppercase()} plan with $credits credits added.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Payment received but failed to update account. Please contact support with Payment ID: $razorpayPaymentId",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    
                    // Reset pending state
                    com.example.ui.screens.PendingSubscription.pendingPlan = ""
                    com.example.ui.screens.PendingSubscription.pendingType = ""
                    com.example.ui.screens.PendingSubscription.pendingCredits = 0
                }
            } else {
                Toast.makeText(
                    this,
                    "Payment Successful! ID: $razorpayPaymentId",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "Payment Successful but no user is logged in! ID: $razorpayPaymentId",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(
            this,
            "Payment Failed! Error: $response",
            Toast.LENGTH_LONG
        ).show()
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
                    navController.navigate(LoginRoute) {
                        popUpTo<IntroRoute> { inclusive = true }
                    }
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
