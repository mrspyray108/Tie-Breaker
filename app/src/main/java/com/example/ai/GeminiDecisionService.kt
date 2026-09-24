package com.example.ai

import com.example.BuildConfig
import com.example.data.json.JsonHelper
import com.example.data.model.*
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val responseMimeType: String = "application/json",
    val temperature: Float = 0.4f
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateContentRequest
    ): GeminiGenerateContentResponse
}

class GeminiDecisionService {

    private val apiService: GeminiApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(JsonHelper.moshi))
            .build()

        retrofit.create(GeminiApiService::class.java)
    }

    suspend fun analyzeDecision(
        question: String,
        options: List<String>,
        userContext: String
    ): Pair<FullDecisionAnalysis, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val isKeyValid = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && !apiKey.contains("PLACEHOLDER", ignoreCase = true)

        if (!isKeyValid) {
            val fallback = FallbackDecisionEngine.generateAnalysis(question, options, userContext)
            return@withContext Pair(fallback, false)
        }

        try {
            val prompt = buildDecisionPrompt(question, options, userContext)
            val request = GeminiGenerateContentRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.35f
                )
            )

            val response = apiService.generateContent(apiKey = apiKey, request = request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (!rawText.isNullOrBlank()) {
                val cleanedJson = rawText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val analysis = JsonHelper.fullAnalysisFromJson(cleanedJson)
                if (analysis != null && analysis.prosAndCons.isNotEmpty()) {
                    return@withContext Pair(analysis, true)
                }
            }

            // Fallback if parsing was empty
            val fallback = FallbackDecisionEngine.generateAnalysis(question, options, userContext)
            Pair(fallback, false)
        } catch (e: Exception) {
            e.printStackTrace()
            val fallback = FallbackDecisionEngine.generateAnalysis(question, options, userContext)
            Pair(fallback, false)
        }
    }

    private fun buildDecisionPrompt(
        question: String,
        options: List<String>,
        userContext: String
    ): String {
        val optionsList = options.joinToString(", ") { "\"$it\"" }
        return """
            You are "The Tiebreaker", an expert strategic decision advisor, executive coach, and behavioral economist.
            Help the user evaluate this critical decision thoroughly, impartially, and decisively.

            Decision to Make: "$question"
            Options under consideration: [$optionsList]
            User Priorities / Context: ${if (userContext.isNotBlank()) "\"$userContext\"" else "None provided"}

            Generate a comprehensive decision package in valid JSON with:
            1. "summary": A 2-sentence executive summary of the core dilemma and tension between options.
            2. "prosAndCons": For EACH option, provide:
               - "optionName": Exact name of option
               - "pros": List of { "text": short bullet, "weight": 1 to 5 (impact), "category": "Financial"|"Emotional"|"Practical"|"Risk"|"Growth", "explanation": concise justification }
               - "cons": List of { "text": short bullet, "weight": 1 to 5 (severity), "category": "Financial"|"Emotional"|"Practical"|"Risk"|"Growth", "explanation": concise justification }
               - "netScore": calculated numeric balance (+/-)
               - "keyTakeaway": 1-sentence punchy takeaway
            3. "comparisonCriteria": 5 to 7 key comparison dimensions (e.g., Financial ROI, Time/Effort, Risk & Reversibility, Long-Term Fulfillment, Daily Stress).
               Each criterion has:
               - "criterion": Name of criterion
               - "importance": "High" | "Medium" | "Low"
               - "scores": List for EACH option { "optionName": ..., "score": 1 to 10, "commentary": brief 10-word reason }
               - "winnerOption": Name of the option that wins this criterion
            4. "swotAnalysis": For EACH option, provide:
               - "optionName": Exact name of option
               - "strengths": 3-4 bullet strings (internal advantages)
               - "weaknesses": 3-4 bullet strings (internal limitations/drawbacks)
               - "opportunities": 3-4 bullet strings (external upside, future doors opened)
               - "threats": 3-4 bullet strings (external risks, worst-case vulnerabilities)
               - "strategicAdvice": 1-sentence strategy for executing this option successfully
            5. "verdict": "The Tiebreaker's Verdict":
               - "recommendedOption": The single clear winning option picked by The Tiebreaker
               - "confidencePercentage": Integer between 65 and 95
               - "headline": Punchy 5-word headline (e.g. "Choose Growth Over Temporary Comfort")
               - "detailedReasoning": Clear, empathetic, logically sound paragraph breaking the tie.
               - "hiddenBlindspots": 2-3 often-overlooked factors the user might be ignoring
               - "decisiveQuestion": The one piercing question the user should ask themselves to instantly know their answer
               - "gutCheckAdvice": A behavioral psychology tip (e.g., "Imagine someone told you that you MUST do Option B right now—did you feel relief or regret?")

            Respond with ONLY the JSON object. No Markdown code fencing, no extra commentary.
        """.trimIndent()
    }
}
