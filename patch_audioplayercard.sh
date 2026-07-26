cat << 'INNER_EOF' > replacement.txt
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
        if (imageUrl != null && imageUrl.isNotEmpty()) {
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
                Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                
                IconButton(
                    onClick = { com.example.utils.downloadAudio(context, audioUrl, title) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Rounded.Download, contentDescription = "Download", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.FavoriteBorder, contentDescription = "Like", tint = Color.Gray, modifier = Modifier.size(24.dp))
                
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
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, 
                        contentDescription = "Play/Pause",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
INNER_EOF

# Find start and end line of AudioPlayerCard function
start_line=$(grep -n "@Composable" app/src/main/java/com/example/ui/screens/MainScreens.kt | grep -A 1 "fun AudioPlayerCard" | head -n 1 | cut -d: -f1)
# Because grep -A 1 might not give the start line of @Composable correctly if there are multiple.
# Let's just find "fun AudioPlayerCard(" and go one line up.
func_line=$(grep -n "fun AudioPlayerCard(" app/src/main/java/com/example/ui/screens/MainScreens.kt | cut -d: -f1)
start_line=$((func_line - 1))

# Find the end of the function (a bit tricky, let's just use awk or manual replacement)
