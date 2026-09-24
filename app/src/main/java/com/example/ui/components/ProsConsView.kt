package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OptionProsCons
import com.example.data.model.ProConItem
import com.example.ui.theme.*

@Composable
fun ProsConsView(
    prosConsList: List<OptionProsCons>,
    modifier: Modifier = Modifier
) {
    if (prosConsList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No pros and cons analysis available.", color = Color.Gray)
        }
        return
    }

    var selectedOptionIndex by remember { mutableIntStateOf(0) }
    val currentOption = prosConsList.getOrNull(selectedOptionIndex) ?: prosConsList.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("pros_cons_view")
    ) {
        // Option selector tabs if multiple options
        if (prosConsList.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = selectedOptionIndex,
                containerColor = Color.Transparent,
                contentColor = PrimaryGold,
                edgePadding = 0.dp,
                divider = {}
            ) {
                prosConsList.forEachIndexed { index, option ->
                    Tab(
                        selected = selectedOptionIndex == index,
                        onClick = { selectedOptionIndex = index },
                        text = {
                            Text(
                                text = option.optionName,
                                fontWeight = if (selectedOptionIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("pro_con_tab_$index")
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Net balance summary card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentOption.optionName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            val isPositive = currentOption.netScore >= 0
                            val badgeBg = if (isPositive) Color(0xFF064E3B) else Color(0xFF450A0A)
                            val badgeColor = if (isPositive) ProGreen else ConRed

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = badgeBg
                            ) {
                                Text(
                                    text = "Net Score: ${if (isPositive) "+" else ""}${currentOption.netScore}",
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        if (currentOption.keyTakeaway.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentOption.keyTakeaway,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // PROS SECTION
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = ProGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pros & Upsides (${currentOption.pros.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProGreen
                    )
                }
            }

            items(currentOption.pros) { pro ->
                ProConCard(item = pro, isPro = true)
            }

            // CONS SECTION
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircle,
                        contentDescription = null,
                        tint = ConRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cons & Risks (${currentOption.cons.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ConRed
                    )
                }
            }

            items(currentOption.cons) { con ->
                ProConCard(item = con, isPro = false)
            }
        }
    }
}

@Composable
fun ProConCard(
    item: ProConItem,
    isPro: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Impact badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPro) Color(0xFF064E3B) else Color(0xFF450A0A)
                ) {
                    Text(
                        text = "${if (isPro) "+" else "-"}${item.weight}",
                        color = if (isPro) ProGreen else ConRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Category pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceVariantDark
                ) {
                    Text(
                        text = item.category,
                        color = SecondaryCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                if (item.explanation.isNotBlank()) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded && item.explanation.isNotBlank()) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
