package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DecisionOption
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CoinFlipDialog(
    options: List<DecisionOption>,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    var isFlipping by remember { mutableStateOf(false) }

    val optionA = options.getOrNull(0)?.name ?: "Option A"
    val optionB = options.getOrNull(1)?.name ?: "Option B"

    var currentResult by remember { mutableStateOf<String?>(null) }
    var flipCount by remember { mutableIntStateOf(0) }

    fun doFlip() {
        if (isFlipping) return
        isFlipping = true
        currentResult = null

        coroutineScope.launch {
            val randomTurns = Random.nextInt(5, 9) * 360f
            val winnerIsOptionA = Random.nextBoolean()
            val finalTarget = randomTurns + (if (winnerIsOptionA) 0f else 180f)

            rotationAnim.animateTo(
                targetValue = rotationAnim.value + finalTarget,
                animationSpec = tween(
                    durationMillis = 1800,
                    easing = FastOutSlowInEasing
                )
            )

            currentResult = if (winnerIsOptionA) optionA else optionB
            flipCount++
            isFlipping = false
        }
    }

    Dialog(onDismissRequest = { if (!isFlipping) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("coin_flip_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = PrimaryGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "The Tiebreaker Coin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isFlipping,
                        modifier = Modifier.testTag("close_coin_flip")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A deadlocked decision? Flip the coin not to let fate decide, but to reveal how your gut reacts!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // The 3D Animated Coin
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer {
                            rotationY = rotationAnim.value
                            cameraDistance = 12f * density
                        }
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFDF70),
                                    Color(0xFFF59E0B),
                                    Color(0xFFB45309)
                                )
                            )
                        )
                        .border(4.dp, Color(0xFFFEF08A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val normalizedRotation = ((rotationAnim.value % 360) + 360) % 360
                    val isHeads = normalizedRotation in 0f..90f || normalizedRotation in 270f..360f

                    Text(
                        text = if (isHeads) "A" else "B",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF451A03),
                        modifier = Modifier.graphicsLayer {
                            if (!isHeads) rotationY = 180f
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Labels for Option A and B
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Side A", color = PrimaryGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(optionA, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
                    }
                    VerticalDivider(
                        modifier = Modifier.height(30.dp),
                        color = Color.Gray.copy(alpha = 0.4f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Side B", color = SecondaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(optionB, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (currentResult != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Landed on:",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                            Text(
                                text = currentResult!!,
                                color = PrimaryGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = SecondaryCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Gut Check: Did you feel relief or disappointment? If disappointed, pick the other one!",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { doFlip() },
                    enabled = !isFlipping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("flip_coin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isFlipping) "Flipping..." else if (flipCount == 0) "Flip Coin" else "Flip Again",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
