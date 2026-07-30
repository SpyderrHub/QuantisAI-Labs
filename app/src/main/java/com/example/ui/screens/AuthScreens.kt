package com.example.ui.screens

import androidx.compose.animation.core.*

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.auth.AuthManager

@Composable
fun SplashScreen(onNavigateNext: (Boolean) -> Unit, authManager: AuthManager) {
    LaunchedEffect(Unit) {
        delay(2000)
        val isLoggedIn = authManager.currentUser.value != null
        onNavigateNext(isLoggedIn)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "QuantisAI Labs",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

data class OnboardingSlide(val title: String, val description: String, val imageRes: Int)

@Composable
fun CarouselDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pageCount) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (i == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (i == currentPage) Color.White else Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
fun IntroScreen(onNavigateToLogin: () -> Unit, onNavigateToSignup: () -> Unit = {}) {
    var step by remember { mutableIntStateOf(0) }
    
    val slides = listOf(
        OnboardingSlide("AI Voice Generator", "Transform text into lifelike speech instantly with advanced neural models.", com.example.R.drawable.neon_voice_gen_1785106399154),
        OnboardingSlide("AI Voice Design", "Customize and craft the perfect voice with granular pitch and speed controls.", com.example.R.drawable.neon_voice_design_1785106415938),
        OnboardingSlide("Global Reach", "Reach a global audience with support for hundreds of languages and dialects.", com.example.R.drawable.neon_global_1785106431907)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F13)) // Dark background
    ) {
        // Full screen image
        Image(
            painter = painterResource(id = slides[step].imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability and blending
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f), // Top shadow for progress bar
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xFF0F0F13).copy(alpha = 0.8f),
                            Color(0xFF0F0F13),
                            Color(0xFF0F0F13)
                        ),
                        startY = 0f
                    )
                )
        )

        // Progress bar at top
        CarouselDots(
            pageCount = slides.size,
            currentPage = step,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                .systemBarsPadding()
        )

        // Tappable areas for manual prev/next (placed first in z-order so they don't block buttons if they overlap, but they are fillMaxSize so they are at the back)
        // Wait, if they are at the back, buttons will steal focus, which is correct!
        Row(modifier = Modifier.fillMaxSize()) {
            val interactionSourcePrev = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            val interactionSourceNext = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSourcePrev,
                        indication = null
                    ) { 
                        if (step > 0) {
                            step--
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSourceNext,
                        indication = null
                    ) { 
                        if (step < slides.size - 1) {
                            step++
                        } else {
                            step = 0
                        }
                    }
            )
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = slides[step].title,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = slides[step].description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Sign up button
            Button(
                onClick = onNavigateToSignup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Sign up with Email",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Login button
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF232325),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "I have an account",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            
            // Terms text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "By continuing, you accept our ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Terms",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://www.quantisai.org/terms")
                        }
                    )
                    Text(
                        text = ", ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://www.quantisai.org/privacy")
                        }
                    )
                    Text(
                        text = ", and chat guidelines.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authManager: AuthManager, onNavigateToMain: () -> Unit, onNavigateToSignup: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Background Image
        Image(
            painter = painterResource(id = com.example.R.drawable.auth_header_bg_1785114826853),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Header row
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text("Email address", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("alexsmith@gmail.com", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email, imeAction = androidx.compose.ui.text.input.ImeAction.Next)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Password", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Your password", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation() as androidx.compose.ui.text.input.VisualTransformation,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.Gray
                            )
                        }
                    },
                    singleLine = true,
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Button(
                    onClick = {
                        isLoading = true
                        error = null
                        scope.launch {
                            val result = authManager.signInWithEmail(email, password)
                            if (result.isSuccess) {
                                onNavigateToMain()
                            } else {
                                error = result.exceptionOrNull()?.message ?: "Login failed"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE0E0E0)))
                    Text("or", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE0E0E0)))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onNavigateToSignup,
                    modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                ) {
                    Text("Create an account", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(authManager: AuthManager, onNavigateToMain: () -> Unit, onNavigateBack: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var isAwaitingOtp by remember { mutableStateOf(false) }
    var sentOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val firestoreRepository = remember { com.example.data.FirestoreRepository() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Background Image
        Image(
            painter = painterResource(id = com.example.R.drawable.auth_header_bg_1785114826853),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Header row
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(36.dp)
                            .background(Color(0xFFF5F5F5), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                    
                    Text(
                        text = "Signup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = if (isAwaitingOtp) "Enter the 6-digit code send to your at ${email.take(3)}***@gmail" else "Create your own account",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                if (!isAwaitingOtp) {
                    Text("Email address", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("alexsmith@gmail.com", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email, imeAction = androidx.compose.ui.text.input.ImeAction.Next)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Password", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("At least 8 characters", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation() as androidx.compose.ui.text.input.VisualTransformation,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        singleLine = true,
                    )
                } else {
                    androidx.compose.foundation.text.BasicTextField(
                        value = enteredOtp,
                        onValueChange = { if (it.length <= 6) enteredOtp = it },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number, imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        decorationBox = {
                            val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "cursor")
                            val cursorAlpha by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 0f,
                                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                    animation = androidx.compose.animation.core.tween(500, easing = androidx.compose.animation.core.LinearEasing),
                                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                ),
                                label = "cursorAlpha"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (i in 0 until 6) {
                                    val isFocused = enteredOtp.length == i
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                            .border(if (isFocused) 1.dp else 0.dp, if (isFocused) Color.Black else Color.Transparent, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (i < enteredOtp.length) enteredOtp[i].toString() else "",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black
                                        )
                                        if (isFocused) {
                                            Box(modifier = Modifier.width(2.dp).height(24.dp).background(Color.Black.copy(alpha = cursorAlpha)))
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Button(
                    onClick = {
                        if (!isAwaitingOtp) {
                            isLoading = true
                            error = null
                            scope.launch {
                                val otp = (100000..999999).random().toString()
                                val success = com.example.api.ResendManager.sendOtpEmail(email, otp)
                                if (success) {
                                    sentOtp = otp
                                    isAwaitingOtp = true
                                } else {
                                    error = "Failed to send OTP. Please try again."
                                }
                                isLoading = false
                            }
                        } else {
                            if (enteredOtp == sentOtp) {
                                isLoading = true
                                error = null
                                scope.launch {
                                    val result = authManager.signUpWithEmail(email, password)
                                    if (result.isSuccess) {
                                        val user = authManager.currentUser.value
                                        if (user != null) {
                                            val profile = com.example.data.UserProfile(
                                                email = user.email ?: "",
                                                name = fullName,
                                                avatarUrl = "https://i.pravatar.cc/150?u=${user.uid}",
                                                credits = 3000,
                                                savedVoices = emptyList()
                                            )
                                            firestoreRepository.saveUserProfile(user.uid, profile)
                                        }
                                        onNavigateToMain()
                                    } else {
                                        error = result.exceptionOrNull()?.message ?: "Sign up failed"
                                    }
                                    isLoading = false
                                }
                            } else {
                                error = "Invalid OTP"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    enabled = !isLoading && (if (!isAwaitingOtp) email.isNotBlank() && password.isNotBlank() else enteredOtp.length == 6)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isAwaitingOtp) "Continue" else "Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                
                
                Spacer(modifier = Modifier.weight(1f))
                
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "By continuing, you accept our ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Terms",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                uriHandler.openUri("https://www.quantisai.org/terms")
                            }
                        )
                        Text(
                            text = ", ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                uriHandler.openUri("https://www.quantisai.org/privacy")
                            }
                        )
                        Text(
                            text = ", and chat guidelines.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
