package com.example.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AudioPreviewScreen(
    audioUrl: String,
    title: String,
    subtitle: String,
    imageUrl: String?,
    lyricsText: String?,
    isFreeUser: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cacheManager = remember { com.example.data.CacheManager(context) }
    var resolvedUrl by remember { mutableStateOf<String?>(null) }
    
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableIntStateOf(0) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var showMenu by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    
    val mediaPlayer = remember { MediaPlayer() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(audioUrl) {
        resolvedUrl = cacheManager.getCachedAudioUrl(audioUrl)
    }
    
    LaunchedEffect(resolvedUrl) {
        val currentUrl = resolvedUrl ?: return@LaunchedEffect
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(currentUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    duration = mp.duration
                    mp.start()
                    isPlaying = true
                }
                setOnCompletionListener {
                    isPlaying = false
                    currentPosition = 0
                }
            } catch (e: Exception) {
                // error handling
            }
        }
    }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isActive) {
                if (!isDraggingSlider) {
                    try {
                        currentPosition = mediaPlayer.currentPosition
                    } catch (e: Exception) {
                        // ignore if player released or error
                    }
                }
                delay(100)
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.stop()
            } catch (e: Exception) {}
            mediaPlayer.release()
        }
    }
    
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E1A47), // Deep premium dark plum/indigo
            Color(0xFF150A24), // Very deep purple
            Color(0xFF09040F)  // Pure dark/charcoal background
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .clickable(enabled = false) {} // block click propagation
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF231438))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Download",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            onClick = {
                                showMenu = false
                                scope.launch {
                                    com.example.utils.downloadAudio(context, audioUrl, title)
                                    android.widget.Toast.makeText(
                                        context,
                                        "Saved successfully to Downloads",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Audio visualizer bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val barCount = 12
                val heights = listOf(0.4f, 0.7f, 0.3f, 0.9f, 0.5f, 0.8f, 0.6f, 0.4f, 0.7f, 0.3f, 0.9f, 0.5f)
                
                repeat(barCount) { i ->
                    val barHeightFactor by animateFloatAsState(
                        targetValue = if (isPlaying) heights[i] * (0.4f + (0.6f * Math.random().toFloat())) else 0.15f,
                        animationSpec = tween(150, easing = FastOutSlowInEasing),
                        label = "bar_$i"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .width(4.dp)
                            .fillMaxHeight(barHeightFactor)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                                )
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Square cover art with dynamic progress bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                if (isFreeUser) {
                    BannerAd(
                        adUnitId = "ca-app-pub-7467637204633571~8902513793",
                        adSize = AdSize.MEDIUM_RECTANGLE,
                        modifier = Modifier
                            .size(300.dp, 250.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF150A24))
                            .border(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFF150A24))
                            .border(2.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!imageUrl.isNullOrEmpty()) {
                            coil.compose.AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                            )
                        } else {
                            // Styled voice designer square
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFFCC00), Color(0xFFFF9900))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Mic,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "LISTEN VOICE",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black,
                                            letterSpacing = 2.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Speaker Icon Badge indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeMute,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPlaying) "Playing Audio" else "Paused",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Text Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Audio Waveform Progress Bar / Time controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                AudioWaveformProgressBar(
                    currentPosition = currentPosition.toFloat(),
                    duration = if (duration > 0) duration.toFloat() else 100f,
                    isPlaying = isPlaying,
                    onSeek = { seekPos ->
                        try {
                            mediaPlayer.seekTo(seekPos.toInt())
                            currentPosition = seekPos.toInt()
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Main Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isShuffle by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { isShuffle = !isShuffle }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) Color(0xFFD63384) else Color.White.copy(alpha = 0.5f)
                    )
                }
                
                IconButton(
                    onClick = {
                        try {
                            mediaPlayer.seekTo(0)
                            currentPosition = 0
                        } catch (e: Exception) {}
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                // Play / Pause glowing gradient button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFD63384), Color(0xFF8E2DE2), Color(0xFF4A00E0))
                            )
                        )
                        .clickable {
                            try {
                                if (isPlaying) {
                                    mediaPlayer.pause()
                                    isPlaying = false
                                } else {
                                    mediaPlayer.start()
                                    isPlaying = true
                                }
                            } catch (e: Exception) {}
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
                
                IconButton(
                    onClick = {
                        try {
                            mediaPlayer.seekTo(0)
                            currentPosition = 0
                            mediaPlayer.start()
                            isPlaying = true
                        } catch (e: Exception) {}
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                var isLooping by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {
                        isLooping = !isLooping
                        try {
                            mediaPlayer.isLooping = isLooping
                        } catch (e: Exception) {}
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isLooping) Color(0xFFD63384) else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Text Prompt (Lyrics/Source) bottom panel
            var showLyrics by remember { mutableStateOf(true) }
            val promptTextToDisplay = if (!lyricsText.isNullOrBlank()) lyricsText else if (title.isNotBlank()) title else "Generated Voice Speech"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLyrics = !showLyrics }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Text Prompt",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector = if (showLyrics) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                
                if (showLyrics) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = promptTextToDisplay,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 18.sp,
                                    letterSpacing = 0.25.sp
                                ),
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        
    }
}

@Composable
fun BannerAd(adUnitId: String, adSize: AdSize = AdSize.BANNER, modifier: Modifier = Modifier) {
    val cleanAdUnitId = if (adUnitId.contains("~")) adUnitId.replace("~", "/") else adUnitId
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                this.adUnitId = cleanAdUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

private fun formatTime(ms: Int): String {
    val totalSecs = ms / 1000
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format(Locale.US, "%02d:%02d", mins, secs)
}

@Composable
fun AudioWaveformProgressBar(
    currentPosition: Float,
    duration: Float,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val barCount = 40
    val progress = if (duration > 0f) (currentPosition / duration).coerceIn(0f, 1f) else 0f

    val baseHeights = remember {
        listOf(
            0.3f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 1.0f, 0.7f, 0.5f, 0.8f,
            0.3f, 0.9f, 0.7f, 0.4f, 0.8f, 1.0f, 0.6f, 0.5f, 0.9f, 0.7f,
            0.4f, 0.8f, 0.6f, 1.0f, 0.5f, 0.7f, 0.3f, 0.8f, 0.6f, 0.9f,
            0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f, 0.9f, 0.5f, 0.7f, 0.4f
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .pointerInput(duration) {
                detectTapGestures { offset ->
                    if (duration > 0f) {
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeek(fraction * duration)
                    }
                }
            }
            .pointerInput(duration) {
                detectDragGestures { change, _ ->
                    if (duration > 0f) {
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        onSeek(fraction * duration)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            baseHeights.forEachIndexed { index, baseH ->
                val barProgress = (index.toFloat() + 0.5f) / barCount.toFloat()
                val isPassed = barProgress <= progress

                val animFactor = if (isPlaying && isPassed) {
                    0.7f + 0.3f * kotlin.math.sin(phase + index * 0.5f)
                } else if (isPlaying) {
                    0.85f + 0.15f * kotlin.math.cos(phase + index * 0.3f)
                } else {
                    1.0f
                }

                val finalHeight = (baseH * animFactor).coerceIn(0.15f, 1.0f)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .fillMaxHeight(finalHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isPassed) {
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF2D55), Color(0xFF8E2DE2))
                                )
                            } else {
                                SolidColor(Color.White.copy(alpha = 0.2f))
                            }
                        )
                )
            }
        }
    }
}
