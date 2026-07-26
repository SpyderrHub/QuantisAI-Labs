cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/WatchAdScreen.kt
package com.example.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchAdScreen(authManager: AuthManager, onNavigateBack: () -> Unit) {
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var adState by remember { mutableStateOf("loading") } // loading, ready, playing, finished, error
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    
    LaunchedEffect(Unit) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adState = "error"
                rewardedAd = null
            }
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                adState = "ready"
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earn Credits") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
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
                "loading" -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Loading Ad...",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
                "error" -> {
                    Icon(
                        Icons.Rounded.CheckCircle, // using a fallback icon since error might not be loaded
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
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Go Back")
                    }
                }
                "ready", "playing" -> {
                    Icon(
                        Icons.Rounded.PlayCircle,
                        contentDescription = "Watch Ad",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Watch a short video to earn 50 credits.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            adState = "playing"
                            rewardedAd?.let { ad ->
                                ad.show(context as Activity) { rewardItem ->
                                    // Reward user
                                    adState = "finished"
                                }
                            } ?: run {
                                adState = "error"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Start Ad")
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
                        "Thanks for watching! You earned 50 credits.",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (user != null) {
                                scope.launch {
                                    firestoreRepository.addCredits(user.uid, 50)
                                    onNavigateBack()
                                }
                            } else {
                                onNavigateBack()
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
INNER_EOF
