package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.api.RemoteConfigManager
import com.example.billing.PlayBillingManager

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

fun getPlanColor(planId: String?): Color {
    val id = planId?.lowercase() ?: "free"
    return SubscriptionTier.values().find { it.id == id }?.color ?: Color(0xFFA855F7)
}

enum class SubscriptionTier(
    val id: String,
    val title: String,
    val description: String,
    val defaultMonthlyPrice: Double,
    val defaultYearlyPrice: Double,
    val remoteConfigMonthlyKey: String?,
    val remoteConfigYearlyKey: String?,
    val color: Color,
    val playStoreMonthlyId: String,
    val playStoreYearlyId: String
) {
    FREE(
        id = "free",
        title = "FREE",
        description = "Get started with basic access",
        defaultMonthlyPrice = 0.0,
        defaultYearlyPrice = 0.0,
        remoteConfigMonthlyKey = null,
        remoteConfigYearlyKey = null,
        color = Color(0xFFA855F7), // Radiant Purple
        playStoreMonthlyId = "",
        playStoreYearlyId = ""
    ),
    STARTER(
        id = "starter",
        title = "STARTER",
        description = "Perfect for individuals",
        defaultMonthlyPrice = 149.0,
        defaultYearlyPrice = 1490.0,
        remoteConfigMonthlyKey = "RAZORPAY_PLAN_STARTER_MONTHLY",
        remoteConfigYearlyKey = "RAZORPAY_PLAN_STARTER_YEARLY",
        color = Color(0xFFF97316), // Glowing Orange
        playStoreMonthlyId = "starter_monthly",
        playStoreYearlyId = "starter-yearly"
    ),
    CREATOR(
        id = "creator",
        title = "CREATOR",
        description = "For creators and growing teams",
        defaultMonthlyPrice = 399.0,
        defaultYearlyPrice = 3990.0,
        remoteConfigMonthlyKey = "RAZORPAY_PLAN_CREATOR_MONTHLY",
        remoteConfigYearlyKey = "RAZORPAY_PLAN_CREATOR_YEARLY",
        color = Color(0xFF3B82F6), // Brilliant Blue
        playStoreMonthlyId = "creator_monthly",
        playStoreYearlyId = "creator_yearly"
    ),
    PRO(
        id = "pro",
        title = "PRO",
        description = "For professionals and enterprises",
        defaultMonthlyPrice = 999.0,
        defaultYearlyPrice = 9990.0,
        remoteConfigMonthlyKey = "RAZORPAY_PLAN_PRO_MONTHLY",
        remoteConfigYearlyKey = "RAZORPAY_PLAN_PRO_YEARLY",
        color = Color(0xFF10B981), // Emerald Green
        playStoreMonthlyId = "pro_monthly",
        playStoreYearlyId = "pro_yearly"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(authManager: com.example.auth.AuthManager, onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Monthly, 1 = Yearly
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val coroutineScope = rememberCoroutineScope()

    val user = authManager.currentUser.collectAsState(initial = authManager.currentUser.value).value
    val firestoreRepository = remember { com.example.data.FirestoreRepository() }
    var currentPlan by remember { mutableStateOf("free") }
    
    val billingManager = remember(user) {
        PlayBillingManager(context, coroutineScope, firestoreRepository, user?.uid) { newPlan ->
            currentPlan = newPlan
        }
    }
    
    val productDetailsList by billingManager.productDetailsList.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            val profile = firestoreRepository.getUserProfile(user.uid, user.email ?: "")
            currentPlan = profile.subscriptionPlan.ifEmpty { "free" }
        }
    }

    LaunchedEffect(Unit) {
        try {
            RemoteConfigManager.initialize()
        } catch (e: Exception) {
            Log.e("SubscriptionScreen", "Failed to init RemoteConfig on load", e)
        }
    }

    fun startPayment(tier: SubscriptionTier) {
        val targetProductId = if (selectedTab == 0) tier.playStoreMonthlyId else tier.playStoreYearlyId
        val product = productDetailsList.find { it.productId == targetProductId }
        
        if (activity != null && product != null) {
            billingManager.launchBillingFlow(activity, product)
        } else if (product == null) {
            Toast.makeText(context, "Product not available for purchase yet. Please try again later.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Activity window unavailable for payment flow.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070C)) // Deep space premium black background
    ) {
        // Ambient background glowing blobs for premium aesthetic
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x158B5CF6), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1210B981), Color.Transparent),
                    center = Offset(size.width, size.height * 0.7f),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width, size.height * 0.7f),
                radius = size.width * 0.9f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF131324),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Premium Plans",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Unlock unlimited AI voice generation & cloning",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Billing Interval Toggle (Custom Pill Selector)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF121224), CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Sliding indicator
                val density = androidx.compose.ui.platform.LocalDensity.current
                var containerWidth by remember { mutableStateOf(0.dp) }
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    containerWidth = maxWidth
                }

                val indicatorWidth = containerWidth / 2 - 4.dp
                val indicatorOffset by animateDpAsState(
                    targetValue = if (selectedTab == 0) 0.dp else indicatorWidth,
                    animationSpec = tween(durationMillis = 250),
                    label = "IndicatorOffset"
                )

                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(indicatorWidth)
                        .fillMaxHeight()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            CircleShape
                        )
                )

                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .clickable { selectedTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Monthly",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Yearly",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SAVE 15%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subscriptions List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SubscriptionTier.values().forEach { tier ->
                    val isFree = tier == SubscriptionTier.FREE
                    val isCurrentPlan = tier.id.lowercase() == currentPlan.lowercase()
                    
                    val targetProductId = if (selectedTab == 0) tier.playStoreMonthlyId else tier.playStoreYearlyId
                    val productDetail = productDetailsList.find { it.productId == targetProductId }
                    val offerDetails = productDetail?.subscriptionOfferDetails?.firstOrNull { it.offerId == null } ?: productDetail?.subscriptionOfferDetails?.firstOrNull()
                    val formattedPrice = offerDetails?.pricingPhases?.pricingPhaseList?.lastOrNull()?.formattedPrice
                    
                    val priceSuffix = if (selectedTab == 0) "/m" else "/y"

                    val displayPriceText = if (isFree) {
                        "FREE"
                    } else if (formattedPrice != null) {
                        "$formattedPrice$priceSuffix"
                    } else {
                        // Fallback logic
                        val configKey = if (selectedTab == 0) tier.remoteConfigMonthlyKey else tier.remoteConfigYearlyKey
                        val configPrice = configKey?.let { RemoteConfigManager.getPlanPrice(it) }
                        val doublePrice = configPrice?.toDoubleOrNull() ?: if (selectedTab == 0) tier.defaultMonthlyPrice else tier.defaultYearlyPrice
                        val displayPriceString = if (doublePrice % 1.0 == 0.0) {
                            doublePrice.toInt().toString()
                        } else {
                            doublePrice.toString()
                        }
                        "₹$displayPriceString$priceSuffix"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0F1A), RoundedCornerShape(24.dp))
                            .border(
                                BorderStroke(
                                    if (isCurrentPlan) 2.dp else 1.dp,
                                    if (isCurrentPlan) {
                                        Brush.verticalGradient(listOf(tier.color, tier.color))
                                    } else {
                                        Brush.linearGradient(
                                            listOf(tier.color.copy(alpha = 0.6f), tier.color.copy(alpha = 0.05f))
                                        )
                                    }
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                if (isCurrentPlan) {
                                    Toast.makeText(context, "You are already subscribed to the ${tier.title} plan!", Toast.LENGTH_SHORT).show()
                                } else if (isFree) {
                                    if (currentPlan.lowercase() != "free") {
                                        val currentPlanTitle = currentPlan.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
                                        Toast.makeText(context, "You are already enjoying the $currentPlanTitle plan!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "You are currently enjoying the Free Plan!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    startPayment(tier)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Box with radial gradient background and colored border
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(tier.color.copy(alpha = 0.25f), tier.color.copy(alpha = 0.05f))
                                        ),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .border(BorderStroke(1.dp, tier.color.copy(alpha = 0.4f)), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (tier) {
                                    SubscriptionTier.FREE -> GiftIcon()
                                    SubscriptionTier.STARTER -> LightningIcon()
                                    SubscriptionTier.CREATOR -> CrownIcon()
                                    SubscriptionTier.PRO -> StarIcon()
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Text details
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tier.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                    if (isCurrentPlan) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(tier.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .border(BorderStroke(1.dp, tier.color), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "CURRENT",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = tier.color
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tier.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Price Pill (similar to the UI image)
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(tier.color.copy(alpha = 0.08f), CircleShape)
                                        .border(BorderStroke(1.dp, tier.color.copy(alpha = 0.4f)), CircleShape)
                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                     contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = displayPriceText,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = tier.color
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                if (isCurrentPlan) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(tier.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Current Plan",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF475569),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GiftIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val color = Color.White
        val strokeWidth = 2.dp.toPx()
        
        // Draw Box lid
        val lidY = h * 0.4f
        val lidHeight = h * 0.12f
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, lidY),
            size = Size(w * 0.7f, lidHeight),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        
        // Draw Box body
        val bodyY = lidY + lidHeight
        val bodyHeight = h * 0.35f
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.22f, bodyY),
            size = Size(w * 0.56f, bodyHeight),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        
        // Draw Ribbon (vertical line)
        drawLine(
            color = color,
            start = Offset(w * 0.5f, lidY),
            end = Offset(w * 0.5f, bodyY + bodyHeight),
            strokeWidth = strokeWidth
        )
        
        // Draw Bow (loops at the top)
        val bowPath = Path().apply {
            // Left loop
            moveTo(w * 0.5f, lidY)
            cubicTo(w * 0.3f, lidY - h * 0.25f, w * 0.15f, lidY - h * 0.05f, w * 0.5f, lidY)
            // Right loop
            moveTo(w * 0.5f, lidY)
            cubicTo(w * 0.7f, lidY - h * 0.25f, w * 0.85f, lidY - h * 0.05f, w * 0.5f, lidY)
        }
        drawPath(
            path = bowPath,
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun LightningIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        
        val path = Path().apply {
            moveTo(w * 0.58f, h * 0.12f)
            lineTo(w * 0.28f, h * 0.52f)
            lineTo(w * 0.52f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.88f)
            lineTo(w * 0.72f, h * 0.48f)
            lineTo(w * 0.48f, h * 0.48f)
            close()
        }
        
        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
fun CrownIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        
        val crownPath = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.12f, h * 0.35f)
            lineTo(w * 0.35f, h * 0.52f)
            lineTo(w * 0.5f, h * 0.25f)
            lineTo(w * 0.65f, h * 0.52f)
            lineTo(w * 0.88f, h * 0.35f)
            lineTo(w * 0.85f, h * 0.75f)
            close()
        }
        
        drawPath(
            path = crownPath,
            color = Color.White,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
        
        // Little circles on top of the 3 peaks
        drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.12f, h * 0.35f))
        drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.5f, h * 0.25f))
        drawCircle(color = Color.White, radius = 1.5.dp.toPx(), center = Offset(w * 0.88f, h * 0.35f))
    }
}

@Composable
fun StarIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        
        val starPath = Path().apply {
            val centerX = w * 0.5f
            val centerY = h * 0.52f
            val outerRadius = w * 0.4f
            val innerRadius = w * 0.18f
            
            for (i in 0 until 10) {
                val angle = Math.toRadians((i * 36 - 90).toDouble())
                val radius = if (i % 2 == 0) outerRadius else innerRadius
                val x = (centerX + Math.cos(angle) * radius).toFloat()
                val y = (centerY + Math.sin(angle) * radius).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        
        drawPath(
            path = starPath,
            color = Color.White,
            style = Stroke(
                width = strokeWidth,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round
            )
        )
    }
}
