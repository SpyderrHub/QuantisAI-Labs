package com.example.ui.screens
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown

import com.example.api.SttApiManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.auth.AuthManager
import com.example.data.FirestoreRepository
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.rounded.Add
import com.example.data.VoiceEntity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Mic
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.example.api.TtsApiManager
import com.example.ui.theme.LocalAppSettings
import com.example.ui.theme.LocalAppSettingsUpdater
import com.example.ui.theme.translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(authManager: AuthManager, onLogout: () -> Unit, onNavigateToWatchAd: () -> Unit) {
    var currentTab by remember { mutableIntStateOf(0) }
    val appSettings = LocalAppSettings.current
    val lang = appSettings.language
    val tabs = listOf(
        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("Generate", Icons.Filled.Mic, Icons.Outlined.Mic),
        Triple("Design", Icons.Filled.DesignServices, Icons.Outlined.DesignServices),
        Triple("Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
        Triple("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Main Content Area with bottom padding for the floating navbar
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 90.dp)) {
                when (currentTab) {
                    0 -> HomeScreen(authManager, onNavigateToWatchAd)
                    1 -> GenerateScreen(authManager, { currentTab = 0 })
                    2 -> VoiceDesignScreen(authManager)
                    3 -> LibraryScreen(authManager)
                    4 -> AccountScreen(authManager, onLogout)
                }
            }
            
            // Floating Glass Navbar (VisionOS Style)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0x26FFFFFF)) // Translucent white for glass effect
                    .border(
                        width = 1.dp,
                        color = Color(0x33FFFFFF), // Subtle white border
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, (title, selectedIcon, unselectedIcon) ->
                        val isSelected = currentTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { currentTab = index }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = translate(title, lang),
                                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = translate(title, lang),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(authManager: AuthManager, onNavigateToWatchAd: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { FirestoreRepository() }
    val historyManager = remember { com.example.data.HistoryManager(context) }
    
    var credits by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("") }
    var userAvatar by remember { mutableStateOf("") }
    var userPlan by remember { mutableStateOf("free") }
    var isLoading by remember { mutableStateOf(true) }
    var recentGenerations by remember { mutableStateOf<List<com.example.data.GenerationHistory>>(emptyList()) }
    var selectedHistoryItemForPreview by remember { mutableStateOf<com.example.data.GenerationHistory?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        recentGenerations = historyManager.getLocalHistory()
    }

    LaunchedEffect(user) {
        if (user != null) {
            val profile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
            credits = profile.credits
            userName = profile.name
            userAvatar = profile.avatarUrl
            userPlan = profile.subscriptionPlan.ifEmpty { "free" }
            isLoading = false
            
            val history = historyManager.fetchHistory(user.uid)
            if (history.isNotEmpty()) {
                recentGenerations = history
            }
        } else {
            recentGenerations = historyManager.getLocalHistory()
            isLoading = false
        }
    }

    if (selectedHistoryItemForPreview != null) {
        val isFreeUser = userPlan.lowercase(java.util.Locale.getDefault()) == "free"
        AudioPreviewScreen(
            audioUrl = selectedHistoryItemForPreview!!.audioUrl,
            title = selectedHistoryItemForPreview!!.voiceName.ifEmpty { "Generated Speech" },
            subtitle = selectedHistoryItemForPreview!!.type.ifEmpty { "Audio generated successfully" },
            imageUrl = selectedHistoryItemForPreview!!.imageUrl.ifEmpty { null },
            lyricsText = selectedHistoryItemForPreview!!.text,
            isFreeUser = isFreeUser,
            onBack = { selectedHistoryItemForPreview = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val displayUser = userName.ifEmpty { user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Creator" }
                    Text(
                        text = displayUser,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                val homePlanColor = getPlanColor(userPlan)
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, homePlanColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userAvatar.isNotEmpty()) {
                            coil.compose.AsyncImage(
                                model = userAvatar,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Credits Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (isLoading) "..." else "$credits",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Credits Available",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Add Credits Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onNavigateToWatchAd()
                        }
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Watch Ad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+30 Credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Recent Activity
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (recentGenerations.isNotEmpty()) {
                        Text(
                            text = "${recentGenerations.size} audio(s)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                if (recentGenerations.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.GraphicEq,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "No recent generations",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        recentGenerations.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedHistoryItemForPreview = item
                                    }
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.imageUrl.isNotEmpty()) {
                                            coil.compose.AsyncImage(
                                                model = item.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Rounded.GraphicEq,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.voiceName.ifEmpty { "Generated Voice" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.text.ifEmpty { "Audio generation" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    IconButton(
                                        onClick = {
                                            selectedHistoryItemForPreview = item
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = "Play",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioPlayerCard(
    audioUrl: String,
    title: String = "Generated Audio",
    subtitle: String = "Audio generated successfully",
    imageUrl: String? = null
) {
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var resolvedUrl by remember { mutableStateOf<String?>(null) }
    val cacheManager = remember { com.example.data.CacheManager(context) }
    
    LaunchedEffect(audioUrl) {
        resolvedUrl = cacheManager.getCachedAudioUrl(audioUrl)
    }
    
    DisposableEffect(resolvedUrl) {
        val currentUrl = resolvedUrl ?: return@DisposableEffect onDispose {}
        
        val player = android.media.MediaPlayer().apply {
            setAudioAttributes(
                android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(currentUrl)
                prepareAsync()
                setOnPreparedListener { 
                    // Ready
                }
                setOnCompletionListener {
                    isPlaying = false
                }
            } catch (e: Exception) {
                // handle error
            }
        }
        mediaPlayer = player
        onDispose {
            player.release()
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF121212))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            coil.compose.AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
        
        Spacer(modifier = Modifier.width(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // Play Button
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer?.start()
                        isPlaying = true
                    }
                },
                modifier = Modifier
                    .size(25.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, 
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Download Button
            IconButton(
                onClick = { 
                    scope.launch { 
                        com.example.utils.downloadAudio(context, audioUrl, title)
                        android.widget.Toast.makeText(context, "Saved successfully", android.widget.Toast.LENGTH_SHORT).show()
                    } 
                },
                modifier = Modifier
                    .size(25.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowDown, 
                    contentDescription = "Download", 
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(authManager: AuthManager, onNavigateToHome: () -> Unit) {
    var isTTS by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("Transform your words into natural-sounding speech instantly. Our advanced neural voices deliver human-like emotion and clarity for any project.") }
    var sttText by remember { mutableStateOf("Ready to transcribe...") }
    
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    var userProfile by remember { mutableStateOf<com.example.data.UserProfile?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val voiceRepository = remember { com.example.data.VoiceRepository(context) }
    val allVoices by voiceRepository.allVoices.collectAsState(initial = emptyList())
    val firestoreRepository = remember { FirestoreRepository() }
    var selectedVoice by remember { mutableStateOf<VoiceEntity?>(null) }
    var showVoiceSelector by remember { mutableStateOf(false) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedAudioUrl by remember { mutableStateOf<String?>(null) }
    var generateError by remember { mutableStateOf<String?>(null) }
    var showPreviewPlayer by remember { mutableStateOf(false) }
    
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var isSttGenerating by remember { mutableStateOf(false) }
    var sttResultText by remember { mutableStateOf<String?>(null) }
    var sttError by remember { mutableStateOf<String?>(null) }
    
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedAudioUri = uri
        sttResultText = null
        sttError = null
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(user) {
        voiceRepository.syncVoicesIfNeeded()
        if (user != null) {
            val profile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
            userProfile = profile
            val savedVoiceNames = profile.savedVoices
            val savedVoices = allVoices.filter { savedVoiceNames.contains(it.voiceName) }
            if (savedVoices.isNotEmpty() && selectedVoice == null) {
                selectedVoice = savedVoices.first()
            }
        }
    }
    
    LaunchedEffect(allVoices) {
        if (userProfile != null && selectedVoice == null && allVoices.isNotEmpty()) {
            val savedVoices = allVoices.filter { userProfile!!.savedVoices.contains(it.voiceName) }
            if (savedVoices.isNotEmpty()) {
                selectedVoice = savedVoices.first()
            }
        }
    }
    
    val currentSavedVoices = allVoices.filter { userProfile?.savedVoices?.contains(it.voiceName) == true }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateToHome) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(4.dp)) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (isTTS) MaterialTheme.colorScheme.primary else Color.Transparent).clickable { isTTS = true }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text("TTS", color = if (isTTS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (!isTTS) MaterialTheme.colorScheme.primary else Color.Transparent).clickable { isTTS = false }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text("STT", color = if (!isTTS) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        
        // Main Content
        if (isTTS) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            // Text Area Container
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                TextField(
                    value = text,
                    onValueChange = { if (it.length <= 2000) text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    placeholder = {
                        Text(
                            "Type or paste your text here to generate natural AI voice...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                )
                
                // Bottom actions of text area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${text.length} / 2000",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Settings Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Voice Selector
                if (currentSavedVoices.isEmpty()) {
                    Text(
                        "No saved voices. Go to Library to add some.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(currentSavedVoices) { voice ->
                            val isSelected = selectedVoice?.voiceName == voice.voiceName
                            Row(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .clickable { selectedVoice = voice }
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val imageUrl = voice.avatarUrl.ifEmpty { voice.imageUrl.ifEmpty { "https://i.pravatar.cc/150?u=${voice.voiceName}" } }
                                coil.compose.AsyncImage(
                                    model = imageUrl,
                                    contentDescription = voice.voiceName,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = voice.voiceName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            
            // Audio Player Card Replacement
            if (generatedAudioUrl != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            showPreviewPlayer = true
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Headphones,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Listen to generated audio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Tap to preview in high fidelity player",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Preview",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Generate Button
            Button(
                onClick = { 
                    val voice = selectedVoice
                    if (voice == null) {
                        val errMsg = "Please select a voice"
                        generateError = errMsg
                        android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (text.isBlank()) {
                        val errMsg = "Please enter some text"
                        generateError = errMsg
                        android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isGenerating = true
                    generateError = null
                    generatedAudioUrl = null
                    
                    scope.launch {
                        val referenceAudio = voice.audioUrl
                        val referenceText = voice.referenceText.ifEmpty { voice.description.ifEmpty { "Example reference text for ${voice.voiceName}" } }
                        
                        val result = TtsApiManager.generateSpeech(text, referenceAudio, referenceText)
                        isGenerating = false
                        if (result.isSuccess) {
                            val audioUrl = result.getOrNull()
                            generatedAudioUrl = audioUrl
                            if (!audioUrl.isNullOrEmpty()) {
                                val historyManager = com.example.data.HistoryManager(context)
                                val historyItem = com.example.data.GenerationHistory(
                                    id = java.util.UUID.randomUUID().toString(),
                                    text = text,
                                    type = "TTS",
                                    date = System.currentTimeMillis(),
                                    voiceName = voice.voiceName,
                                    duration = "",
                                    creditsUsed = 0,
                                    audioUrl = audioUrl,
                                    imageUrl = voice.avatarUrl.ifEmpty { voice.imageUrl }
                                )
                                scope.launch {
                                    historyManager.saveHistoryItem(user?.uid, historyItem)
                                }
                            }
                            android.widget.Toast.makeText(context, "Speech generated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            val errMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                            generateError = errMsg
                            android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = "Generate")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Speech", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            // Speech-to-Text View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                
                if (sttResultText != null) {
                    Text(
                        text = "Transcription successfully completed.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 300.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                text = sttResultText!!,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Upload an audio file to transcribe",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Audio Upload Section
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        audioPickerLauncher.launch("audio/*")
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Mic, // Reusing icon for audio
                            contentDescription = "Upload Audio",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (selectedAudioUri != null) "Audio Selected" else "Tap to Upload Audio",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Button(
                    onClick = {
                        val uri = selectedAudioUri
                        if (uri == null) {
                            val errMsg = "Please select an audio file first."
                            sttError = errMsg
                            android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSttGenerating = true
                        sttError = null
                        sttResultText = null
                        
                        scope.launch {
                            val result = SttApiManager.generateText(uri.toString())
                            isSttGenerating = false
                            if (result.isSuccess) {
                                sttResultText = result.getOrNull()
                                android.widget.Toast.makeText(context, "Transcription successfully completed.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                val errMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                                sttError = errMsg
                                android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isSttGenerating
                ) {
                    if (isSttGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = "Generate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Text", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showPreviewPlayer && generatedAudioUrl != null) {
        val isFreeUser = (userProfile?.subscriptionPlan?.lowercase(java.util.Locale.getDefault()) ?: "free") == "free"
        AudioPreviewScreen(
            audioUrl = generatedAudioUrl!!,
            title = selectedVoice?.voiceName ?: "Generated Speech",
            subtitle = selectedVoice?.let { "${it.gender} • ${it.language}" } ?: "Voice Preview",
            imageUrl = selectedVoice?.avatarUrl,
            lyricsText = text,
            isFreeUser = isFreeUser,
            onBack = { showPreviewPlayer = false }
        )
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(authManager: AuthManager) {
    val itemsPerPage = 10
    var currentPage by remember { mutableIntStateOf(0) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val voiceRepository = remember { com.example.data.VoiceRepository(context) }
    val allVoices by voiceRepository.allVoices.collectAsState(initial = emptyList())
    var isSyncing by remember { mutableStateOf(false) }
    
    var filterGender by remember { mutableStateOf("All") }
    
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    var userProfile by remember { mutableStateOf<com.example.data.UserProfile?>(null) }
    
    val scope = rememberCoroutineScope()
    val firestoreRepository = remember { FirestoreRepository() }

    LaunchedEffect(user) {
        scope.launch(Dispatchers.IO) {
            isSyncing = true
            voiceRepository.syncVoicesIfNeeded()
            isSyncing = false
        }
        if (user != null) {
            userProfile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
        }
    }
    
    val filteredVoices = allVoices.filter { 
        (filterGender == "All" || it.gender == filterGender)
    }

    val totalPages = maxOf(1, (filteredVoices.size + itemsPerPage - 1) / itemsPerPage)
    
    LaunchedEffect(filteredVoices.size) {
        if (currentPage >= totalPages) currentPage = maxOf(0, totalPages - 1)
    }
    
    val currentVoices = filteredVoices.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Voice Library",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text("Gender Filter", style = MaterialTheme.typography.labelMedium)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "Male", "Female")) { gender ->
                    FilterChip(
                        selected = filterGender == gender,
                        onClick = { filterGender = gender; currentPage = 0 },
                        label = { Text(gender) }
                    )
                }
            }
        }

        if (isSyncing && allVoices.isNotEmpty()) {
            androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (allVoices.isEmpty() && isSyncing) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredVoices.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No voices found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentVoices) { voice ->
                    val isSaved = userProfile?.savedVoices?.contains(voice.voiceName) == true
                    VoiceCard(voice = voice, isSaved = isSaved, onToggleSave = {
                        if (user != null && userProfile != null) {
                            scope.launch {
                                val currentSaved = userProfile!!.savedVoices.toMutableList()
                                if (isSaved) {
                                    currentSaved.remove(voice.voiceName)
                                } else {
                                    currentSaved.add(voice.voiceName)
                                }
                                val newProfile = userProfile!!.copy(savedVoices = currentSaved)
                                firestoreRepository.saveUserProfile(user.uid, newProfile)
                                userProfile = newProfile
                            }
                        }
                    })
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous")
            }
            Text(
                text = "Page ${currentPage + 1} of $totalPages",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(
                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Rounded.ChevronRight, contentDescription = "Next")
            }
        }
    }
}
@Composable
fun VoiceCard(voice: VoiceEntity, isSaved: Boolean = false, onToggleSave: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { /* Select Voice */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Avatar
            val imageUrl = voice.avatarUrl.ifEmpty { voice.imageUrl.ifEmpty { "https://i.pravatar.cc/150?u=${voice.voiceName}" } }
            AsyncImage(
                model = imageUrl,
                contentDescription = voice.voiceName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop
            )
            // Info
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = voice.voiceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (voice.isPro) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${voice.gender} • ${voice.language}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Save button
            IconButton(
                onClick = onToggleSave,
                modifier = Modifier
                    .size(40.dp)
                    .background(if (isSaved) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    if (isSaved) Icons.Rounded.CheckCircle else Icons.Rounded.Add,
                    contentDescription = if (isSaved) "Remove" else "Add",
                    tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Play button
            val cacheManager = remember { com.example.data.CacheManager(context) }
            val scope = rememberCoroutineScope()
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                        isPlaying = false
                    } else {
                        if (mediaPlayer == null && voice.audioUrl.isNotEmpty()) {
                            scope.launch {
                                val cachedUrl = cacheManager.getCachedAudioUrl(voice.audioUrl)
                                try {
                                    mediaPlayer = android.media.MediaPlayer().apply {
                                        setDataSource(cachedUrl)
                                        prepareAsync()
                                        setOnPreparedListener { 
                                            start()
                                            isPlaying = true
                                        }
                                        setOnCompletionListener { 
                                            isPlaying = false
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.CheckCircle else Icons.Rounded.PlayArrow,
                    contentDescription = "Preview",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(authManager: AuthManager, onLogout: () -> Unit) {
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    var currentSettingsScreen by remember { mutableStateOf("main") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    
    val appSettings = LocalAppSettings.current
    val updateSettings = LocalAppSettingsUpdater.current
    val uriHandler = LocalUriHandler.current
    val lang = appSettings.language
    
    when (currentSettingsScreen) {
        "main" -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(translate("Settings", lang), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    translate("Account", lang),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.Person, title = translate("Profile", lang), onClick = { currentSettingsScreen = "profile" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Notifications, title = translate("Notification", lang), onClick = { currentSettingsScreen = "notification" })
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    translate("Preferences", lang),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.Palette, title = translate("Theme", lang), value = appSettings.theme, onClick = { currentSettingsScreen = "theme" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Language, title = translate("Language", lang), value = translate(lang, lang), onClick = { currentSettingsScreen = "language" })
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    translate("More", lang),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.HelpOutline, title = translate("Help & Support", lang), onClick = { uriHandler.openUri("mailto:support@quantisai.org") })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.PrivacyTip, title = translate("Privacy Policy", lang), onClick = { uriHandler.openUri("https://www.quantisai.org/privacy") })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Article, title = translate("Terms of Service", lang), onClick = { uriHandler.openUri("https://www.quantisai.org/terms") })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Star, title = translate("Subscription", lang), onClick = { currentSettingsScreen = "subscription" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Info, title = translate("Version", lang), value = "1.0.0", showArrow = false)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedButton(
                    onClick = {
                        authManager.signOut()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(translate("Logout", lang), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        "profile" -> {
            ProfileScreen(
                uid = user?.uid ?: "",
                email = user?.email ?: "Guest",
                lang = lang,
                onBack = { currentSettingsScreen = "main" }
            )
        }
        "notification" -> {
            NotificationScreen(
                isEnabled = notificationsEnabled,
                lang = lang,
                onToggle = { notificationsEnabled = it },
                onBack = { currentSettingsScreen = "main" }
            )
        }
        "language" -> {
            LanguageScreen(
                currentLanguage = lang,
                onSelectLanguage = { 
                    updateSettings(appSettings.copy(language = it))
                    currentSettingsScreen = "main" 
                },
                onBack = { currentSettingsScreen = "main" }
            )
        }
        "theme" -> {
            ThemeScreen(
                currentTheme = appSettings.theme,
                lang = lang,
                onSelectTheme = { 
                    updateSettings(appSettings.copy(theme = it))
                    currentSettingsScreen = "main" 
                },
                onBack = { currentSettingsScreen = "main" }
            )
        }
        "subscription" -> {
            SubscriptionScreen(authManager = authManager, onBack = { currentSettingsScreen = "main" })
        }
    }
}

@Composable
fun ProfileScreen(uid: String, email: String, lang: String, onBack: () -> Unit) {
    val firestoreRepository = remember { com.example.data.FirestoreRepository() }
    var currentProfile by remember { mutableStateOf<com.example.data.UserProfile?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && uid.isNotEmpty() && currentProfile != null) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val fileName = "avatar_${uid}_${System.currentTimeMillis()}.jpg"
                        val file = java.io.File(context.filesDir, fileName)
                        java.io.FileOutputStream(file).use { output ->
                            inputStream.use { input ->
                                input.copyTo(output)
                            }
                        }
                        val localFileUri = Uri.fromFile(file).toString()
                        val updatedProfile = currentProfile!!.copy(avatarUrl = localFileUri)
                        val success = firestoreRepository.saveUserProfile(uid, updatedProfile)
                        
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            if (success) {
                                currentProfile = updatedProfile
                                android.widget.Toast.makeText(context, "Avatar updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                android.widget.Toast.makeText(context, "Failed to update profile", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Error saving avatar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            currentProfile = firestoreRepository.getUserProfile(uid, email)
        }
    }

    val name = currentProfile?.name?.ifEmpty { "Guest User" } ?: "Loading..."
    val avatarUrl = currentProfile?.avatarUrl ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(translate("Profile", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        val planColor = getPlanColor(currentProfile?.subscriptionPlan)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(3.dp, planColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl.isNotEmpty()) {
                        coil.compose.AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                
                // Overlay plus/edit icon badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 4.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(1.5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Upload Avatar",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Text(
                text = "Tap photo to change avatar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePickerLauncher.launch("image/*") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Name", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Text("Email", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Text("Subscription Plan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = (currentProfile?.subscriptionPlan?.uppercase() ?: "FREE"),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = planColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(planColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, planColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = planColor
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationScreen(isEnabled: Boolean, lang: String, onToggle: (Boolean) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(translate("Notification", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(translate("Allow Notifications", lang), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun LanguageScreen(currentLanguage: String, onSelectLanguage: (String) -> Unit, onBack: () -> Unit) {
    val languages = listOf("English", "Spanish", "French", "German", "Italian", "Portuguese", "Russian", "Chinese", "Japanese", "Korean")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(translate("Language", currentLanguage), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
        ) {
            items(languages) { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLanguage(lang) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(translate(lang, currentLanguage), style = MaterialTheme.typography.bodyLarge)
                    if (lang == currentLanguage) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (lang != languages.last()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun ThemeScreen(currentTheme: String, lang: String, onSelectTheme: (String) -> Unit, onBack: () -> Unit) {
    val themes = listOf("default", "white", "blue", "red", "orange", "gray", "yellow")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(translate("Theme", lang), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
        ) {
            items(themes) { themeOption ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTheme(themeOption) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (themeOption == "default") com.example.ui.theme.md_theme_dark_primary else com.example.ui.theme.getThemePrimaryColor(themeOption))
                        )
                        Text(themeOption.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (themeOption == currentTheme) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                if (themeOption != themes.last()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String? = null,
    showArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
