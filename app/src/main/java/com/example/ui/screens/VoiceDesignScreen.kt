package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.example.auth.AuthManager
import com.example.api.TtsApiManager
import com.example.data.FirestoreRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceDesignScreen(authManager: AuthManager) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("voice_design_prefs", Context.MODE_PRIVATE) }
    
    var voicePrompt by remember { mutableStateOf("") }
    var referenceText by remember { mutableStateOf("") }
    
    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { FirestoreRepository() }
    var userPlan by remember { mutableStateOf("free") }
    var userCredits by remember { mutableIntStateOf(0) }
    var isLoadingPlan by remember { mutableStateOf(true) }
    
    var designsGeneratedToday by remember { mutableIntStateOf(0) }
    
    val maxDesignsPerDay = when (userPlan.lowercase(Locale.getDefault())) {
        "starter" -> 10
        "creator" -> 20
        "pro" -> 30
        else -> 0
    }
    
    LaunchedEffect(user) {
        if (user != null) {
            val profile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
            userPlan = profile.subscriptionPlan.ifEmpty { "free" }.lowercase(Locale.getDefault())
            userCredits = profile.credits
            isLoadingPlan = false
        } else {
            userPlan = "free"
            userCredits = 0
            isLoadingPlan = false
        }
    }
    
    LaunchedEffect(Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = sharedPrefs.getString("last_design_date", "")
        
        if (today != lastDate) {
            sharedPrefs.edit().putString("last_design_date", today).putInt("designs_generated_today", 0).apply()
            designsGeneratedToday = 0
        } else {
            designsGeneratedToday = sharedPrefs.getInt("designs_generated_today", 0)
        }
    }
    
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var generatedAudioUrl by remember { mutableStateOf<String?>(null) }
    var generateError by remember { mutableStateOf<String?>(null) }
    var showPreviewPlayer by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Voice Design",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Quota Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.DesignServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Daily Design Quota",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!isLoadingPlan) {
                                    val planName = userPlan.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    val badgeBgColor = when (userPlan) {
                                        "starter" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                                        "creator" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
                                        "pro" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                    }
                                    val badgeTextColor = when (userPlan) {
                                        "starter" -> MaterialTheme.colorScheme.onSecondaryContainer
                                        "creator" -> MaterialTheme.colorScheme.onTertiaryContainer
                                        "pro" -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(badgeBgColor)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = planName.uppercase(Locale.getDefault()),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = badgeTextColor,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                        if (isLoadingPlan) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "$designsGeneratedToday / $maxDesignsPerDay",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "designs remaining",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    // Premium custom gradient animated bar
                    val progress = if (maxDesignsPerDay > 0) designsGeneratedToday.toFloat() / maxDesignsPerDay else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            // Input Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Voice Prompt",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = voicePrompt,
                    onValueChange = { if (it.length <= 500) voicePrompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
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
                            "e.g. A deep, raspy old man's voice...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${voicePrompt.length} / 500",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Reference Text",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextField(
                    value = referenceText,
                    onValueChange = { if (it.length <= 1000) referenceText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
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
                            "Text for the voice to read...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${referenceText.length} / 1000",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Generate Button
            Button(
                onClick = {
                    if (designsGeneratedToday < maxDesignsPerDay) {
                        if (userCredits < referenceText.length) {
                            val errMsg = "Insufficient credits. Required: ${referenceText.length}, Available: $userCredits"
                            generateError = errMsg
                            android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isGenerating = true
                        generateError = null
                        generatedAudioUrl = null
                        scope.launch {
                            val result = TtsApiManager.generateVoiceDesign(referenceText, voicePrompt)
                            isGenerating = false
                            if (result.isSuccess) {
                                designsGeneratedToday++
                                sharedPrefs.edit().putInt("designs_generated_today", designsGeneratedToday).apply()
                                userCredits = (userCredits - referenceText.length).coerceAtLeast(0)
                                val audioUrl = result.getOrNull()
                                generatedAudioUrl = audioUrl
                                if (!audioUrl.isNullOrEmpty()) {
                                    val historyManager = com.example.data.HistoryManager(context)
                                    val historyItem = com.example.data.GenerationHistory(
                                        id = java.util.UUID.randomUUID().toString(),
                                        text = referenceText,
                                        type = "Voice Design",
                                        date = System.currentTimeMillis(),
                                        voiceName = if (voicePrompt.isNotBlank()) voicePrompt else "Voice Design",
                                        duration = "",
                                        creditsUsed = referenceText.length,
                                        audioUrl = audioUrl,
                                        imageUrl = ""
                                    )
                                    scope.launch {
                                        historyManager.saveHistoryItem(user?.uid, historyItem)
                                    }
                                }
                                android.widget.Toast.makeText(context, "Voice design successful!", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                val errMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                                generateError = errMsg
                                android.widget.Toast.makeText(context, errMsg, android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isGenerating && maxDesignsPerDay > 0 && designsGeneratedToday < maxDesignsPerDay && voicePrompt.isNotBlank() && referenceText.isNotBlank(),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (maxDesignsPerDay == 0) {
                            "Upgrade to Design Voices"
                        } else if (designsGeneratedToday >= maxDesignsPerDay) {
                            "Daily Limit Reached"
                        } else {
                            "Generate Voice"
                        },
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPreviewPlayer && generatedAudioUrl != null) {
        val isFreeUser = userPlan == "free"
        AudioPreviewScreen(
            audioUrl = generatedAudioUrl ?: "",
            title = "Generated Voice",
            subtitle = if (voicePrompt.length > 30) "Voice prompt: \"${voicePrompt.take(27)}...\"" else "Voice prompt: \"$voicePrompt\"",
            imageUrl = null,
            lyricsText = referenceText,
            isFreeUser = isFreeUser,
            onBack = { showPreviewPlayer = false }
        )
    }
}
}
