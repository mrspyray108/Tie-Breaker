package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DecisionTemplates
import com.example.data.db.AppDatabase
import com.example.data.json.JsonHelper
import com.example.data.model.DecisionEntity
import com.example.data.model.DecisionOption
import com.example.data.model.DecisionTemplate
import com.example.data.model.FullDecisionAnalysis
import com.example.data.repository.DecisionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME_INPUT,
    ANALYSIS_RESULT,
    HISTORY,
    TEMPLATES
}

data class DecisionUiState(
    val currentScreen: AppScreen = AppScreen.HOME_INPUT,
    val isGenerating: Boolean = false,
    val generationProgressText: String = "",
    val currentDecision: DecisionEntity? = null,
    val currentAnalysis: FullDecisionAnalysis? = null,
    val currentOptions: List<DecisionOption> = emptyList(),
    val isAiLive: Boolean = true,
    val selectedResultTab: Int = 0, // 0: Pros & Cons, 1: Comparison Table, 2: SWOT, 3: The Verdict
    val savedDecisions: List<DecisionEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryFilter: String = "All",
    val filterFavoritesOnly: Boolean = false,
    val showCoinFlipDialog: Boolean = false,
    val showResolveDialog: Boolean = false,
    val userCriteriaWeights: Map<String, Float> = emptyMap(), // Criterion name -> weight multiplier 0.5 to 2.0
    val errorMessage: String? = null
)

class DecisionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DecisionRepository

    private val _uiState = MutableStateFlow(DecisionUiState())
    val uiState: StateFlow<DecisionUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DecisionRepository(database.decisionDao())

        // Collect saved decisions
        viewModelScope.launch {
            repository.getAllDecisions().collect { list ->
                _uiState.update { it.copy(savedDecisions = list) }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectResultTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedResultTab = tabIndex) }
    }

    fun updateCriterionWeight(criterion: String, weight: Float) {
        _uiState.update { current ->
            val updated = current.userCriteriaWeights.toMutableMap()
            updated[criterion] = weight
            current.copy(userCriteriaWeights = updated)
        }
    }

    fun resetCriteriaWeights() {
        _uiState.update { it.copy(userCriteriaWeights = emptyMap()) }
    }

    fun submitDecision(
        title: String,
        options: List<String>,
        context: String,
        category: String
    ) {
        if (title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter the decision you want to evaluate.") }
            return
        }

        val filteredOptions = options.map { it.trim() }.filter { it.isNotBlank() }
        val finalOptions = if (filteredOptions.size >= 2) filteredOptions else listOf("Option A", "Option B")

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    generationProgressText = "Examining dilemma & psychological factors...",
                    errorMessage = null
                )
            }

            // Staged UX feedback to give the user a rich experience while Gemini processes
            val progressJob = launch {
                val stages = listOf(
                    "Analyzing cognitive biases & trade-offs...",
                    "Extracting weighted pros and cons...",
                    "Building multidimensional comparison matrix...",
                    "Synthesizing SWOT matrix...",
                    "Calculating The Tiebreaker's Verdict..."
                )
                for (stage in stages) {
                    delay(700)
                    _uiState.update { it.copy(generationProgressText = stage) }
                }
            }

            try {
                val (entity, analysis, isLive) = repository.analyzeDecision(
                    title = title.trim(),
                    options = finalOptions,
                    context = context.trim(),
                    category = category
                )
                progressJob.cancel()

                val parsedOptions = JsonHelper.optionsFromJson(entity.optionsJson)

                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        currentDecision = entity,
                        currentAnalysis = analysis,
                        currentOptions = parsedOptions,
                        isAiLive = isLive,
                        selectedResultTab = 0,
                        currentScreen = AppScreen.ANALYSIS_RESULT,
                        userCriteriaWeights = emptyMap()
                    )
                }
            } catch (e: Exception) {
                progressJob.cancel()
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = "Analysis could not be generated: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun loadSavedDecision(entity: DecisionEntity) {
        val analysis = JsonHelper.fullAnalysisFromJson(entity.analysisJson)
        val options = JsonHelper.optionsFromJson(entity.optionsJson)

        if (analysis != null) {
            _uiState.update {
                it.copy(
                    currentDecision = entity,
                    currentAnalysis = analysis,
                    currentOptions = options,
                    selectedResultTab = 0,
                    currentScreen = AppScreen.ANALYSIS_RESULT,
                    userCriteriaWeights = emptyMap()
                )
            }
        }
    }

    fun toggleFavorite(entity: DecisionEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity)
            if (_uiState.value.currentDecision?.id == entity.id) {
                _uiState.update {
                    it.copy(currentDecision = it.currentDecision?.copy(isFavorite = !entity.isFavorite))
                }
            }
        }
    }

    fun deleteDecision(id: Long) {
        viewModelScope.launch {
            repository.deleteDecisionById(id)
            if (_uiState.value.currentDecision?.id == id) {
                _uiState.update {
                    it.copy(
                        currentDecision = null,
                        currentAnalysis = null,
                        currentScreen = AppScreen.HOME_INPUT
                    )
                }
            }
        }
    }

    fun openResolveDialog() {
        _uiState.update { it.copy(showResolveDialog = true) }
    }

    fun closeResolveDialog() {
        _uiState.update { it.copy(showResolveDialog = false) }
    }

    fun resolveCurrentDecision(chosenOptionIndex: Int, notes: String) {
        val current = _uiState.value.currentDecision ?: return
        viewModelScope.launch {
            repository.markResolution(current.id, chosenOptionIndex, notes)
            val updated = current.copy(
                chosenOptionIndex = chosenOptionIndex,
                reflectionNotes = notes,
                isResolved = true
            )
            _uiState.update {
                it.copy(
                    currentDecision = updated,
                    showResolveDialog = false
                )
            }
        }
    }

    fun openCoinFlipDialog() {
        _uiState.update { it.copy(showCoinFlipDialog = true) }
    }

    fun closeCoinFlipDialog() {
        _uiState.update { it.copy(showCoinFlipDialog = false) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun toggleFavoritesOnly() {
        _uiState.update { it.copy(filterFavoritesOnly = !it.filterFavoritesOnly) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
