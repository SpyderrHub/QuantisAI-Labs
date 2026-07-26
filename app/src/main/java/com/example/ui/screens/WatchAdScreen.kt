package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.auth.AuthManager
import com.example.data.FirestoreRepository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchAdScreen(authManager: AuthManager, onNavigateBack: () -> Unit) {
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val sharedPrefs = remember { context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE) }
    
    var adsWatchedToday by remember { mutableIntStateOf(0) }
    var adState by remember { mutableStateOf("initial") } // initial, loading, playing, finished, error, limit_reached
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    
    val maxAdsPerDay = 50
    
    // Load current progress
    LaunchedEffect(Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = sharedPrefs.getString("last_ad_date", "")
        
        if (today != lastDate) {
            sharedPrefs.edit().putString("last_ad_date", today).putInt("ads_watched_today", 0).apply()
            adsWatchedToday = 0
        } else {
            adsWatchedToday = sharedPrefs.getInt("ads_watched_today", 0)
        }
        
        if (adsWatchedToday >= maxAdsPerDay) {
            adState = "limit_reached"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earn Credits") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (adState) {
                "initial", "limit_reached" -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Rounded.PlayCircle,
                                contentDescription = "Watch Ad",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Click to watch Ad",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Earn 30 credits per ad. Watch up to $maxAdsPerDay ads daily.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            
                            // Progress bar
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Daily Quota", style = MaterialTheme.typography.labelMedium)
                                    Text("$adsWatchedToday / $maxAdsPerDay", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                                LinearProgressIndicator(
                                    progress = { adsWatchedToday.toFloat() / maxAdsPerDay },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    if (adsWatchedToday < maxAdsPerDay) {
                                        adState = "loading"
                                        val adRequest = AdRequest.Builder().build()
                                        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
                                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                                adState = "error"
                                                rewardedAd = null
                                            }
                                            override fun onAdLoaded(ad: RewardedAd) {
                                                rewardedAd = ad
                                                adState = "ready"
                                                
                                                // Auto-show when loaded
                                                ad.show(context as Activity) { rewardItem ->
                                                    // Reward user
                                                    adsWatchedToday++
                                                    sharedPrefs.edit().putInt("ads_watched_today", adsWatchedToday).apply()
                                                    adState = "finished"
                                                }
                                            }
                                        })
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = adsWatchedToday < maxAdsPerDay
                            ) {
                                Text(if (adsWatchedToday >= maxAdsPerDay) "Limit Reached" else "Go")
                            }
                        }
                    }
                }
                "loading", "ready", "playing" -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Loading Ad...",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
                "error" -> {
                    Icon(
                        Icons.Rounded.CheckCircle, 
                        contentDescription = "Error",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Failed to load ad. Please try again later.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { adState = "initial" },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Go Back")
                    }
                }
                "finished" -> {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "Done",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Thanks for watching! You earned 30 credits.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (user != null) {
                                scope.launch {
                                    firestoreRepository.addCredits(user.uid, 30)
                                    adState = "initial" // Go back to initial state
                                }
                            } else {
                                adState = "initial"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Collect Credits")
                    }
                }
            }
        }
    }
}
