package com.becetti.tools.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.becetti.tools.design.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutBackEasing),
        label = "scale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepNavy, SecondaryNavy, DeepNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle glow circles
        Box(
            modifier = Modifier
                .size(280.dp)
                .alpha(glowAlpha * 0.15f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryOrange.copy(alpha = 0.35f), DeepNavy.copy(alpha = 0f))
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            // Logo card
            Card(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(PrimaryOrange, DarkOrange))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "B",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Main title
            Text(
                "BECETTITOOLS",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(Modifier.height(6.dp))

            // Tagline with gradient underline
            Box(
                modifier = Modifier
                    .background(PrimaryOrange.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    "Everything you need. One smart toolbox.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = LightOrange,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Chips for features count
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureChip("15+ Tools")
                FeatureChip("Native")
                FeatureChip("Offline")
            }
        }

        // Bottom by ahmed becetti
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width(60.dp)
                    .background(PrimaryOrange.copy(alpha = 0.5f), RoundedCornerShape(1.dp))
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "by ahmed becetti",
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "v1.1.0 • Native • No Ads on Core Tools",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.9f))
    }
}
