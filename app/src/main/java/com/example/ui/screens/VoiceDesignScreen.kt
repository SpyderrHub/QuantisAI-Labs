package com.example.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.TtsApiManager
import com.example.auth.AuthManager
import com.example.data.FirestoreRepository
import com.example.data.GenerationHistory
import com.example.data.HistoryManager
import com.example.data.UserProfile
import com.example.data.VoiceEntity
import com.example.data.VoiceRepository
import com.example.ui.theme.tr
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Message data model for Voice Design chat conversation
data class VoiceDesignChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val voiceName: String? = null,
    val voiceTags: String? = null,
    val audioUrl: String? = null,
    val referenceText: String? = null,
    val isLiked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class VoiceDesignAudioItem(
    val id: String = "",
    val voiceName: String = "",
    val referenceText: String = "",
    val audioUrl: String = "",
    val date: Long = System.currentTimeMillis()
)

class VoiceDesignHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("voice_design_audio_prefs", Context.MODE_PRIVATE)
    private val firestoreRepository = FirestoreRepository()

    private fun getPrefKey(userId: String?): String {
        return if (!userId.isNullOrEmpty()) "vd_audios_$userId" else "vd_audios_guest"
    }

    fun getAudios(userId: String? = null): List<VoiceDesignAudioItem> {
        val key = getPrefKey(userId)
        val json = prefs.getString(key, null) ?: return emptyList()
        val list = mutableListOf<VoiceDesignAudioItem>()
        return try {
            val array = org.json.JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VoiceDesignAudioItem(
                        id = obj.optString("id", ""),
                        voiceName = obj.optString("voiceName", ""),
                        referenceText = obj.optString("referenceText", ""),
                        audioUrl = obj.optString("audioUrl", ""),
                        date = obj.optLong("date", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveAudio(userId: String?, item: VoiceDesignAudioItem) {
        val current = getAudios(userId).toMutableList()
        current.removeAll { it.id == item.id || (it.audioUrl == item.audioUrl && item.audioUrl.isNotEmpty()) }
        current.add(0, item)
        saveList(userId, current)

        if (!userId.isNullOrEmpty()) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    firestoreRepository.saveVoiceDesignAudio(userId, item)
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun deleteAudio(userId: String?, audioUrl: String) {
        val current = getAudios(userId).toMutableList()
        current.removeAll { it.audioUrl == audioUrl }
        saveList(userId, current)

        if (!userId.isNullOrEmpty()) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    firestoreRepository.deleteVoiceDesignAudio(userId, audioUrl)
                } catch (_: Exception) {}
            }
        }
    }

    suspend fun fetchAudios(userId: String?): List<VoiceDesignAudioItem> {
        val local = getAudios(userId)
        if (userId.isNullOrEmpty()) return local
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val remote = firestoreRepository.getVoiceDesignAudios(userId)
                val map = LinkedHashMap<String, VoiceDesignAudioItem>()
                for (item in remote) {
                    val key = if (item.id.isNotEmpty()) item.id else item.audioUrl
                    if (key.isNotEmpty()) map[key] = item
                }
                for (item in local) {
                    val key = if (item.id.isNotEmpty()) item.id else item.audioUrl
                    if (key.isNotEmpty() && !map.containsKey(key)) {
                        map[key] = item
                    }
                }
                val sorted = map.values.sortedByDescending { it.date }
                saveList(userId, sorted)
                sorted
            } catch (e: Exception) {
                local
            }
        }
    }

    private fun saveList(userId: String?, list: List<VoiceDesignAudioItem>) {
        try {
            val key = getPrefKey(userId)
            val array = org.json.JSONArray()
            for (item in list) {
                val obj = org.json.JSONObject().apply {
                    put("id", item.id)
                    put("voiceName", item.voiceName)
                    put("referenceText", item.referenceText)
                    put("audioUrl", item.audioUrl)
                    put("date", item.date)
                }
                array.put(obj)
            }
            prefs.edit().putString(key, array.toString()).apply()
        } catch (e: Exception) {
        }
    }
}

private fun isVoiceDesignPlan(plan: String?): Boolean {
    val p = plan?.lowercase() ?: ""
    return p == "starter" || p == "creator" || p == "pro"
}

private fun getMaxVoiceDesignQuota(plan: String?): Int {
    return when (plan?.lowercase()) {
        "starter" -> 10
        "creator" -> 20
        "pro" -> 30
        else -> 0
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VoiceDesignScreen(
    authManager: AuthManager,
    onBack: (() -> Unit)? = null,
    onNavigateToTts: (() -> Unit)? = null,
    onNavigateToSubscription: (() -> Unit)? = null,
    onNavigateToAccount: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val firestoreRepo = remember { FirestoreRepository() }
    val voiceRepo = remember { VoiceRepository(context) }
    val vdHistoryManager = remember { VoiceDesignHistoryManager(context) }
    
    val user by authManager.currentUser.collectAsState()
    var userProfile by remember { mutableStateOf<com.example.data.UserProfile?>(null) }
    var currentQuota by remember { mutableIntStateOf(0) }
    val today = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) }
    
    // Tab state: 0 = Design, 1 = Library
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Voices list from Room/Firestore
    val allVoicesList by voiceRepo.allVoices.collectAsState(initial = emptyList())
    
    // Custom voices created by user (filtered by gender == "Custom")
    val customVoices = remember(allVoicesList) { allVoicesList.filter { it.gender == "Custom" } }
    
    // Custom voices state
    var selectedVoiceName by remember { mutableStateOf("Aarav (Default)") }
    var inputPrompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var showCreateVoiceDialog by remember { mutableStateOf(false) }
    var voiceToManage by remember { mutableStateOf<VoiceEntity?>(null) }
    var generatedAudios by remember { mutableStateOf<List<VoiceDesignAudioItem>>(emptyList()) }
    var showDisclaimerDialog by remember { mutableStateOf(false) }
    
    // Audio Player state
    var activePlayingUrl by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlaybackTime by remember { mutableStateOf("0:00") }
    var totalPlaybackTime by remember { mutableStateOf("0:06") }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Chat messages stream
    val chatMessages = remember {
        mutableStateListOf(
            VoiceDesignChatMessage(
                isUser = false,
                text = "Welcome to Voice Design! Describe the voice you want to create. Be as detailed as possible about gender, age, tone, language, accent, and emotion."
            ),
            VoiceDesignChatMessage(
                isUser = false,
                text = "Daily voice generation quota 10/10 credits remaining."
            )
        )
    }

    // Load user profile & generated audios & sync daily voice design quota
    LaunchedEffect(user) {
        if (user != null) {
            val profile = firestoreRepo.getUserProfile(user!!.uid, user!!.email ?: "")
            userProfile = profile
            
            val plan = profile.subscriptionPlan
            val isSubscribed = isVoiceDesignPlan(plan)
            val maxQuota = getMaxVoiceDesignQuota(plan)
            
            if (isSubscribed) {
                if (profile.lastVoiceDesignDate == today) {
                    currentQuota = profile.voicedesignQuota
                } else {
                    currentQuota = maxQuota
                    firestoreRepo.updateVoiceDesignQuota(user!!.uid, maxQuota, today)
                    userProfile = profile.copy(voicedesignQuota = maxQuota, lastVoiceDesignDate = today)
                }
                val formattedQuota = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(currentQuota)
                val formattedMax = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(maxQuota)
                if (chatMessages.isNotEmpty() && !chatMessages[0].isUser) {
                    chatMessages[0] = chatMessages[0].copy(
                        text = "Welcome to Voice Design! Describe the voice you want to create. Be as detailed as possible about gender, age, tone, language, accent, and emotion."
                    )
                }
                val quotaText = "Daily voice generation quota $formattedQuota/$formattedMax credits remaining."
                if (chatMessages.size >= 2 && !chatMessages[1].isUser) {
                    chatMessages[1] = chatMessages[1].copy(text = quotaText)
                } else if (chatMessages.size == 1) {
                    chatMessages.add(VoiceDesignChatMessage(isUser = false, text = quotaText))
                }
            } else {
                currentQuota = 0
                if (chatMessages.isNotEmpty() && !chatMessages[0].isUser) {
                    chatMessages[0] = chatMessages[0].copy(
                        text = "Welcome to Voice Design! Describe the voice you want to create. Be as detailed as possible about gender, age, tone, language, accent, and emotion."
                    )
                }
                val subText = "Daily voice generation quota 0/0 credits remaining. Upgrade your plan to unlock Voice Design."
                if (chatMessages.size >= 2 && !chatMessages[1].isUser) {
                    chatMessages[1] = chatMessages[1].copy(text = subText)
                } else if (chatMessages.size == 1) {
                    chatMessages.add(VoiceDesignChatMessage(isUser = false, text = subText))
                }
            }
        } else {
            currentQuota = 0
        }
        scope.launch {
            voiceRepo.syncVoicesIfNeeded(user?.uid)
            if (user != null) {
                val fetched = vdHistoryManager.fetchAudios(user?.uid)
                if (fetched.isEmpty()) {
                    val guestAudios = vdHistoryManager.getAudios(null)
                    if (guestAudios.isNotEmpty()) {
                        guestAudios.forEach { vdHistoryManager.saveAudio(user?.uid, it) }
                        generatedAudios = vdHistoryManager.fetchAudios(user?.uid)
                    } else {
                        generatedAudios = fetched
                    }
                } else {
                    generatedAudios = fetched
                }
            } else {
                generatedAudios = vdHistoryManager.getAudios(null)
            }
        }
    }

    // Refresh Voice Design library audios whenever user switches tab or user changes
    LaunchedEffect(selectedTab, user) {
        if (selectedTab == 1) {
            scope.launch {
                if (user != null) {
                    generatedAudios = vdHistoryManager.fetchAudios(user?.uid)
                } else {
                    generatedAudios = vdHistoryManager.getAudios(null)
                }
            }
        }
    }

    // Cleanup mediaPlayer on dispose
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Helper to control playback
    fun togglePlayPause(url: String) {
        if (url.isBlank()) return
        if (activePlayingUrl == url && isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        mp.start()
                        isPlaying = true
                        activePlayingUrl = url
                        val durationSec = mp.duration / 1000
                        totalPlaybackTime = String.format("%d:%02d", durationSec / 60, durationSec % 60)
                    }
                    setOnCompletionListener {
                        isPlaying = false
                        activePlayingUrl = null
                    }
                    setOnErrorListener { _, _, _ ->
                        isPlaying = false
                        activePlayingUrl = null
                        true
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to play audio: ${e.message}", Toast.LENGTH_SHORT).show()
                isPlaying = false
                activePlayingUrl = null
            }
        }
    }

    // Main layout container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Top Bar
            VoiceDesignTopBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onBack = onBack,
                userAvatarUrl = userProfile?.avatarUrl,
                onNavigateToAccount = onNavigateToAccount
            )

            // Content view based on tab
            if (selectedTab == 0) {
                // DESIGN VIEW (Conversational UI)
                val listState = androidx.compose.foundation.lazy.rememberLazyListState()
                LaunchedEffect(chatMessages.size) {
                    if (chatMessages.isNotEmpty()) {
                        listState.animateScrollToItem(chatMessages.size - 1)
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { msg ->
                            VoiceDesignChatMessageItem(
                                message = msg,
                                userAvatarUrl = userProfile?.avatarUrl,
                                isPlaying = isPlaying && activePlayingUrl == msg.audioUrl,
                                currentPlaybackTime = if (activePlayingUrl == msg.audioUrl) currentPlaybackTime else "0:00",
                                totalPlaybackTime = if (activePlayingUrl == msg.audioUrl) totalPlaybackTime else "0:06",
                                onPlayPauseToggle = { url -> togglePlayPause(url) },
                                onUseVoice = { vName ->
                                    selectedVoiceName = vName
                                    Toast.makeText(context, "Selected '$vName' for TTS!", Toast.LENGTH_SHORT).show()
                                    onNavigateToTts?.invoke()
                                },
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(msg.text))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onLikeToggle = {
                                    val idx = chatMessages.indexOfFirst { it.id == msg.id }
                                    if (idx != -1) {
                                        chatMessages[idx] = msg.copy(isLiked = !msg.isLiked)
                                    }
                                },
                                onInfoClick = { showDisclaimerDialog = true },
                                onOkay = {
                                    Toast.makeText(context, "Okay", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        if (!isVoiceDesignPlan(userProfile?.subscriptionPlan)) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Lock,
                                                contentDescription = "Subscription Required",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Text(
                                                text = "Unlock Voice Design",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "Voice Design is available exclusively for Starter, Creator, and Pro subscription plans. Upgrade to create custom AI voices with daily voice generation quotas.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("• Starter Plan: 10 Voice Designs / day", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF97316), fontWeight = FontWeight.SemiBold)
                                            Text("• Creator Plan: 20 Voice Designs / day", style = MaterialTheme.typography.labelSmall, color = Color(0xFF3B82F6), fontWeight = FontWeight.SemiBold)
                                            Text("• Pro Plan: 30 Voice Designs / day", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                                        }

                                        Button(
                                            onClick = { onNavigateToSubscription?.invoke() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Upgrade Subscription Plan", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        if (isGenerating) {
                            item {
                                AssistantGeneratingBubble()
                            }
                        }
                    }
                }

                // Bottom Section: (+ New Voice) Button + Saved Voice Cards + Input Bar
                val isImeVisible = WindowInsets.isImeVisible
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .then(if (!isImeVisible) Modifier.navigationBarsPadding() else Modifier)
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    // LazyRow above text area: + New Voice button + Saved Voice Cards
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            NewVoiceChipButton(
                                onClick = { showCreateVoiceDialog = true }
                            )
                        }

                        items(customVoices, key = { it.voiceName }) { voice ->
                            VoiceDesignCard(
                                name = voice.voiceName,
                                isSelected = selectedVoiceName == voice.voiceName,
                                onClick = {
                                    selectedVoiceName = voice.voiceName
                                },
                                onEditClick = {
                                    voiceToManage = voice
                                }
                            )
                        }
                    }

                    // Bottom Input Box (Mic + Text + Send)
                    VoiceDesignInputBar(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        isGenerating = isGenerating,
                        onSend = {
                            if (inputPrompt.isNotBlank() && !isGenerating) {
                                val plan = userProfile?.subscriptionPlan ?: "free"
                                val isSubscribed = isVoiceDesignPlan(plan)
                                val maxQuota = getMaxVoiceDesignQuota(plan)

                                if (!isSubscribed) {
                                    Toast.makeText(context, "Voice Design is available exclusively on Starter, Creator, or Pro plans.", Toast.LENGTH_LONG).show()
                                    onNavigateToSubscription?.invoke()
                                    return@VoiceDesignInputBar
                                }

                                if (currentQuota <= 0) {
                                    Toast.makeText(context, "Daily Voice Design quota reached ($maxQuota/$maxQuota used today). Please try again tomorrow or upgrade your plan.", Toast.LENGTH_LONG).show()
                                    return@VoiceDesignInputBar
                                }

                                val userText = inputPrompt
                                inputPrompt = ""
                                chatMessages.add(VoiceDesignChatMessage(isUser = true, text = userText))
                                isGenerating = true

                                scope.launch {
                                    val selectedVoice = customVoices.find { it.voiceName == selectedVoiceName }
                                        ?: allVoicesList.find { it.voiceName == selectedVoiceName }

                                    val instructToSend = when {
                                        selectedVoice != null && selectedVoice.description.isNotBlank() -> selectedVoice.description
                                        selectedVoice != null && selectedVoice.referenceText.isNotBlank() -> selectedVoice.referenceText
                                        else -> userText
                                    }

                                    val textToSend = userText

                                    val result = TtsApiManager.generateVoiceDesign(text = textToSend, instruct = instructToSend)
                                    isGenerating = false
                                    if (result.isSuccess) {
                                        val audioUrl = result.getOrNull() ?: ""
                                        val vName = if (selectedVoice != null) selectedVoice.voiceName else "Designed Voice ${customVoices.size + 1}"

                                        if (selectedVoice == null) {
                                            val newVoice = VoiceEntity(
                                                voiceName = vName,
                                                description = instructToSend,
                                                referenceText = instructToSend,
                                                audioUrl = audioUrl,
                                                language = "English",
                                                gender = "Custom",
                                                isPro = false,
                                                lastUpdated = System.currentTimeMillis()
                                            )
                                            voiceRepo.saveCustomVoice(user?.uid, newVoice)
                                            selectedVoiceName = vName
                                        } else {
                                            val updatedVoice = selectedVoice.copy(
                                                audioUrl = audioUrl,
                                                description = instructToSend,
                                                referenceText = instructToSend,
                                                lastUpdated = System.currentTimeMillis()
                                            )
                                            voiceRepo.updateCustomVoice(user?.uid, selectedVoice.voiceName, updatedVoice)
                                        }

                                        // Save generated audio item to Voice Design Library
                                        val generatedItem = VoiceDesignAudioItem(
                                            id = System.currentTimeMillis().toString(),
                                            voiceName = vName,
                                            referenceText = instructToSend,
                                            audioUrl = audioUrl,
                                            date = System.currentTimeMillis()
                                        )
                                        vdHistoryManager.saveAudio(user?.uid, generatedItem)
                                        generatedAudios = vdHistoryManager.getAudios(user?.uid)

                                         chatMessages.add(
                                            VoiceDesignChatMessage(
                                                isUser = false,
                                                text = "Here is your generated audio preview for \"$vName\":",
                                                voiceName = vName,
                                                voiceTags = "Custom • HD Voice",
                                                audioUrl = audioUrl,
                                                referenceText = instructToSend
                                            )
                                        )

                                        // Deduct 1 from daily voicedesignQuota and deduct character length from credits, saving to Firestore
                                        val totalCharsToDeduct = textToSend.length + instructToSend.length
                                        val newQuota = maxOf(0, currentQuota - 1)
                                        currentQuota = newQuota
                                        if (user != null) {
                                            firestoreRepo.updateVoiceDesignQuota(user!!.uid, newQuota, today)
                                            val currentCredits = userProfile?.credits ?: 3000
                                            val updatedCredits = maxOf(0, currentCredits - totalCharsToDeduct)
                                            val currentSaved = userProfile?.savedVoices?.toMutableList() ?: mutableListOf()
                                            if (!currentSaved.contains(vName)) {
                                                currentSaved.add(0, vName)
                                            }
                                            val updatedProfile = userProfile?.copy(
                                                credits = updatedCredits,
                                                voicedesignQuota = newQuota,
                                                lastVoiceDesignDate = today,
                                                savedVoices = currentSaved
                                            ) ?: UserProfile(
                                                email = user!!.email ?: "",
                                                credits = updatedCredits,
                                                voicedesignQuota = newQuota,
                                                lastVoiceDesignDate = today,
                                                savedVoices = currentSaved
                                            )
                                            firestoreRepo.saveUserProfile(user!!.uid, updatedProfile)
                                            userProfile = updatedProfile
                                        }

                                        val formattedQuota = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(newQuota)
                                        val formattedMax = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(maxQuota)
                                        val quotaText = "Daily voice generation quota $formattedQuota/$formattedMax credits remaining."
                                        if (chatMessages.size >= 2 && !chatMessages[1].isUser) {
                                            chatMessages[1] = chatMessages[1].copy(text = quotaText)
                                        }
                                    } else {
                                        val err = result.exceptionOrNull()?.message ?: "Failed to generate voice design"
                                        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        chatMessages.add(
                                            VoiceDesignChatMessage(
                                                isUser = false,
                                                text = "Sorry, I couldn't generate the voice design: $err"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )

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
                            color = Color(0xFFA09DAE)
                        )
                    }
                }
            } else {
                // LIBRARY VIEW
                VoiceDesignLibraryView(
                    audios = generatedAudios,
                    activePlayingUrl = activePlayingUrl,
                    isPlaying = isPlaying,
                    onPlayToggle = { togglePlayPause(it) },
                    onDeleteAudio = { audioUrl ->
                        scope.launch {
                            vdHistoryManager.deleteAudio(user?.uid, audioUrl)
                            generatedAudios = vdHistoryManager.getAudios(user?.uid)
                        }
                        Toast.makeText(context, "Deleted audio from Library", Toast.LENGTH_SHORT).show()
                    },
                    onUseVoice = { audioItem ->
                        selectedVoiceName = audioItem.voiceName
                        scope.launch {
                            val existing = customVoices.find { it.voiceName == audioItem.voiceName }
                                ?: allVoicesList.find { it.voiceName == audioItem.voiceName }
                            if (existing != null) {
                                val updated = existing.copy(
                                    audioUrl = audioItem.audioUrl,
                                    referenceText = audioItem.referenceText.ifEmpty { existing.referenceText },
                                    description = audioItem.referenceText.ifEmpty { existing.description },
                                    lastUpdated = System.currentTimeMillis()
                                )
                                voiceRepo.updateCustomVoice(user?.uid, existing.voiceName, updated)
                            } else {
                                val newVoice = VoiceEntity(
                                    voiceName = audioItem.voiceName,
                                    description = audioItem.referenceText,
                                    referenceText = audioItem.referenceText,
                                    audioUrl = audioItem.audioUrl,
                                    language = "English",
                                    gender = "Custom",
                                    isPro = false,
                                    lastUpdated = System.currentTimeMillis()
                                )
                                voiceRepo.saveCustomVoice(user?.uid, newVoice)
                            }

                            if (user != null && userProfile != null) {
                                val currentSaved = userProfile!!.savedVoices.toMutableList()
                                if (!currentSaved.contains(audioItem.voiceName)) {
                                    currentSaved.add(0, audioItem.voiceName)
                                    val updatedProfile = userProfile!!.copy(savedVoices = currentSaved)
                                    firestoreRepo.saveUserProfile(user!!.uid, updatedProfile)
                                    userProfile = updatedProfile
                                }
                            }
                        }
                        Toast.makeText(context, "Selected '${audioItem.voiceName}' for TTS", Toast.LENGTH_SHORT).show()
                        onNavigateToTts?.invoke()
                    }
                )
            }
        }
    }

    // Pop-up dialog when "+ New Voice" is clicked
    if (showCreateVoiceDialog) {
        CreateVoiceModalDialog(
            onDismiss = { showCreateVoiceDialog = false },
            onSave = { name, prompt ->
                showCreateVoiceDialog = false
                scope.launch {
                    val newVoice = VoiceEntity(
                        voiceName = name,
                        description = prompt,
                        referenceText = prompt,
                        audioUrl = "",
                        language = "English",
                        gender = "Custom",
                        isPro = false,
                        lastUpdated = System.currentTimeMillis()
                    )
                    voiceRepo.saveCustomVoice(user?.uid, newVoice)
                    selectedVoiceName = name
                    Toast.makeText(context, "Voice '$name' saved!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Pop-up dialog when a saved voice card is clicked (to Edit or Delete)
    if (voiceToManage != null) {
        EditVoiceModalDialog(
            voice = voiceToManage!!,
            onDismiss = { voiceToManage = null },
            onSave = { newName, newPrompt ->
                val oldVoice = voiceToManage!!
                voiceToManage = null
                scope.launch {
                    val updated = oldVoice.copy(
                        voiceName = newName,
                        description = newPrompt,
                        referenceText = newPrompt,
                        lastUpdated = System.currentTimeMillis()
                    )
                    voiceRepo.updateCustomVoice(user?.uid, oldVoice.voiceName, updated)
                    selectedVoiceName = newName
                    Toast.makeText(context, "Voice '$newName' updated!", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = {
                val oldVoice = voiceToManage!!
                voiceToManage = null
                scope.launch {
                    voiceRepo.deleteCustomVoice(user?.uid, oldVoice.voiceName)
                    if (user != null && userProfile != null) {
                        val currentSaved = userProfile!!.savedVoices.toMutableList()
                        if (currentSaved.contains(oldVoice.voiceName)) {
                            currentSaved.remove(oldVoice.voiceName)
                            val updatedProfile = userProfile!!.copy(savedVoices = currentSaved)
                            firestoreRepo.saveUserProfile(user!!.uid, updatedProfile)
                            userProfile = updatedProfile
                        }
                    }
                    if (!oldVoice.audioUrl.isNullOrBlank()) {
                        vdHistoryManager.deleteAudio(user?.uid, oldVoice.audioUrl)
                        vdHistoryManager.deleteAudio(null, oldVoice.audioUrl)
                        generatedAudios = vdHistoryManager.getAudios(user?.uid)
                    }
                    if (selectedVoiceName == oldVoice.voiceName) {
                        selectedVoiceName = "Aarav (Default)"
                    }
                    Toast.makeText(context, "Voice '${oldVoice.voiceName}' deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Disclaimer Info Modal Dialog
    if (showDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showDisclaimerDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• AI-generated speech is produced automatically using neural voice models.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Pronunciations, pitch, or emotional tone may occasionally contain minor inaccuracies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Please review and verify all synthesized audio before publishing or using in production.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Remaining quota is deducted based on text character length.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

// Top Bar with Back Arrow, Title, and Segmented Design/Library Toggle
@Composable
fun VoiceDesignTopBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onBack: (() -> Unit)?,
    userAvatarUrl: String? = null,
    onNavigateToAccount: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = "Voice Design".tr(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Segmented Control Pill: [ ✨ Design ] [ 📁 Library ]
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Design Segment
            val isDesign = selectedTab == 0
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDesign) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTabSelected(0) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = if (isDesign) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Design".tr(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isDesign) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = if (isDesign) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Library Segment
            val isLibrary = selectedTab == 1
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isLibrary) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onTabSelected(1) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Folder,
                    contentDescription = null,
                    tint = if (isLibrary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Library".tr(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isLibrary) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    ),
                    color = if (isLibrary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Chat Message Item
@Composable
fun VoiceDesignChatMessageItem(
    message: VoiceDesignChatMessage,
    userAvatarUrl: String? = null,
    isPlaying: Boolean,
    currentPlaybackTime: String,
    totalPlaybackTime: String,
    onPlayPauseToggle: (String) -> Unit,
    onUseVoice: (String) -> Unit,
    onCopy: () -> Unit,
    onLikeToggle: () -> Unit,
    onInfoClick: (() -> Unit)? = null,
    onOkay: (() -> Unit)? = null
) {
    if (message.isUser) {
        // User Message (Right Aligned)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val avatar = userAvatarUrl ?: ""
                if (avatar.isNotEmpty()) {
                    coil.compose.AsyncImage(
                        model = avatar,
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
        // AI Assistant Message (Left Aligned)
        val isWelcomeMsg = message.text.startsWith("Welcome to Voice Design!")
        val isQuotaMsg = message.text.contains("Daily voice generation quota") || message.text.contains("Daily Voice Generation")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // AI Waveform Avatar Icon (don't show audio logo for second pop / quota msg, but keep left padding like first popup)
            if (!isQuotaMsg) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "AI Assistant",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                // Card Bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Embedded Audio Preview Card if available
                        if (!message.audioUrl.isNullOrBlank()) {
                            EmbeddedAudioPreviewCard(
                                voiceName = message.voiceName ?: "Designed Voice",
                                voiceTags = message.voiceTags ?: "Custom Voice",
                                audioUrl = message.audioUrl,
                                isPlaying = isPlaying,
                                currentPlaybackTime = currentPlaybackTime,
                                totalPlaybackTime = totalPlaybackTime,
                                onPlayPauseToggle = { onPlayPauseToggle(message.audioUrl) },
                                onUseVoice = { onUseVoice(message.voiceName ?: "Designed Voice") }
                            )
                        }
                    }
                }

                // Action Bar Icons below message (hide on first/second popup messages)
                if (!isWelcomeMsg && !isQuotaMsg) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(onClick = onLikeToggle, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.ThumbUp,
                                contentDescription = "Like",
                                tint = if (message.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Alphabet Avatar Composable
@Composable
fun VoiceAlphabetAvatar(
    name: String,
    size: Dp = 32.dp,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val initial = name.trim().firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString()
        ?: name.trim().firstOrNull()?.uppercaseChar()?.toString()
        ?: "V"

    val bgModifier = if (isSelected) {
        Modifier.background(MaterialTheme.colorScheme.primary)
    } else {
        Modifier.background(
            brush = Brush.radialGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        )
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(bgModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.48f).sp,
                color = textColor
            )
        )
    }
}

// Embedded Audio Preview Card inside AI Chat Bubble
@Composable
fun EmbeddedAudioPreviewCard(
    voiceName: String,
    voiceTags: String,
    audioUrl: String,
    isPlaying: Boolean,
    currentPlaybackTime: String,
    totalPlaybackTime: String,
    onPlayPauseToggle: () -> Unit,
    onUseVoice: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Player Top Row (Play/Pause, Waveform Canvas, Time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play/Pause Circular Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .clickable { onPlayPauseToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Waveform Graphic Visualizer
                WaveformCanvas(
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                )

                // Playback Time
                Text(
                    text = "$currentPlaybackTime / $totalPlaybackTime",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)

            // Voice Details Bottom Row (Avatar, Name/Tags, [ Use Voice ])
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    VoiceAlphabetAvatar(
                        name = voiceName,
                        size = 32.dp,
                        isSelected = true
                    )
                    Column {
                        Text(
                            text = voiceName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = voiceTags,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Use Voice Pill Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .clickable { onUseVoice() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Use Voice".tr(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Custom Waveform Canvas
@Composable
fun WaveformCanvas(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier = modifier) {
        val barCount = 28
        val width = size.width
        val height = size.height
        val spacing = width / (barCount * 1.5f)
        val barWidth = spacing * 0.8f

        val heights = listOf(
            0.3f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f, 1.0f, 0.5f,
            0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f, 0.9f, 0.5f, 0.8f, 0.4f,
            0.6f, 0.3f, 0.7f, 0.5f, 0.4f, 0.3f, 0.2f, 0.4f
        )

        for (i in 0 until barCount) {
            val h = (heights[i % heights.size] * height).coerceAtLeast(4f)
            val x = i * (barWidth + spacing / 2)
            val y = (height - h) / 2

            val color = if (isPlaying && i < barCount / 2) {
                activeColor
            } else {
                inactiveColor
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, h),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

// Assistant Loading Indicator
@Composable
fun AssistantGeneratingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }
        Text(
            text = "Designing custom voice... Please wait...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Voice Chip Item
@Composable
fun VoiceChipItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        VoiceAlphabetAvatar(
            name = name,
            size = 22.dp,
            isSelected = isSelected
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// "+ New Voice" Chip Button
@Composable
fun NewVoiceChipButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "New Voice",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "New Voice".tr(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Bottom Input Bar with Mic, Input Field, and Send
@Composable
fun VoiceDesignInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    isGenerating: Boolean,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mic Icon Button
        IconButton(
            onClick = { /* Mic action */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Mic",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text Input Field
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue.take(100))
            },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSend = { onSend() }),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Describe the voice you want to design...".tr(),
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send Button
        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank() && !isGenerating,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (value.isNotBlank() && !isGenerating)
                        Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                    else
                        SolidColor(MaterialTheme.colorScheme.surface)
                )
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Card component for saved voices displayed above text input area
@Composable
fun VoiceDesignCard(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VoiceAlphabetAvatar(
            name = name,
            size = 28.dp,
            isSelected = isSelected
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit voice",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

// Pop-Up Dialog when a saved card is clicked (to Edit or Delete)
@Composable
fun EditVoiceModalDialog(
    voice: VoiceEntity,
    onDismiss: () -> Unit,
    onSave: (newName: String, newPrompt: String) -> Unit,
    onDelete: () -> Unit
) {
    var voiceName by remember { mutableStateOf(voice.voiceName) }
    var voicePrompt by remember { mutableStateOf(voice.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Edit Voice".tr(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name Field
                OutlinedTextField(
                    value = voiceName,
                    onValueChange = { voiceName = it },
                    label = { Text("Voice Name", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Prompt Field
                OutlinedTextField(
                    value = voicePrompt,
                    onValueChange = { voicePrompt = it.take(100) },
                    label = { Text("Voice Prompt / Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete Button
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete".tr())
                }

                // Save Button
                Button(
                    onClick = {
                        if (voiceName.isNotBlank()) {
                            onSave(voiceName, voicePrompt)
                        }
                    },
                    enabled = voiceName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Save".tr(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel".tr(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// Pop-Up Dialog when "+ New Voice" is clicked
@Composable
fun CreateVoiceModalDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, prompt: String) -> Unit
) {
    var voiceName by remember { mutableStateOf("") }
    var voicePrompt by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Create New Voice".tr(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Name Field
                OutlinedTextField(
                    value = voiceName,
                    onValueChange = { voiceName = it },
                    label = { Text("Voice Name", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("e.g. Calm Deep Narrator", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Prompt Field
                OutlinedTextField(
                    value = voicePrompt,
                    onValueChange = { voicePrompt = it.take(100) },
                    label = { Text("Voice Prompt / Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("e.g. Deep, calm male voice, 30s, Hindi accent...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (voiceName.isNotBlank() && voicePrompt.isNotBlank()) {
                        onSave(voiceName, voicePrompt)
                    }
                },
                enabled = voiceName.isNotBlank() && voicePrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Save".tr(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel".tr(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// Voice Design Library Tab View
@Composable
fun VoiceDesignLibraryView(
    audios: List<VoiceDesignAudioItem>,
    activePlayingUrl: String?,
    isPlaying: Boolean,
    onPlayToggle: (String) -> Unit,
    onDeleteAudio: (String) -> Unit,
    onUseVoice: (VoiceDesignAudioItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredAudios = remember(audios, searchQuery) {
        audios.filter {
            searchQuery.isBlank() ||
                    it.voiceName.contains(searchQuery, ignoreCase = true) ||
                    it.referenceText.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search generated audios...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (filteredAudios.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "No generated audios in library",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Generate speech in Voice Design tab to save audios here!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredAudios, key = { it.id.ifEmpty { it.audioUrl } }) { audioItem ->
                    val isCurrentPlaying = isPlaying && activePlayingUrl == audioItem.audioUrl

                    VoiceLibraryItemCard(
                        audioItem = audioItem,
                        isPlaying = isCurrentPlaying,
                        onPlayToggle = { if (audioItem.audioUrl.isNotBlank()) onPlayToggle(audioItem.audioUrl) },
                        onDelete = { onDeleteAudio(audioItem.audioUrl) },
                        onUseVoice = { onUseVoice(audioItem) }
                    )
                }
            }
        }
    }
}

// Voice Library Card Item
@Composable
fun VoiceLibraryItemCard(
    audioItem: VoiceDesignAudioItem,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onDelete: () -> Unit,
    onUseVoice: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    VoiceAlphabetAvatar(
                        name = audioItem.voiceName,
                        size = 40.dp,
                        isSelected = false
                    )
                    Column {
                        Text(
                            text = audioItem.voiceName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (audioItem.referenceText.isNotBlank()) audioItem.referenceText else "Generated Voice Audio",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (audioItem.audioUrl.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onPlayToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    WaveformCanvas(
                        isPlaying = isPlaying,
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    )

                    Button(
                        onClick = onUseVoice,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Use Voice".tr(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}
