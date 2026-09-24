package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "decisions")
data class DecisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val context: String = "",
    val category: String = "General",
    val optionsJson: String,
    val analysisJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val chosenOptionIndex: Int = -1,
    val reflectionNotes: String = "",
    val isResolved: Boolean = false
)

@JsonClass(generateAdapter = true)
data class DecisionOption(
    val id: Int,
    val name: String,
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class ProConItem(
    val text: String,
    val weight: Int, // 1 to 5 (impact)
    val category: String, // Financial, Emotional, Practical, Risk, Growth
    val explanation: String = ""
)

@JsonClass(generateAdapter = true)
data class OptionProsCons(
    val optionName: String,
    val pros: List<ProConItem> = emptyList(),
    val cons: List<ProConItem> = emptyList(),
    val netScore: Int = 0,
    val keyTakeaway: String = ""
)

@JsonClass(generateAdapter = true)
data class CriterionScore(
    val optionName: String,
    val score: Int, // 1 to 10
    val commentary: String = ""
)

@JsonClass(generateAdapter = true)
data class CriterionComparison(
    val criterion: String,
    val importance: String = "Medium", // High, Medium, Low
    val scores: List<CriterionScore> = emptyList(),
    val winnerOption: String? = null
)

@JsonClass(generateAdapter = true)
data class OptionSwot(
    val optionName: String,
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val opportunities: List<String> = emptyList(),
    val threats: List<String> = emptyList(),
    val strategicAdvice: String = ""
)

@JsonClass(generateAdapter = true)
data class TiebreakerVerdict(
    val recommendedOption: String,
    val confidencePercentage: Int, // 50 to 99
    val headline: String,
    val detailedReasoning: String,
    val hiddenBlindspots: List<String> = emptyList(),
    val decisiveQuestion: String = "",
    val gutCheckAdvice: String = ""
)

@JsonClass(generateAdapter = true)
data class FullDecisionAnalysis(
    val summary: String = "",
    val prosAndCons: List<OptionProsCons> = emptyList(),
    val comparisonCriteria: List<CriterionComparison> = emptyList(),
    val swotAnalysis: List<OptionSwot> = emptyList(),
    val verdict: TiebreakerVerdict
)

data class DecisionTemplate(
    val title: String,
    val category: String,
    val description: String,
    val defaultOptions: List<String>,
    val defaultContext: String
)
