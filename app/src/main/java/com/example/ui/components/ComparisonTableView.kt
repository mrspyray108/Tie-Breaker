package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CriterionComparison
import com.example.ui.theme.*

@Composable
fun ComparisonTableView(
    criteriaList: List<CriterionComparison>,
    userWeights: Map<String, Float>,
    onUpdateWeight: (criterion: String, weight: Float) -> Unit,
    onResetWeights: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (criteriaList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No comparison criteria available.", color = Color.Gray)
        }
        return
    }

    var showWeightSliders by remember { mutableStateOf(false) }

    // Calculate dynamic weighted winner across all criteria
    val optionTotals = remember(criteriaList, userWeights) {
        val totals = mutableMapOf<String, Float>()
        criteriaList.forEach { crit ->
            val userMultiplier = userWeights[crit.criterion] ?: 1.0f
            val baseMultiplier = when (crit.importance.lowercase()) {
                "high" -> 1.5f
                "low" -> 0.75f
                else -> 1.0f
            }
            val effectiveWeight = baseMultiplier * userMultiplier

            crit.scores.forEach { scoreItem ->
                val current = totals[scoreItem.optionName] ?: 0f
                totals[scoreItem.optionName] = current + (scoreItem.score * effectiveWeight)
            }
        }
        totals
    }

    val dynamicLeader = remember(optionTotals) {
        optionTotals.maxByOrNull { it.value }?.key
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("comparison_table_view")
    ) {
        // Summary & Weight Toggle Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Multidimensional Matrix",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (dynamicLeader != null) {
                            Text(
                                text = "Current Matrix Leader: $dynamicLeader",
                                color = PrimaryGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    FilledTonalIconButton(
                        onClick = { showWeightSliders = !showWeightSliders },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (showWeightSliders) PrimaryGold else SurfaceVariantDark,
                            contentColor = if (showWeightSliders) Color.Black else PrimaryGold
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Tune Priorities"
                        )
                    }
                }

                AnimatedVisibility(visible = showWeightSliders) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "Adjust criteria multipliers to match your personal priorities:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        criteriaList.forEach { crit ->
                            val currentMultiplier = userWeights[crit.criterion] ?: 1.0f
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = crit.criterion,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Slider(
                                    value = currentMultiplier,
                                    onValueChange = { onUpdateWeight(crit.criterion, it) },
                                    valueRange = 0.5f..2.0f,
                                    steps = 3,
                                    modifier = Modifier.width(130.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = PrimaryGold,
                                        activeTrackColor = PrimaryGold
                                    )
                                )
                                Text(
                                    text = "%.1fx".format(currentMultiplier),
                                    fontSize = 11.sp,
                                    color = PrimaryGold,
                                    modifier = Modifier.width(36.dp)
                                )
                            }
                        }

                        TextButton(
                            onClick = onResetWeights,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Reset All to 1.0x", color = SecondaryCyan, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(criteriaList) { item ->
                CriterionCard(
                    criterion = item,
                    userWeight = userWeights[item.criterion] ?: 1.0f
                )
            }
        }
    }
}

@Composable
fun CriterionCard(
    criterion: CriterionComparison,
    userWeight: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = criterion.criterion,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (userWeight != 1.0f) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF451A03),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "%.1fx".format(userWeight),
                                color = PrimaryGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    val impBg = when (criterion.importance.lowercase()) {
                        "high" -> Color(0xFF450A0A)
                        "low" -> Color(0xFF1E293B)
                        else -> Color(0xFF1E1B4B)
                    }
                    val impText = when (criterion.importance.lowercase()) {
                        "high" -> ConRed
                        "low" -> Color.LightGray
                        else -> TertiaryPurple
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = impBg
                    ) {
                        Text(
                            text = "${criterion.importance} Priority",
                            color = impText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score bars for each option
            criterion.scores.forEach { scoreItem ->
                val isWinner = criterion.winnerOption.equals(scoreItem.optionName, ignoreCase = true)
                val barProgress = (scoreItem.score / 10f).coerceIn(0f, 1f)
                val barColor = if (isWinner) PrimaryGold else SecondaryCyan

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = scoreItem.optionName,
                                fontSize = 12.sp,
                                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                                color = if (isWinner) Color.White else Color(0xFFCBD5E1)
                            )
                            if (isWinner) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Winner",
                                    tint = PrimaryGold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Text(
                            text = "${scoreItem.score}/10",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = barColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { barProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = barColor,
                        trackColor = SurfaceVariantDark
                    )

                    if (scoreItem.commentary.isNotBlank()) {
                        Text(
                            text = scoreItem.commentary,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
