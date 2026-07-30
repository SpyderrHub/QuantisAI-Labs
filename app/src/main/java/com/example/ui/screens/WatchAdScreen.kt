package com.example.ui.screens

import android.app.Activity
import android.content.Context
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("Watch Ads & Earn", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    CreditsPill(credits = userCredits)
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 50.dp),
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
                                    
                                    ad.show(context as Activity) { rewardItem ->
                                        adsWatchedToday++
                                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                        if (user != null) {
                                            scope.launch {
                                                firestoreRepository.updateAdsQuota(user.uid, adsWatchedToday, today)
                                            }
                                        }
                                        adState = "finished"
                                    }
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B4EE6),
                        disabledContainerColor = Color(0xFF6B4EE6).copy(alpha = 0.5f)
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
                                text = if (adsWatchedToday >= maxAdsPerDay) "Limit Reached" else "Watch Ad Now", 
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
                        withStyle(SpanStyle(color = Color(0xFFE6A300))) {
                            append("$adsWatchedToday ")
                        }
                        append("/ $maxAdsPerDay")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            HeroBanner()
            ProgressCard(adsWatched = adsWatchedToday, maxAds = maxAdsPerDay, creditsPerAd = creditsPerAd)
            HowItWorksSection()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CreditsPill(credits: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
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
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun HeroBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111424)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Watch Ads\nEarn Credits",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Watch short ads and earn credits to unlock premium AI features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(80.dp).weight(0.8f), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Rounded.DesktopWindows,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = Color(0xFF6B4EE6)
                )
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
                Icon(
                    Icons.Rounded.MonetizationOn,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-8).dp, y = 8.dp),
                    tint = Color(0xFFFFB300)
                )
            }
        }
    }
}

@Composable
fun ProgressCard(adsWatched: Int, maxAds: Int, creditsPerAd: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = borderStroke()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                        text = "Your Progress Today",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFFE6A300))) {
                            append("$adsWatched ")
                        }
                        withStyle(SpanStyle(color = Color(0xFF6B4EE6))) {
                            append("/ $maxAds Ads")
                        }
                    },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { if (maxAds > 0) adsWatched.toFloat() / maxAds else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF6B4EE6),
                    trackColor = Color(0xFFF3F4F6)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Watch up to $maxAds ads daily and earn credits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Rounded.Toll,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+$creditsPerAd Credits",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Per Ad",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            style = MaterialTheme.typography.titleMedium
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
                description = "Tap on watch ad button to start.",
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.padding(top = 24.dp).size(16.dp)
            )
            HowItWorksStep(
                icon = Icons.Rounded.AccessTime,
                stepNumber = "2",
                title = "Complete Ad",
                description = "Watch full ad to earn credits.",
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.padding(top = 24.dp).size(16.dp)
            )
            HowItWorksStep(
                icon = Icons.Rounded.MonetizationOn,
                stepNumber = "3",
                title = "Earn Credits",
                description = "Credits will be added to your account.",
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
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFEDE9FE), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6B4EE6), modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color(0xFFE5E7EB), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
