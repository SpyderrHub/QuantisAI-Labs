package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthManager
import com.example.data.FirestoreRepository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchAdScreen(authManager: AuthManager, onNavigateBack: () -> Unit) {
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { FirestoreRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val sharedPrefs = remember { context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE) }
    
    var adsWatchedToday by remember { mutableIntStateOf(0) }
    var adState by remember { mutableStateOf("initial") } // initial, limit_reached, loading, error, finished
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var userCredits by remember { mutableIntStateOf(0) }
    
    val maxAdsPerDay = 50
    val creditsPerAd = 30
    
    LaunchedEffect(user?.uid) {
        if (user != null) {
            firestoreRepository.getUserProfileFlow(user.uid, user.email ?: "").collect { profile ->
                userCredits = profile.credits
                
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (profile.lastAdDate == today) {
                    adsWatchedToday = profile.adsWatchedToday
                } else {
                    adsWatchedToday = 0
                }
                
                if (adsWatchedToday >= maxAdsPerDay) {
                    adState = "limit_reached"
                }
            }
        }
    }
    
    if (adState == "finished") {
        AlertDialog(
            onDismissRequest = { adState = "initial" },
            title = { Text("Thanks for watching!") },
            text = { Text("You earned $creditsPerAd credits.") },
            confirmButton = {
                Button(onClick = {
                    if (user != null) {
                        scope.launch {
                            firestoreRepository.addCredits(user.uid, creditsPerAd)
                            userCredits += creditsPerAd
                            adState = "initial"
                        }
                    } else {
                        adState = "initial"
                    }
                }) {
                    Text("Collect Credits")
                }
            }
        )
    }
    
    if (adState == "error") {
        AlertDialog(
            onDismissRequest = { adState = "initial" },
            title = { Text("Oops!") },
            text = { Text("Failed to load ad. Please try again later.") },
            confirmButton = {
                Button(onClick = { adState = "initial" }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF090A10),
        topBar = {
            TopAppBar(
                title = { 
                    Text("Watch Ads & Earn", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    CreditsPill(credits = userCredits)
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF090A10)
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF090A10))
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                    
                                    val activity = context.findActivity()
                                    if (activity != null) {
                                        ad.show(activity) { rewardItem ->
                                            adsWatchedToday++
                                            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                            if (user != null) {
                                                scope.launch {
                                                    firestoreRepository.updateAdsQuota(user.uid, adsWatchedToday, today)
                                                }
                                            }
                                            adState = "finished"
                                        }
                                    } else {
                                        adState = "error"
                                    }
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    enabled = adsWatchedToday < maxAdsPerDay && adState != "loading"
                ) {
                    if (adState == "loading") {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (adsWatchedToday >= maxAdsPerDay) "Limit Reached" else "Watch Ad Now (+30 Credits)", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Daily Quota: ")
                        withStyle(SpanStyle(color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)) {
                            append("$adsWatchedToday ")
                        }
                        append("/ $maxAdsPerDay")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090A10))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            HeroBanner()
            ProgressCard(adsWatched = adsWatchedToday, maxAds = maxAdsPerDay, creditsPerAd = creditsPerAd)
            HowItWorksSection()
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CreditsPill(credits: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131524))
            .border(1.dp, Color(0x30A855F7), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.MonetizationOn, 
                contentDescription = null, 
                tint = Color(0xFFFFB300), 
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = java.text.NumberFormat.getNumberInstance(Locale.US).format(credits),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF131420))
            .border(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(Color(0x40A855F7), Color(0x203B82F6))
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Watch Ads\nEarn Credits",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Watch short sponsored videos to instantly add +30 credits to your balance.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ProgressCard(adsWatched: Int, maxAds: Int, creditsPerAd: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF131420))
            .border(
                width = 1.dp,
                color = Color(0x1AFFFFFF),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Progress",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = "$adsWatched / $maxAds Ads Completed",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { if (maxAds > 0) adsWatched.toFloat() / maxAds else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFF222436)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "+$creditsPerAd",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(
                    text = "Per Ad",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
)

@Composable
fun HowItWorksSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "How it works?",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            HowItWorksStep(
                icon = Icons.Rounded.PlayArrow,
                stepNumber = "1",
                title = "Watch Ad",
                description = "Tap watch ad button to begin.",
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier.padding(top = 20.dp).size(16.dp)
            )
            HowItWorksStep(
                icon = Icons.Rounded.AccessTime,
                stepNumber = "2",
                title = "Complete",
                description = "Watch the full video ad.",
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier.padding(top = 20.dp).size(16.dp)
            )
            HowItWorksStep(
                icon = Icons.Rounded.MonetizationOn,
                stepNumber = "3",
                title = "Earn",
                description = "Credits added to account.",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HowItWorksStep(
    icon: ImageVector,
    stepNumber: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1F30))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 10.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}
