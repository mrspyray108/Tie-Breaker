package com.example.data.json

import com.example.data.model.DecisionOption
import com.example.data.model.FullDecisionAnalysis
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonHelper {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val fullAnalysisAdapter = moshi.adapter(FullDecisionAnalysis::class.java)
    private val optionsListAdapter = moshi.adapter<List<DecisionOption>>(
        Types.newParameterizedType(List::class.java, DecisionOption::class.java)
    )

    fun fullAnalysisToJson(analysis: FullDecisionAnalysis): String {
        return fullAnalysisAdapter.toJson(analysis)
    }

    fun fullAnalysisFromJson(json: String): FullDecisionAnalysis? {
        return try {
            fullAnalysisAdapter.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun optionsToJson(options: List<DecisionOption>): String {
        return optionsListAdapter.toJson(options)
    }

    fun optionsFromJson(json: String): List<DecisionOption> {
        return try {
            optionsListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
