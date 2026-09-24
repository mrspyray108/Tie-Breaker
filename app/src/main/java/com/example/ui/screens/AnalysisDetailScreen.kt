package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullDecisionAnalysis
import com.example.ui.DecisionUiState
import com.example.ui.components.ComparisonTableView
import com.example.ui.components.ProsConsView
import com.example.ui.components.SwotView
import com.example.ui.components.TiebreakerVerdictView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisDetailScreen(
    uiState: DecisionUiState,
    onBack: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenCoinFlip: () -> Unit,
    onOpenResolve: () -> Unit,
    onUpdateCriterionWeight: (criterion: String, weight: Float) -> Unit,
    onResetCriterionWeights: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val decision = uiState.currentDecision
    val analysis = uiState.currentAnalysis

    if (decision == null || analysis == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No decision analysis loaded.", color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) {
                    Text("Return Home")
                }
            }
        }
        return
    }

    val tabs = listOf(
        Pair("Pros & Cons", Icons.AutoMirrored.Filled.FormatListBulleted),
        Pair("Comparison", Icons.Default.TableChart),
        Pair("SWOT", Icons.Default.GridView),
        Pair("The Verdict", Icons.Default.Bolt)
    )

    fun shareDecision() {
        val shareText = buildString {
            append("⚖️ The Tiebreaker Analysis: ${decision.title}\n\n")
            append("💡 Verdict: ${analysis.verdict.recommendedOption} (${analysis.verdict.confidencePercentage}% confidence)\n")
            append("Headline: ${analysis.verdict.headline}\n\n")
            append("Summary: ${analysis.summary}\n\n")
            append("Decisive Question: \"${analysis.verdict.decisiveQuestion}\"")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Decision Analysis")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = modifier.testTag("analysis_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = decision.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (uiState.isAiLive) Color(0xFF064E3B) else Color(0xFF1E293B),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = if (uiState.isAiLive) "Gemini 3.5 Flash" else "Smart Offline Engine",
                                    color = if (uiState.isAiLive) ProGreen else Color.LightGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }

                            if (decision.isResolved) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1E1B4B)
                                ) {
                                    Text(
                                        text = "DECIDED",
                                        color = TertiaryPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCoinFlip, modifier = Modifier.testTag("appbar_coin_flip")) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Flip Coin",
                            tint = PrimaryGold
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.testTag("appbar_favorite")) {
                        Icon(
                            imageVector = if (decision.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (decision.isFavorite) ConRed else Color.LightGray
                        )
                    }
                    IconButton(onClick = { shareDecision() }, modifier = Modifier.testTag("appbar_share")) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.LightGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (decision.isResolved) {
                        val chosenName = uiState.currentOptions.getOrNull(decision.chosenOptionIndex)?.name ?: "Option"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ProGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Committed to: $chosenName",
                                color = ProGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        TextButton(onClick = onOpenResolve) {
                            Text("Edit Note", color = PrimaryGold, fontSize = 12.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onOpenCoinFlip,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Coin Toss", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onOpenResolve,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ProGreen),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Commit Choice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mode Tabs Row
            TabRow(
                selectedTabIndex = uiState.selectedResultTab,
                containerColor = SurfaceDark,
                contentColor = PrimaryGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedResultTab]),
                        color = PrimaryGold,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, (tabTitle, tabIcon) ->
                    val isSelected = uiState.selectedResultTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { onSelectTab(index) },
                        modifier = Modifier.testTag("result_tab_$index"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tabIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) PrimaryGold else Color.Gray,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tabTitle,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (uiState.selectedResultTab) {
                0 -> ProsConsView(prosConsList = analysis.prosAndCons)
                1 -> ComparisonTableView(
                    criteriaList = analysis.comparisonCriteria,
                    userWeights = uiState.userCriteriaWeights,
                    onUpdateWeight = onUpdateCriterionWeight,
                    onResetWeights = onResetCriterionWeights
                )
                2 -> SwotView(swotList = analysis.swotAnalysis)
                3 -> TiebreakerVerdictView(
                    verdict = analysis.verdict,
                    onFlipCoin = onOpenCoinFlip,
                    onLockDecision = onOpenResolve
                )
            }
        }
    }
}
