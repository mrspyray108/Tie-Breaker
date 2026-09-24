package com.example.data.repository

import com.example.ai.GeminiDecisionService
import com.example.data.db.DecisionDao
import com.example.data.json.JsonHelper
import com.example.data.model.DecisionEntity
import com.example.data.model.DecisionOption
import com.example.data.model.FullDecisionAnalysis
import kotlinx.coroutines.flow.Flow

class DecisionRepository(
    private val decisionDao: DecisionDao,
    private val geminiService: GeminiDecisionService = GeminiDecisionService()
) {
    fun getAllDecisions(): Flow<List<DecisionEntity>> = decisionDao.getAllDecisions()

    suspend fun getDecisionById(id: Long): DecisionEntity? = decisionDao.getDecisionById(id)

    suspend fun analyzeDecision(
        title: String,
        options: List<String>,
        context: String,
        category: String = "General"
    ): Triple<DecisionEntity, FullDecisionAnalysis, Boolean> {
        val (analysis, isAiLive) = geminiService.analyzeDecision(
            question = title,
            options = options,
            userContext = context
        )

        val optionModels = options.mapIndexed { idx, name ->
            DecisionOption(id = idx, name = name)
        }

        val entity = DecisionEntity(
            title = title,
            context = context,
            category = category,
            optionsJson = JsonHelper.optionsToJson(optionModels),
            analysisJson = JsonHelper.fullAnalysisToJson(analysis),
            createdAt = System.currentTimeMillis(),
            isFavorite = false,
            chosenOptionIndex = -1,
            isResolved = false
        )

        val insertedId = decisionDao.insertDecision(entity)
        val savedEntity = entity.copy(id = insertedId)

        return Triple(savedEntity, analysis, isAiLive)
    }

    suspend fun updateDecision(entity: DecisionEntity) {
        decisionDao.updateDecision(entity)
    }

    suspend fun deleteDecisionById(id: Long) {
        decisionDao.deleteDecisionById(id)
    }

    suspend fun toggleFavorite(entity: DecisionEntity) {
        decisionDao.updateDecision(entity.copy(isFavorite = !entity.isFavorite))
    }

    suspend fun markResolution(id: Long, chosenOptionIndex: Int, notes: String) {
        val current = decisionDao.getDecisionById(id) ?: return
        val updated = current.copy(
            chosenOptionIndex = chosenOptionIndex,
            reflectionNotes = notes,
            isResolved = true
        )
        decisionDao.updateDecision(updated)
    }
}
