package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DecisionOption
import com.example.ui.AppScreen
import com.example.ui.DecisionViewModel
import com.example.ui.components.CoinFlipDialog
import com.example.ui.components.ResolutionDialog
import com.example.ui.screens.AnalysisDetailScreen
import com.example.ui.screens.DecisionHistoryScreen
import com.example.ui.screens.DecisionInputScreen
import com.example.ui.screens.DecisionTemplatesScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceDark

class MainActivity : ComponentActivity() {

    private val viewModel: DecisionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TiebreakerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TiebreakerApp(viewModel: DecisionViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root"),
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = PrimaryGold,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.HOME_INPUT,
                    onClick = { viewModel.navigateTo(AppScreen.HOME_INPUT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "New Decision"
                        )
                    },
                    label = { Text("New", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_new_decision")
                )

                if (uiState.currentAnalysis != null) {
                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.ANALYSIS_RESULT,
                        onClick = { viewModel.navigateTo(AppScreen.ANALYSIS_RESULT) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Analysis"
                            )
                        },
                        label = { Text("Analysis", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SecondaryCyan,
                            indicatorColor = SecondaryCyan,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_active_analysis")
                    )
                }

                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.HISTORY,
                    onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.savedDecisions.isNotEmpty()) {
                                    Badge(
                                        containerColor = PrimaryGold,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${uiState.savedDecisions.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryEdu,
                                contentDescription = "Vault"
                            )
                        }
                    },
                    label = { Text("Vault", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_vault")
                )

                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.TEMPLATES,
                    onClick = { viewModel.navigateTo(AppScreen.TEMPLATES) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CollectionsBookmark,
                            contentDescription = "Playbooks"
                        )
                    },
                    label = { Text("Playbooks", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_templates")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                AppScreen.HOME_INPUT -> {
                    DecisionInputScreen(
                        uiState = uiState,
                        onSubmit = { title, options, context, category ->
                            viewModel.submitDecision(title, options, context, category)
                        },
                        onSelectTemplate = { template ->
                            viewModel.submitDecision(
                                title = template.title,
                                options = template.defaultOptions,
                                context = template.defaultContext,
                                category = template.category
                            )
                        },
                        onViewTemplates = {
                            viewModel.navigateTo(AppScreen.TEMPLATES)
                        }
                    )
                }

                AppScreen.ANALYSIS_RESULT -> {
                    AnalysisDetailScreen(
                        uiState = uiState,
                        onBack = { viewModel.navigateTo(AppScreen.HOME_INPUT) },
                        onSelectTab = { viewModel.selectResultTab(it) },
                        onToggleFavorite = {
                            uiState.currentDecision?.let { viewModel.toggleFavorite(it) }
                        },
                        onOpenCoinFlip = { viewModel.openCoinFlipDialog() },
                        onOpenResolve = { viewModel.openResolveDialog() },
                        onUpdateCriterionWeight = { crit, weight ->
                            viewModel.updateCriterionWeight(crit, weight)
                        },
                        onResetCriterionWeights = {
                            viewModel.resetCriteriaWeights()
                        }
                    )
                }

                AppScreen.HISTORY -> {
                    DecisionHistoryScreen(
                        uiState = uiState,
                        onSelectDecision = { entity ->
                            viewModel.loadSavedDecision(entity)
                        },
                        onDeleteDecision = { id ->
                            viewModel.deleteDecision(id)
                        },
                        onToggleFavorite = { entity ->
                            viewModel.toggleFavorite(entity)
                        },
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        onCategoryChanged = { viewModel.setCategoryFilter(it) },
                        onToggleFavoritesOnly = { viewModel.toggleFavoritesOnly() },
                        onStartNew = { viewModel.navigateTo(AppScreen.HOME_INPUT) }
                    )
                }

                AppScreen.TEMPLATES -> {
                    DecisionTemplatesScreen(
                        onSelectTemplate = { template ->
                            viewModel.submitDecision(
                                title = template.title,
                                options = template.defaultOptions,
                                context = template.defaultContext,
                                category = template.category
                            )
                        }
                    )
                }
            }

            // Coin Flip Dialog
            if (uiState.showCoinFlipDialog) {
                val currentOptions = if (uiState.currentOptions.isNotEmpty()) {
                    uiState.currentOptions
                } else {
                    listOf(
                        DecisionOption(0, "Option A"),
                        DecisionOption(1, "Option B")
                    )
                }
                CoinFlipDialog(
                    options = currentOptions,
                    onDismiss = { viewModel.closeCoinFlipDialog() }
                )
            }

            // Resolution Dialog
            if (uiState.showResolveDialog && uiState.currentDecision != null) {
                ResolutionDialog(
                    options = uiState.currentOptions,
                    currentSelectedIndex = uiState.currentDecision!!.chosenOptionIndex,
                    initialNotes = uiState.currentDecision!!.reflectionNotes,
                    onDismiss = { viewModel.closeResolveDialog() },
                    onConfirm = { chosenIndex, notes ->
                        viewModel.resolveCurrentDecision(chosenIndex, notes)
                    }
                )
            }
        }
    }
}
