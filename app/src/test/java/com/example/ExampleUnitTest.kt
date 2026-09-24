package com.example

import com.example.ai.FallbackDecisionEngine
import com.example.data.json.JsonHelper
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun fallbackEngineGeneratesValidAnalysis() {
        val analysis = FallbackDecisionEngine.generateAnalysis(
            question = "Should I switch careers to AI engineering?",
            options = listOf("Switch to AI", "Stay in Current Field"),
            userContext = "Have software background, want high growth."
        )

        assertNotNull(analysis)
        assertTrue(analysis.prosAndCons.isNotEmpty())
        assertEquals(2, analysis.prosAndCons.size)
        assertTrue(analysis.comparisonCriteria.isNotEmpty())
        assertEquals(2, analysis.swotAnalysis.size)
        assertNotNull(analysis.verdict)
        assertTrue(analysis.verdict.recommendedOption.isNotBlank())
        assertTrue(analysis.verdict.confidencePercentage in 50..100)

        // Verify JSON serialization round-trip
        val json = JsonHelper.fullAnalysisToJson(analysis)
        assertTrue(json.isNotBlank())

        val restored = JsonHelper.fullAnalysisFromJson(json)
        assertNotNull(restored)
        assertEquals(analysis.verdict.recommendedOption, restored?.verdict?.recommendedOption)
    }
}
