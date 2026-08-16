package com.example.ui.screens

import androidx.compose.ui.zIndex
import com.example.ui.components.shimmerEffect
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause

import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown

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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Mic
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.MoreVert
import com.example.utils.downloadAudio
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
fun GlowingWaveformGraph(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height * 0.75f)
            cubicTo(
                width * 0.25f, height * 0.3f,
                width * 0.45f, height * 0.95f,
                width * 0.7f, height * 0.15f
            )
            cubicTo(
                width * 0.85f, height * 0.35f,
                width * 0.95f, height * 0.1f,
                width, height * 0.05f
            )
        }
        
        drawPath(
            path = path,
            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                colors = listOf(
                    secondaryColor,
                    primaryColor,
                    secondaryColor
                )
            ),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 3.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
        
        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.2f),
                    Color.Transparent
                )
            )
        )
    }
}

class CurvedCutoutNavShape(
    private val cornerRadius: Dp = 28.dp,
    private val domeRadius: Dp = 34.dp,
    private val domeHeight: Dp = 22.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cornerRadius.toPx() }
        val dr = with(density) { domeRadius.toPx() }
        val dh = with(density) { domeHeight.toPx() }
        val cx = size.width / 2f

        val path = Path().apply {
            moveTo(r, 0f)
            lineTo(cx - dr * 1.25f, 0f)

            // Smooth curve UP over the central circle
            cubicTo(
                cx - dr * 0.7f, 0f,
                cx - dr * 0.7f, -dh,
                cx, -dh
            )
            cubicTo(
                cx + dr * 0.7f, -dh,
                cx + dr * 0.7f, 0f,
                cx + dr * 1.25f, 0f
            )

            lineTo(size.width - r, 0f)

            arcTo(
                rect = Rect(size.width - 2 * r, 0f, size.width, 2 * r),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(size.width, size.height - r)

            arcTo(
                rect = Rect(size.width - 2 * r, size.height - 2 * r, size.width, size.height),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(r, size.height)

            arcTo(
                rect = Rect(0f, size.height - 2 * r, 2 * r, size.height),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            lineTo(0f, r)

            arcTo(
                rect = Rect(0f, 0f, 2 * r, 2 * r),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun NavItemComponent(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0x28FFFFFF) else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF8E92A8),
            modifier = Modifier.size(if (isSelected) 25.dp else 22.dp)
        )
    }
}

@Composable
fun MainScreen(authManager: AuthManager, onLogout: () -> Unit, onNavigateToWatchAd: () -> Unit) {
    var currentTab by remember { mutableIntStateOf(0) }
    var showSubscriptionModal by remember { mutableStateOf(false) }
    val appSettings = LocalAppSettings.current
    val lang = appSettings.language
    
    Scaffold(
        containerColor = Color(0xFF090A10),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Main Content Area with bottom padding for the navbar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(bottom = if (currentTab == 1 || currentTab == 2) 0.dp else 64.dp)
            ) {
                when (currentTab) {
                    0 -> HomeScreen(authManager, onNavigateToWatchAd)
                    1 -> GenerateScreen(
                        authManager = authManager,
                        onNavigateToHome = { currentTab = 0 },
                        onNavigateToAccount = { currentTab = 4 }
                    )
                    2 -> VoiceDesignScreen(
                        authManager = authManager,
                        onBack = { currentTab = 0 },
                        onNavigateToTts = { currentTab = 1 },
                        onNavigateToSubscription = { showSubscriptionModal = true },
                        onNavigateToAccount = { currentTab = 4 }
                    )
                    3 -> LibraryScreen(authManager)
                    4 -> AccountScreen(authManager, onLogout)
                }
            }
            
            if (showSubscriptionModal) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100f)
                ) {
                    SubscriptionScreen(
                        authManager = authManager,
                        onBack = { showSubscriptionModal = false }
                    )
                }
            }
            
            // Custom Curved Bottom Navigation Bar
            if (currentTab != 1 && currentTab != 2) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 4.dp)
                        .fillMaxWidth()
                ) {
                val navShape = remember { CurvedCutoutNavShape(cornerRadius = 24.dp, domeRadius = 32.dp, domeHeight = 20.dp) }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            shape = navShape
                            clip = true
                        }
                        .background(Color(0xFF13141F))
                        .border(width = 1.dp, color = Color(0x25FFFFFF), shape = navShape)
                        .padding(top = 6.dp, bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItemComponent(
                            label = translate("Home", lang),
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home,
                            isSelected = currentTab == 0,
                            onClick = { currentTab = 0 }
                        )

                        NavItemComponent(
                            label = translate("Design", lang),
                            selectedIcon = Icons.Filled.GraphicEq,
                            unselectedIcon = Icons.Outlined.GraphicEq,
                            isSelected = currentTab == 2,
                            onClick = { currentTab = 2 }
                        )

                        Spacer(modifier = Modifier.width(56.dp))

                        NavItemComponent(
                            label = translate("Library", lang),
                            selectedIcon = Icons.Filled.LibraryMusic,
                            unselectedIcon = Icons.Outlined.LibraryMusic,
                            isSelected = currentTab == 3,
                            onClick = { currentTab = 3 }
                        )

                        NavItemComponent(
                            label = translate("Account", lang),
                            selectedIcon = Icons.Filled.Person,
                            unselectedIcon = Icons.Outlined.PersonOutline,
                            isSelected = currentTab == 4,
                            onClick = { currentTab = 4 }
                        )
                    }
                }

                // Center Floating Mic Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-18).dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .border(width = 3.dp, color = Color(0xFF13141F), shape = CircleShape)
                        .clickable { currentTab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = translate("Generate", lang),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
}

@Composable
fun HomeScreen(authManager: AuthManager, onNavigateToWatchAd: () -> Unit) {
    val context = LocalContext.current
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
        recentGenerations = historyManager.getLocalHistory(user?.uid)
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
            recentGenerations = history
        } else {
            recentGenerations = historyManager.getLocalHistory(null)
            isLoading = false
        }
    }

    val item = selectedHistoryItemForPreview
    if (item != null) {
        val isFreeUser = userPlan.lowercase(java.util.Locale.getDefault()) == "free"
        AudioPreviewScreen(
            audioUrl = item.audioUrl,
            title = item.voiceName.ifEmpty { "Generated Speech" },
            subtitle = item.type.ifEmpty { "Audio generated successfully" },
            imageUrl = item.imageUrl.ifEmpty { null },
            lyricsText = item.text,
            isFreeUser = isFreeUser,
            onBack = { selectedHistoryItemForPreview = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090A10))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header: Welcome back & Avatar
            val displayUser = userName.ifEmpty { user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Abhishek del Mundu" }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👋 Welcome back,",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = displayUser,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Glowing Avatar Badge
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF131522)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userAvatar.isNotEmpty()) {
                            coil.compose.AsyncImage(
                                model = userAvatar,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initial = displayUser.firstOrNull()?.toString()?.uppercase() ?: "A"
                            Text(
                                text = initial,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Top Row Cards: Credits Available & Watch Ad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Credits Available
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF131420))
                        .border(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.GraphicEq,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Credits Available",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            val formattedCredits = if (isLoading) "..." else if (credits == 0) "38,612" else java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(credits)
                            Text(
                                text = formattedCredits,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Total Credits",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    
                    // Wave graphic inside credits card
                    GlowingWaveformGraph(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(55.dp)
                    )
                }
                
                // Card 2: Watch Ad
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF131420))
                        .border(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(Color(0x303B82F6), Color(0x10A855F7))
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Watch Ad",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+30 Credits",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                        
                        // Watch Now Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(Color(0x30A855F7), Color(0x203B82F6))
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(Color(0x60A855F7), Color(0x403B82F6))
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onNavigateToWatchAd() }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Watch Now",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Rounded.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Recent Activity Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        val countText = "${recentGenerations.size} audio(s)"
                        Text(
                            text = countText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(3) {
                            com.example.ui.components.VoiceCardSkeleton()
                        }
                    }
                } else if (recentGenerations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF12131F))
                            .border(
                                width = 1.dp,
                                color = Color(0x1AFFFFFF),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x222E3254)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "No Audio Generations Yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Your generated speech and audio recordings will appear here privately.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recentGenerations.forEach { historyItem ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF12131F))
                                .border(
                                    width = 1.dp,
                                    color = Color(0x1AFFFFFF),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (historyItem.audioUrl.isNotEmpty()) {
                                        selectedHistoryItemForPreview = historyItem
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Waveform Icon Box
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x222E3254)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (historyItem.imageUrl.isNotEmpty() && historyItem.imageUrl.startsWith("http")) {
                                        coil.compose.AsyncImage(
                                            model = historyItem.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        val vName = historyItem.voiceName.ifEmpty { "Generated Audio" }
                                        val initial = vName.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
                                            ?: "A"
                                        Text(
                                            text = initial,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = historyItem.voiceName.ifEmpty { "Generated Audio" },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = historyItem.text.ifEmpty { "Audio generation" },
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Delete Button with Gradient Ring (styled like play button)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(Color(0xFFEF4444), Color(0xFFF43F5E))
                                            )
                                        )
                                        .padding(1.5.dp)
                                        .clickable {
                                            scope.launch {
                                                historyManager.deleteHistoryItem(user?.uid, historyItem)
                                                recentGenerations = historyManager.getLocalHistory(user?.uid)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFF131420)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFF87171),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Play Button with Gradient Ring
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                        )
                                        .padding(1.5.dp)
                                        .clickable {
                                            if (historyItem.audioUrl.isNotEmpty()) {
                                                selectedHistoryItemForPreview = historyItem
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFF131420)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.White,
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
        val hasHttpImage = !imageUrl.isNullOrEmpty() && imageUrl.startsWith("http")
        if (hasHttpImage) {
            coil.compose.AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            val initial = title.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() ?: "V"
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
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
                    try {
                        if (isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        } else {
                            mediaPlayer?.start()
                            isPlaying = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AudioPlayerCard", "Error controlling mediaPlayer", e)
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

data class TtsChatMessageItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val voiceName: String = "",
    val voiceAvatar: String = "",
    val userAvatar: String = "",
    val audioUrl: String? = null,
    val isLoading: Boolean = false,
    val isLiked: Boolean = false,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreen(
    authManager: AuthManager,
    onNavigateToHome: () -> Unit,
    onNavigateToAccount: (() -> Unit)? = null
) {
    var inputText by remember { mutableStateOf("") }
    
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    var userProfile by remember { mutableStateOf<com.example.data.UserProfile?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val voiceRepository = remember { com.example.data.VoiceRepository(context) }
    val allVoices by voiceRepository.allVoices.collectAsState(initial = emptyList())
    val firestoreRepository = remember { FirestoreRepository() }
    var selectedVoice by remember { mutableStateOf<VoiceEntity?>(null) }

    var generatedAudioUrl by remember { mutableStateOf<String?>(null) }
    var showPreviewPlayer by remember { mutableStateOf(false) }
    var showDisclaimerDialog by remember { mutableStateOf(false) }
    
    var activePlayingAudioUrl by remember { mutableStateOf<String?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }

    val chatMessages = remember {
        mutableStateListOf<TtsChatMessageItem>(
            TtsChatMessageItem(
                isUser = false,
                text = "Welcome to Text to Speech! Write your script below, pick a speaker voice, and send to synthesize natural human speech.",
                voiceName = "Voice AI"
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    val speechToTextLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val newCombined = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
                inputText = newCombined.take(1500)
            }
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(user) {
        voiceRepository.syncVoicesIfNeeded()
        if (user != null) {
            val profile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
            userProfile = profile
        }
    }
    
    val currentSavedVoices = remember(allVoices, userProfile) {
        allVoices.filter { userProfile?.savedVoices?.contains(it.voiceName) == true }
    }
    
    val availableVoices = remember(allVoices, currentSavedVoices) {
        if (currentSavedVoices.isNotEmpty()) currentSavedVoices else allVoices.take(15)
    }

    LaunchedEffect(availableVoices) {
        if (selectedVoice == null && availableVoices.isNotEmpty()) {
            selectedVoice = availableVoices.first()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Text to speech",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // TTS Chat View
            val listState = rememberLazyListState()
                LaunchedEffect(chatMessages.size) {
                    if (chatMessages.isNotEmpty()) {
                        listState.animateScrollToItem(chatMessages.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        if (msg.isUser) {
                            // User Message Bubble (Right)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                val userAvatar = msg.userAvatar.ifEmpty { userProfile?.avatarUrl ?: "" }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (userAvatar.isNotEmpty()) {
                                        coil.compose.AsyncImage(
                                            model = userAvatar,
                                            contentDescription = "User Profile Avatar",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = "User Profile Avatar",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // AI Message Bubble (Left)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Top
                            ) {
                                val hasHttpAvatar = msg.voiceAvatar.isNotBlank() && msg.voiceAvatar.startsWith("http")
                                if (hasHttpAvatar) {
                                    coil.compose.AsyncImage(
                                        model = msg.voiceAvatar,
                                        contentDescription = msg.voiceName,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                } else {
                                    val initial = msg.voiceName.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
                                        ?: "V"
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                                        MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                            )
                                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 290.dp)
                                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp))
                                            .padding(14.dp)
                                    ) {
                                        if (msg.isLoading) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        strokeWidth = 2.dp
                                                    )
                                                    Text(
                                                        text = msg.text,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(16.dp)
                                                        .shimmerEffect()
                                                )
                                            }
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                                Text(
                                                    text = msg.text,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 14.sp
                                                )

                                                if (msg.audioUrl != null) {
                                                    val isThisPlaying = activePlayingAudioUrl == msg.audioUrl && isAudioPlaying
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(16.dp))
                                                            .background(MaterialTheme.colorScheme.surface)
                                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                                            .clickable {
                                                                if (isThisPlaying) {
                                                                    mediaPlayer?.pause()
                                                                    isAudioPlaying = false
                                                                } else {
                                                                    try {
                                                                        mediaPlayer?.release()
                                                                        mediaPlayer = android.media.MediaPlayer().apply {
                                                                            setDataSource(msg.audioUrl)
                                                                            setOnPreparedListener {
                                                                                start()
                                                                                isAudioPlaying = true
                                                                                activePlayingAudioUrl = msg.audioUrl
                                                                            }
                                                                            setOnCompletionListener {
                                                                                isAudioPlaying = false
                                                                                activePlayingAudioUrl = null
                                                                            }
                                                                            prepareAsync()
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        android.widget.Toast.makeText(context, "Error playing audio", android.widget.Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }
                                                            }
                                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(36.dp)
                                                                    .clip(CircleShape)
                                                                    .background(MaterialTheme.colorScheme.primary),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = if (isThisPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                                                    contentDescription = "Play",
                                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                                    modifier = Modifier.size(22.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column {
                                                                Text(
                                                                    text = "Generated Audio (${msg.voiceName})",
                                                                    style = MaterialTheme.typography.labelMedium,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = MaterialTheme.colorScheme.onSurface
                                                                )
                                                                Text(
                                                                    text = if (isThisPlaying) "Playing..." else "Tap to listen",
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                            }
                                                        }

                                                        IconButton(
                                                            onClick = {
                                                                generatedAudioUrl = msg.audioUrl
                                                                showPreviewPlayer = true
                                                            },
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Rounded.Headphones,
                                                                contentDescription = "Full Player",
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Action Icons Row under AI Message
                                    if (!msg.isLoading) {
                                        Row(
                                            modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Copy Icon
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                    val clip = android.content.ClipData.newPlainText("Script", msg.text)
                                                    clipboard.setPrimaryClip(clip)
                                                    android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ContentCopy,
                                                    contentDescription = "Copy",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // ThumbsUp / Like Icon
                                            val msgIdx = chatMessages.indexOfFirst { it.id == msg.id }
                                            IconButton(
                                                onClick = {
                                                    if (msgIdx != -1) {
                                                        chatMessages[msgIdx] = chatMessages[msgIdx].copy(isLiked = !chatMessages[msgIdx].isLiked)
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (msg.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                    contentDescription = "Like",
                                                    tint = if (msg.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            // Listen / Volume Icon
                                            if (msg.audioUrl != null) {
                                                IconButton(
                                                    onClick = {
                                                        try {
                                                            mediaPlayer?.release()
                                                            mediaPlayer = android.media.MediaPlayer().apply {
                                                                setDataSource(msg.audioUrl)
                                                                setOnPreparedListener {
                                                                    start()
                                                                    isAudioPlaying = true
                                                                    activePlayingAudioUrl = msg.audioUrl
                                                                }
                                                                setOnCompletionListener {
                                                                    isAudioPlaying = false
                                                                    activePlayingAudioUrl = null
                                                                }
                                                                prepareAsync()
                                                            }
                                                        } catch (e: Exception) {
                                                            android.widget.Toast.makeText(context, "Audio preview unavailable", android.widget.Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.VolumeUp,
                                                        contentDescription = "Listen",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            // Download Icon
                                            if (msg.audioUrl != null) {
                                                IconButton(
                                                    onClick = {
                                                        scope.launch {
                                                            downloadAudio(context, msg.audioUrl, "Speech_${msg.voiceName}_${System.currentTimeMillis()}")
                                                            android.widget.Toast.makeText(context, "Downloading audio file...", android.widget.Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.FileDownload,
                                                        contentDescription = "Download",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            // Recycle / Regenerate Icon
                                            IconButton(
                                                onClick = {
                                                    val promptMsg = chatMessages.take(msgIdx).lastOrNull { it.isUser }?.text ?: msg.text
                                                    val voice = availableVoices.firstOrNull { it.voiceName == msg.voiceName } ?: selectedVoice
                                                    if (voice != null && promptMsg.isNotBlank()) {
                                                        val newAiId = java.util.UUID.randomUUID().toString()
                                                        chatMessages.add(
                                                            TtsChatMessageItem(
                                                                id = newAiId,
                                                                isUser = false,
                                                                text = "Re-generating speech with ${voice.voiceName}...",
                                                                voiceName = voice.voiceName,
                                                                voiceAvatar = voice.avatarUrl.ifEmpty { voice.imageUrl },
                                                                isLoading = true
                                                            )
                                                        )
                                                        scope.launch {
                                                            val res = TtsApiManager.generateSpeech(
                                                                promptMsg,
                                                                voice.audioUrl,
                                                                voice.referenceText.ifEmpty { voice.description }
                                                            )
                                                            val idx = chatMessages.indexOfFirst { it.id == newAiId }
                                                            if (res.isSuccess) {
                                                                val u = res.getOrNull()
                                                                if (idx != -1) {
                                                                    chatMessages[idx] = chatMessages[idx].copy(
                                                                        text = "Audio generated successfully! Listen below:",
                                                                        audioUrl = u,
                                                                        isLoading = false
                                                                    )
                                                                }
                                                            } else {
                                                                if (idx != -1) {
                                                                    chatMessages[idx] = chatMessages[idx].copy(
                                                                        text = "Regeneration failed",
                                                                        isLoading = false
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Refresh,
                                                    contentDescription = "Regenerate",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Speaker Selector & Input Bar (No Title "Speaker", Theme Colors, No Gap)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
                ) {
                    // Speaker Selector Pills (Title "Speaker" removed!)
                    if (availableVoices.isNotEmpty()) {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            items(availableVoices) { voice ->
                                val isSelected = selectedVoice?.voiceName == voice.voiceName
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedVoice = voice }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val rawUrl = voice.avatarUrl.ifEmpty { voice.imageUrl }
                                    val hasHttpImage = rawUrl.isNotBlank() && rawUrl.startsWith("http") && voice.gender != "Custom"
                                    if (hasHttpImage) {
                                        coil.compose.AsyncImage(
                                            model = rawUrl,
                                            contentDescription = voice.voiceName,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    } else {
                                        val initial = voice.voiceName.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
                                            ?: "V"
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = initial,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = voice.voiceName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Script Text Area with Default Google Speech To Text Mic & Send Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Google Speech to Text Mic Button
                        IconButton(
                            onClick = {
                                val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now to convert voice to text...")
                                }
                                try {
                                    speechToTextLauncher.launch(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Speech recognition is not available on this device", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Mic,
                                contentDescription = "Speech to Text Mic",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it.take(1500) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            placeholder = {
                                Text(
                                    text = "Write or speak your script here...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            maxLines = 4
                        )

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    if (inputText.isBlank()) {
                                        android.widget.Toast.makeText(context, "Please write or speak a script first", android.widget.Toast.LENGTH_SHORT).show()
                                        return@clickable
                                    }
                                    val voice = selectedVoice
                                    if (voice == null) {
                                        android.widget.Toast.makeText(context, "Please select a speaker voice", android.widget.Toast.LENGTH_SHORT).show()
                                        return@clickable
                                    }

                                    val scriptText = inputText
                                    inputText = ""

                                    val userMsg = TtsChatMessageItem(
                                        isUser = true,
                                        text = scriptText,
                                        voiceName = voice.voiceName,
                                        userAvatar = userProfile?.avatarUrl ?: ""
                                    )
                                    chatMessages.add(userMsg)

                                    val aiMsgId = java.util.UUID.randomUUID().toString()
                                    val aiMsg = TtsChatMessageItem(
                                        id = aiMsgId,
                                        isUser = false,
                                        text = "Synthesizing voice with ${voice.voiceName}...",
                                        voiceName = voice.voiceName,
                                        voiceAvatar = voice.avatarUrl.ifEmpty { voice.imageUrl },
                                        isLoading = true
                                    )
                                    chatMessages.add(aiMsg)

                                    scope.launch {
                                        val refAudio = voice.audioUrl
                                        val refText = voice.referenceText.ifEmpty { voice.description.ifEmpty { "Reference text for ${voice.voiceName}" } }
                                        val result = TtsApiManager.generateSpeech(scriptText, refAudio, refText)

                                        val idx = chatMessages.indexOfFirst { it.id == aiMsgId }
                                        if (result.isSuccess) {
                                            val audioUrl = result.getOrNull()
                                            if (idx != -1) {
                                                chatMessages[idx] = chatMessages[idx].copy(
                                                    text = "Audio generated successfully! Listen below:",
                                                    audioUrl = audioUrl,
                                                    isLoading = false
                                                )
                                            }
                                            if (!audioUrl.isNullOrEmpty()) {
                                                val historyManager = com.example.data.HistoryManager(context)
                                                val historyItem = com.example.data.GenerationHistory(
                                                    id = java.util.UUID.randomUUID().toString(),
                                                    text = scriptText,
                                                    type = "TTS",
                                                    date = System.currentTimeMillis(),
                                                    voiceName = voice.voiceName,
                                                    duration = "",
                                                    creditsUsed = 0,
                                                    audioUrl = audioUrl,
                                                    imageUrl = voice.avatarUrl.ifEmpty { voice.imageUrl }
                                                )
                                                historyManager.saveHistoryItem(user?.uid, historyItem)
                                            }
                                        } else {
                                            val errMsg = result.exceptionOrNull()?.message ?: "Generation error"
                                            if (idx != -1) {
                                                chatMessages[idx] = chatMessages[idx].copy(
                                                    text = "Error generating audio: $errMsg",
                                                    isLoading = false
                                                )
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Disclaimer Notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI-generated audio may contain mistakes. Please review before use.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

    if (showPreviewPlayer && generatedAudioUrl != null) {
        val isFreeUser = (userProfile?.subscriptionPlan?.lowercase(java.util.Locale.getDefault()) ?: "free") == "free"
        AudioPreviewScreen(
            audioUrl = generatedAudioUrl ?: "",
            title = selectedVoice?.voiceName ?: "Generated Speech",
            subtitle = selectedVoice?.let { "${it.gender} • ${it.language}" } ?: "Voice Preview",
            imageUrl = selectedVoice?.avatarUrl,
            lyricsText = inputText,
            isFreeUser = isFreeUser,
            onBack = { showPreviewPlayer = false }
        )
    }

    // Disclaimer Info Modal Dialog
    if (showDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showDisclaimerDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "AI Audio Disclaimer & Guidelines",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• AI-generated speech is produced automatically using neural speech models.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Pronunciations, pitch, or emotional tone may occasionally contain minor inaccuracies.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Please review and listen to all synthesized audio before publishing or using in production.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Credits/quota are deducted based on character length during audio generation.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisclaimerDialog = false }) {
                    Text("Understood", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(authManager: AuthManager) {
    val itemsPerPage = 15
    var currentPage by remember { mutableIntStateOf(0) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val voiceRepository = remember { com.example.data.VoiceRepository(context) }
    val allVoices by voiceRepository.allVoices.collectAsState(initial = emptyList())
    var isSyncing by remember { mutableStateOf(false) }
    
    var filterTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
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
    
    val recentNewVoiceNames = remember(allVoices) { allVoices.take(10).map { it.voiceName }.toSet() }

    val filteredVoices = allVoices.filter { voice ->
        val matchesTab = when (filterTab) {
            "Male" -> voice.gender.equals("Male", ignoreCase = true)
            "Female" -> voice.gender.equals("Female", ignoreCase = true)
            "✨ New" -> recentNewVoiceNames.contains(voice.voiceName)
            else -> true
        }
        val matchesSearch = searchQuery.isEmpty() || 
            voice.voiceName.contains(searchQuery, ignoreCase = true) || 
            voice.language.contains(searchQuery, ignoreCase = true) ||
            voice.description.contains(searchQuery, ignoreCase = true)
        
        matchesTab && matchesSearch
    }

    val totalPages = maxOf(1, (filteredVoices.size + itemsPerPage - 1) / itemsPerPage)
    
    LaunchedEffect(filteredVoices.size) {
        if (currentPage >= totalPages) currentPage = maxOf(0, totalPages - 1)
    }
    
    val currentVoices = filteredVoices.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFF090A15))
    ) {
        // Top Header Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Voice Library",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )

                IconButton(
                    onClick = { isSearchActive = !isSearchActive },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            androidx.compose.ui.graphics.Color(0xFF14172B),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            androidx.compose.ui.graphics.Color(0xFF262943),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Discover the voices in our library! 🎙️✨",
                fontSize = 13.sp,
                color = androidx.compose.ui.graphics.Color(0xFF94A3B8)
            )

            if (isSearchActive) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it; currentPage = 0 },
                    placeholder = { Text("Search voice by name...", color = androidx.compose.ui.graphics.Color(0xFF64748B), fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF131527),
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF131527),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF262943),
                        focusedTextColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedTextColor = androidx.compose.ui.graphics.Color.White
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = androidx.compose.ui.graphics.Color.White)
                            }
                        }
                    }
                )
            }
        }

        // Category Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val categories = listOf("All", "Male", "Female", "🔥 New")
            categories.forEach { category ->
                val isSelected = filterTab == category || (filterTab == "✨ New" && category == "🔥 New")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else androidx.compose.ui.graphics.Color(0xFF131527)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else androidx.compose.ui.graphics.Color(0xFF262943),
                            shape = RoundedCornerShape(50)
                        )
                        .clickable {
                            filterTab = category
                            currentPage = 0
                        }
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else androidx.compose.ui.graphics.Color(0xFF94A3B8),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isSyncing && allVoices.isNotEmpty()) {
            androidx.compose.material3.LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Voice Cards Grid
        if (allVoices.isEmpty() && isSyncing) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                com.example.ui.components.VoiceGridSkeleton(count = 9)
            }
        } else if (filteredVoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No voices found", color = androidx.compose.ui.graphics.Color(0xFF94A3B8))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentVoices, key = { it.voiceName }) { voice ->
                    val isSaved = userProfile?.savedVoices?.contains(voice.voiceName) == true
                    val isNew = recentNewVoiceNames.contains(voice.voiceName)
                    GridVoiceCard(
                        voice = voice,
                        isSaved = isSaved,
                        isNew = isNew,
                        onToggleSave = {
                            val profile = userProfile
                            if (user != null && profile != null) {
                                scope.launch {
                                    val currentSaved = profile.savedVoices.toMutableList()
                                    if (isSaved) {
                                        currentSaved.remove(voice.voiceName)
                                    } else {
                                        currentSaved.add(voice.voiceName)
                                    }
                                    val newProfile = profile.copy(savedVoices = currentSaved)
                                    firestoreRepository.saveUserProfile(user.uid, newProfile)
                                    userProfile = newProfile
                                }
                            }
                        }
                    )
                }
            }
        }

        // Pagination Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = currentPage > 0) {
                        if (currentPage > 0) currentPage--
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous",
                    tint = if (currentPage > 0) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFF475569)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Previous",
                    color = if (currentPage > 0) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFF475569),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "Page ${currentPage + 1} of $totalPages",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.Color.White
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = currentPage < totalPages - 1) {
                        if (currentPage < totalPages - 1) currentPage++
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next",
                    color = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFF475569),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Next",
                    tint = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFF475569)
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun GridVoiceCard(
    voice: VoiceEntity,
    isSaved: Boolean = false,
    isNew: Boolean = false,
    onToggleSave: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    val avatarBgColors = listOf(
        androidx.compose.ui.graphics.Color(0xFF00B4D8), // Cyan
        androidx.compose.ui.graphics.Color(0xFF38B000), // Green
        androidx.compose.ui.graphics.Color(0xFFF77F00), // Orange/Amber
        androidx.compose.ui.graphics.Color(0xFFE056FD), // Pink/Magenta
        androidx.compose.ui.graphics.Color(0xFFFFB703), // Yellow
        androidx.compose.ui.graphics.Color(0xFF0077B6), // Ocean Blue
        androidx.compose.ui.graphics.Color(0xFF7209B7), // Deep Purple
        androidx.compose.ui.graphics.Color(0xFFD62828), // Red/Rose
        androidx.compose.ui.graphics.Color(0xFF14B8A6)  // Teal
    )
    val bgColor = avatarBgColors[Math.abs(voice.voiceName.hashCode()) % avatarBgColors.size]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0x332E3254),
                        androidx.compose.ui.graphics.Color(0x1F1E223D)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0x40A855F7),
                        androidx.compose.ui.graphics.Color(0x203B82F6),
                        androidx.compose.ui.graphics.Color(0x15FFFFFF)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(top = 10.dp, bottom = 12.dp, start = 6.dp, end = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 100% Circular Avatar
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                val rawUrl = voice.avatarUrl.ifEmpty { voice.imageUrl }
                val hasHttpImage = rawUrl.isNotBlank() && rawUrl.startsWith("http") && voice.gender != "Custom"
                if (hasHttpImage) {
                    AsyncImage(
                        model = rawUrl,
                        contentDescription = voice.voiceName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val initial = voice.voiceName.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
                        ?: voice.voiceName.trim().firstOrNull()?.uppercaseChar()?.toString()
                        ?: "V"
                    Text(
                        text = initial,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Voice Name
            Text(
                text = voice.voiceName,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Refined Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(androidx.compose.ui.graphics.Color(0x20FFFFFF))
                        .border(
                            width = 1.dp,
                            color = androidx.compose.ui.graphics.Color(0x40FFFFFF),
                            shape = CircleShape
                        )
                        .clickable { onToggleSave() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isSaved) "Remove" else "Save",
                        tint = if (isSaved) androidx.compose.ui.graphics.Color(0xFFEF4444) else androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Play Button
                val cacheManager = remember { com.example.data.CacheManager(context) }
                val scope = rememberCoroutineScope()

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(
                            width = 1.5.dp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
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
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Top-Right "New 🔥" Badge inside card frame (rendered in front)
        if (isNew) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 6.dp, top = 2.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.graphics.Color(0x99131527))
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0x60FF9800),
                        shape = CircleShape
                    )
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "New 🔥",
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun VoiceCard(voice: VoiceEntity, isSaved: Boolean = false, onToggleSave: () -> Unit = {}) {
    GridVoiceCard(voice = voice, isSaved = isSaved, onToggleSave = onToggleSave)
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
                    .background(Color(0xFF090A10))
                    .padding(20.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = translate("Settings", lang),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = translate("Account", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF131420))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.Person, title = translate("Profile", lang), onClick = { currentSettingsScreen = "profile" })
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Notifications, title = translate("Notification", lang), onClick = { currentSettingsScreen = "notification" })
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = translate("Preferences", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF131420))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.Palette, title = translate("Theme", lang), value = appSettings.theme.replaceFirstChar { it.uppercase() }, onClick = { currentSettingsScreen = "theme" })
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = translate("More", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF131420))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                ) {
                    SettingsItem(icon = Icons.Rounded.HelpOutline, title = translate("Help & Support", lang), onClick = { uriHandler.openUri("mailto:support@quantisai.org") })
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.PrivacyTip, title = translate("Privacy Policy", lang), onClick = { uriHandler.openUri("https://www.quantisai.org/privacy") })
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Article, title = translate("Terms of Service", lang), onClick = { uriHandler.openUri("https://www.quantisai.org/terms") })
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Star, title = translate("Subscription", lang), onClick = { currentSettingsScreen = "subscription" })
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(icon = Icons.Rounded.Info, title = translate("Version", lang), value = "v${com.example.BuildConfig.VERSION_NAME}", showArrow = false)
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x1DEF4444))
                        .border(1.dp, Color(0x40EF4444), RoundedCornerShape(20.dp))
                        .clickable {
                            authManager.signOut()
                            onLogout()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = translate("Logout", lang),
                            color = Color(0xFFF87171),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
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
                        val updatedProfile = currentProfile?.copy(avatarUrl = localFileUri)
                        val success = if (updatedProfile != null) firestoreRepository.saveUserProfile(uid, updatedProfile) else false
                        
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            if (success) {
                                if (updatedProfile != null) currentProfile = updatedProfile
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
            .background(Color(0xFF090A10))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = translate("Profile", lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        val planColor = getPlanColor(currentProfile?.subscriptionPlan)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF131420))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .padding(20.dp),
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
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF131522)),
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
                            Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White)
                        }
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
                        .border(1.5.dp, Color(0xFF131420), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Upload Avatar",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
            
            Text(
                text = "Tap photo to change avatar",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePickerLauncher.launch("image/*") }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text("Name", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Text(name, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            
            HorizontalDivider(color = Color(0x12FFFFFF))
            
            Text("Email", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Text(email, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.SemiBold)

            HorizontalDivider(color = Color(0x12FFFFFF))
            
            Text("Subscription Plan", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = (currentProfile?.subscriptionPlan?.uppercase() ?: "FREE"),
                    fontSize = 15.sp,
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
            .background(Color(0xFF090A10))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = translate("Notification", lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF131420))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = translate("Allow Notifications", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color(0xFF64748B),
                    uncheckedTrackColor = Color(0xFF1E1F30)
                )
            )
        }
    }
}

@Composable
fun ThemeScreen(currentTheme: String, lang: String, onSelectTheme: (String) -> Unit, onBack: () -> Unit) {
    val themes = listOf("white", "purple", "blue", "red", "orange", "gray", "yellow", "green", "pink", "default")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A10))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = translate("Theme", lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF131420))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
        ) {
            items(themes) { themeOption ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTheme(themeOption) }
                        .padding(18.dp),
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
                                .background(com.example.ui.theme.getThemePrimaryColor(themeOption))
                        )
                        Text(
                            text = themeOption.replaceFirstChar { it.uppercase() },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                    if (themeOption == currentTheme) {
                        val themeColor = com.example.ui.theme.getThemePrimaryColor(themeOption)
                        val iconTint = com.example.ui.theme.getOnPrimaryColor(themeOption)
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(themeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = iconTint,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                if (themeOption != themes.last()) {
                    HorizontalDivider(color = Color(0x12FFFFFF), modifier = Modifier.padding(horizontal = 16.dp))
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
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            if (showArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
