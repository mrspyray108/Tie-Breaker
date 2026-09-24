package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OptionSwot
import com.example.ui.theme.*

@Composable
fun SwotView(
    swotList: List<OptionSwot>,
    modifier: Modifier = Modifier
) {
    if (swotList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No SWOT analysis available.", color = Color.Gray)
        }
        return
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val currentSwot = swotList.getOrNull(selectedIndex) ?: swotList.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("swot_view")
    ) {
        if (swotList.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                containerColor = Color.Transparent,
                contentColor = PrimaryGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                swotList.forEachIndexed { index, option ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        text = {
                            Text(
                                text = option.optionName,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("swot_tab_$index")
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SWOT Matrix: ${currentSwot.optionName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (currentSwot.strategicAdvice.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Strategy: ${currentSwot.strategicAdvice}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryGold,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Strengths Quadrant
            item {
                SwotQuadrantCard(
                    title = "Strengths (Internal Advantages)",
                    items = currentSwot.strengths,
                    icon = Icons.Default.Shield,
                    accentColor = ProGreen,
                    containerColor = Color(0xFF042F24)
                )
            }

            // Weaknesses Quadrant
            item {
                SwotQuadrantCard(
                    title = "Weaknesses (Internal Vulnerabilities)",
                    items = currentSwot.weaknesses,
                    icon = Icons.Default.Warning,
                    accentColor = ThreatOrange,
                    containerColor = Color(0xFF331604)
                )
            }

            // Opportunities Quadrant
            item {
                SwotQuadrantCard(
                    title = "Opportunities (External Upside)",
                    items = currentSwot.opportunities,
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    accentColor = OpportunityBlue,
                    containerColor = Color(0xFF082647)
                )
            }

            // Threats Quadrant
            item {
                SwotQuadrantCard(
                    title = "Threats (External Risks & Pitfalls)",
                    items = currentSwot.threats,
                    icon = Icons.Default.AutoAwesome,
                    accentColor = ConRed,
                    containerColor = Color(0xFF380808)
                )
            }
        }
    }
}

@Composable
fun SwotQuadrantCard(
    title: String,
    items: List<String>,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            items.forEach { bullet ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
