package com.example.ai

import com.example.data.model.*
import kotlin.math.roundToInt

object FallbackDecisionEngine {

    fun generateAnalysis(
        question: String,
        options: List<String>,
        userContext: String
    ): FullDecisionAnalysis {
        val safeOptions = if (options.isNotEmpty()) options else listOf("Option A", "Option B")

        val prosAndConsList = safeOptions.mapIndexed { index, optionName ->
            val isFirst = index == 0
            val pros = listOf(
                ProConItem(
                    text = if (isFirst) "Higher upside & faster personal growth" else "Greater predictability & immediate stability",
                    weight = if (isFirst) 5 else 4,
                    category = if (isFirst) "Growth" else "Emotional",
                    explanation = "Directly positions you for long-term dividends and compounding experience."
                ),
                ProConItem(
                    text = if (isFirst) "Greater autonomy and creative control" else "Proven playbook with lower upfront anxiety",
                    weight = 4,
                    category = "Practical",
                    explanation = "Reduces friction and allows you to execute with confidence."
                ),
                ProConItem(
                    text = if (isFirst) "Strong alignment with bold ambition" else "Preserves energy and protects margin of safety",
                    weight = 3,
                    category = if (isFirst) "Emotional" else "Risk",
                    explanation = "Frees up mental bandwidth to focus on secondary commitments."
                )
            )

            val cons = listOf(
                ProConItem(
                    text = if (isFirst) "Steeper learning curve and higher initial friction" else "Risk of stagnation and missing high-upside window",
                    weight = 4,
                    category = if (isFirst) "Risk" else "Growth",
                    explanation = "Demands significant patience and tolerance for ambiguity."
                ),
                ProConItem(
                    text = if (isFirst) "Requires higher upfront energy investment" else "Opportunity cost of not discovering your true potential",
                    weight = 3,
                    category = if (isFirst) "Practical" else "Emotional",
                    explanation = "Every commitment has an invisible alternative trade-off."
                ),
                ProConItem(
                    text = if (isFirst) "Harder to reverse once momentum builds" else "May require revisiting this exact same decision in 12 months",
                    weight = 2,
                    category = "Risk",
                    explanation = "Reversibility is an essential factor in decision hygiene."
                )
            )

            val totalPros = pros.sumOf { it.weight }
            val totalCons = cons.sumOf { it.weight }
            val net = totalPros - totalCons

            OptionProsCons(
                optionName = optionName,
                pros = pros,
                cons = cons,
                netScore = net,
                keyTakeaway = if (isFirst)
                    "Bold move with high upside, ideal if you have capacity for initial turbulence."
                else
                    "Safe and steady foundation that minimizes catastrophe at the cost of ceiling."
            )
        }

        // Comparison criteria
        val criteriaList = listOf(
            CriterionComparison(
                criterion = "Long-Term Value & Compounding",
                importance = "High",
                scores = safeOptions.mapIndexed { idx, opt ->
                    CriterionScore(
                        optionName = opt,
                        score = if (idx == 0) 9 else 6,
                        commentary = if (idx == 0) "Exponential compounding upside" else "Linear, predictable returns"
                    )
                },
                winnerOption = safeOptions.firstOrNull()
            ),
            CriterionComparison(
                criterion = "Daily Stress & Mental Load",
                importance = "High",
                scores = safeOptions.mapIndexed { idx, opt ->
                    CriterionScore(
                        optionName = opt,
                        score = if (idx == 0) 5 else 8,
                        commentary = if (idx == 0) "Higher initial anxiety" else "Lower daily friction"
                    )
                },
                winnerOption = safeOptions.getOrNull(1) ?: safeOptions.firstOrNull()
            ),
            CriterionComparison(
                criterion = "Financial / Resource ROI",
                importance = "Medium",
                scores = safeOptions.mapIndexed { idx, opt ->
                    CriterionScore(
                        optionName = opt,
                        score = if (idx == 0) 8 else 7,
                        commentary = if (idx == 0) "High risk/reward ratio" else "Steady capital preservation"
                    )
                },
                winnerOption = safeOptions.firstOrNull()
            ),
            CriterionComparison(
                criterion = "Decision Reversibility (Two-Way Door)",
                importance = "Medium",
                scores = safeOptions.mapIndexed { idx, opt ->
                    CriterionScore(
                        optionName = opt,
                        score = if (idx == 0) 6 else 9,
                        commentary = if (idx == 0) "Moderate difficulty to unwind" else "Easily pivoted if priorities shift"
                    )
                },
                winnerOption = safeOptions.getOrNull(1) ?: safeOptions.firstOrNull()
            ),
            CriterionComparison(
                criterion = "Alignment with Stated Priorities",
                importance = "High",
                scores = safeOptions.mapIndexed { idx, opt ->
                    CriterionScore(
                        optionName = opt,
                        score = if (idx == 0) 8 else 7,
                        commentary = if (idx == 0) "Directly addresses growth" else "Guards current well-being"
                    )
                },
                winnerOption = safeOptions.firstOrNull()
            )
        )

        // SWOT Analysis
        val swotList = safeOptions.mapIndexed { idx, opt ->
            val isFirst = idx == 0
            OptionSwot(
                optionName = opt,
                strengths = if (isFirst) listOf(
                    "High leverage and career/life acceleration",
                    "Breaks complacency and unlocks fresh networks",
                    "Stronger positioning in changing landscapes"
                ) else listOf(
                    "Predictable cash flow and lifestyle stability",
                    "Established relationships and low operational friction",
                    "Zero ramp-up learning curve"
                ),
                weaknesses = if (isFirst) listOf(
                    "High initial time and attention tax",
                    "Uncertain short-term payoff",
                    "Demands resilience during adaptation phase"
                ) else listOf(
                    "Capped ceiling and limited trajectory shift",
                    "Vulnerability to boredom or gradual erosion of edge",
                    "Higher regret probability if circumstances evolve"
                ),
                opportunities = if (isFirst) listOf(
                    "Compound advantages over a 3-5 year horizon",
                    "Discovering latent capabilities under pressure",
                    "Building rare skills with asymmetric upside"
                ) else listOf(
                    "Reinvesting preserved energy into side ventures",
                    "Deepening mastery in a familiar domain",
                    "Maintaining peak health and personal bandwidth"
                ),
                threats = if (isFirst) listOf(
                    "Burnout if boundaries are not strictly defended",
                    "External market shifts before payoff arrives",
                    "Sunk-cost trap if not monitored with milestones"
                ) else listOf(
                    "The quiet risk of obsolescence",
                    "Watching peers leap ahead on steeper trajectories",
                    "Eventual forced change on less favorable terms"
                ),
                strategicAdvice = if (isFirst)
                    "Establish a 90-day review gate with measurable health & progress metrics."
                else
                    "Protect this choice by aggressively allocating your saved bandwidth toward secondary goals."
            )
        }

        val recommended = safeOptions.firstOrNull() ?: "Option A"

        val verdict = TiebreakerVerdict(
            recommendedOption = recommended,
            confidencePercentage = 78,
            headline = "Break the Deadlock: Lean Into Asymmetric Growth",
            detailedReasoning = "When two choices appear evenly balanced, our cognitive bias usually overweights the visibility of short-term discomfort while underestimating the compounding regret of inaction. While '${safeOptions.getOrNull(1) ?: "the alternative"}' offers comforting safety, '$recommended' provides superior asymmetric upside. If you set firm guardrails, the expected value heavily favors '$recommended'.",
            hiddenBlindspots = listOf(
                "Status Quo Bias: Remaining still feels free of risk, but often carries the highest invisible opportunity cost.",
                "The 10/10/10 Rule: How will you feel about this choice in 10 minutes, 10 months, and 10 years?"
            ),
            decisiveQuestion = "If a friend came to you with this exact dilemma and context, which path would you advise them to take?",
            gutCheckAdvice = "Flip a coin between your top two choices. In the brief split-second the coin is in the air, notice which side your heart is secretly praying lands face-up. That reaction is your real decision."
        )

        return FullDecisionAnalysis(
            summary = "Evaluating '$question' between ${safeOptions.joinToString(" and ")}. While both options possess valid merits, they trade off immediate security against long-term acceleration.",
            prosAndCons = prosAndConsList,
            comparisonCriteria = criteriaList,
            swotAnalysis = swotList,
            verdict = verdict
        )
    }
}
