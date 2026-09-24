package com.example.data

import com.example.data.model.DecisionTemplate

object DecisionTemplates {
    val allTemplates = listOf(
        DecisionTemplate(
            title = "Career Crossroads: Startup vs Big Tech",
            category = "Career",
            description = "High growth equity potential vs compensation, perks, and brand prestige.",
            defaultOptions = listOf(
                "Join Early-Stage Startup",
                "Stay at Established Tech Company"
            ),
            defaultContext = "I have 5 years experience, value skill acceleration, but also want decent work-life balance."
        ),
        DecisionTemplate(
            title = "Living: Buy a Home vs Rent & Invest",
            category = "Finance",
            description = "Real estate equity and stability vs mobility and compounding in liquid markets.",
            defaultOptions = listOf(
                "Buy a Home with Mortgage",
                "Continue Renting & Invest Surplus"
            ),
            defaultContext = "Planning on staying in this city for at least 3-4 years. Current interest rates are moderate."
        ),
        DecisionTemplate(
            title = "Relocation: Move to New City vs Stay Put",
            category = "Lifestyle",
            description = "Excitement and fresh network vs established community and lower upheaval.",
            defaultOptions = listOf(
                "Relocate to New City",
                "Renew Lease & Stay Put"
            ),
            defaultContext = "Looking for renewed inspiration and outdoor access, but love my existing friend group."
        ),
        DecisionTemplate(
            title = "Work Model: Freelance vs Full-Time Job",
            category = "Career",
            description = "Total schedule autonomy and unlimited upside vs health benefits and predictable pay.",
            defaultOptions = listOf(
                "Go Full-Time Freelance / Consulting",
                "Accept Safe Salaried Role"
            ),
            defaultContext = "Have 6 months emergency runway saved. Want more agency over my working hours."
        ),
        DecisionTemplate(
            title = "Vehicle: Electric EV vs Reliable Hybrid",
            category = "Finance",
            description = "Zero gas and modern tech vs zero range anxiety and lower upfront premium.",
            defaultOptions = listOf(
                "Go 100% Electric (EV)",
                "Get Plug-in Hybrid (PHEV)"
            ),
            defaultContext = "I commute 25 miles daily and do 2-3 interstate road trips per year."
        ),
        DecisionTemplate(
            title = "Engineering: Rewrite from Scratch vs Incremental Refactor",
            category = "Tech",
            description = "Clean modern architecture vs keeping legacy revenue-generating code running.",
            defaultOptions = listOf(
                "Full Modern Rewrite",
                "Iterative Module-by-Module Refactor"
            ),
            defaultContext = "Team of 4 engineers, customers actively using the system daily."
        )
    )
}
