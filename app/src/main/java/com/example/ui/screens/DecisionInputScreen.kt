package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DecisionTemplates
import com.example.data.model.DecisionTemplate
import com.example.ui.DecisionUiState
import com.example.ui.theme.*

@Composable
fun DecisionInputScreen(
    uiState: DecisionUiState,
    onSubmit: (title: String, options: List<String>, context: String, category: String) -> Unit,
    onSelectTemplate: (DecisionTemplate) -> Unit,
    onViewTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Career") }
    var contextText by remember { mutableStateOf("") }
    var showContextInput by remember { mutableStateOf(false) }

    val options = remember { mutableStateListOf("Option A", "Option B") }

    val categories = listOf("Career", "Finance", "Lifestyle", "Tech", "Personal")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("decision_input_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header with Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_decision),
                    contentDescription = "The Tiebreaker",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    BackgroundDark.copy(alpha = 0.7f),
                                    BackgroundDark
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF451A03),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Balance,
                                contentDescription = null,
                                tint = PrimaryGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DECISION INTELLIGENCE",
                                color = PrimaryGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Text(
                        text = "The Tiebreaker",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Weigh pros & cons, build comparison tables, and eliminate deadlock.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        // Quick Starters Carousel
        item {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Starters",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    TextButton(onClick = onViewTemplates) {
                        Text("See All", color = SecondaryCyan, fontSize = 12.sp)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(DecisionTemplates.allTemplates.take(4)) { template ->
                        Surface(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable {
                                    title = template.title
                                    selectedCategory = template.category
                                    contextText = template.defaultContext
                                    showContextInput = true
                                    options.clear()
                                    options.addAll(template.defaultOptions)
                                }
                                .testTag("template_chip_${template.category}"),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineDark)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = template.category.uppercase(),
                                    color = PrimaryGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = template.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = template.description,
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Decision Input Form
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGold,
                                selectedLabelColor = Color.Black,
                                containerColor = SurfaceDark,
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = OutlineDark,
                                selectedBorderColor = PrimaryGold
                            )
                        )
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What decision do you need to make?", color = Color.Gray) },
                    placeholder = { Text("e.g. Accept new job offer or ask for a promotion?", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decision_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark
                    )
                )

                // Options Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Options Under Consideration (${options.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        if (options.size < 5) {
                            TextButton(
                                onClick = {
                                    val nextLetter = ('A'.code + options.size).toChar()
                                    options.add("Option $nextLetter")
                                },
                                modifier = Modifier.testTag("add_option_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = SecondaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Add Option", color = SecondaryCyan, fontSize = 12.sp)
                            }
                        }
                    }

                    options.forEachIndexed { index, optionName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (index == 0) PrimaryGold else SecondaryCyan,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${('A'.code + index).toChar()}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = optionName,
                                onValueChange = { options[index] = it },
                                placeholder = { Text("Option name", color = Color.Gray) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("option_input_$index"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = PrimaryGold,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedContainerColor = SurfaceDark,
                                    unfocusedContainerColor = SurfaceDark
                                )
                            )

                            if (options.size > 2) {
                                IconButton(
                                    onClick = { options.removeAt(index) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove Option",
                                        tint = ConRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Optional Context Toggle & Accordion
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showContextInput = !showContextInput },
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = PrimaryGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Personal Priorities & Constraints (Optional)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = if (showContextInput) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = showContextInput) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                OutlinedTextField(
                                    value = contextText,
                                    onValueChange = { contextText = it },
                                    placeholder = {
                                        Text(
                                            "e.g. Budget is tight this year; I value peace of mind over raw money; must decide within 48 hours.",
                                            color = Color.DarkGray,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .testTag("decision_context_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = PrimaryGold,
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = SurfaceVariantDark,
                                        unfocusedContainerColor = SurfaceVariantDark
                                    )
                                )
                            }
                        }
                    }
                }

                // Error message
                if (uiState.errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF450A0A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ConRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.errorMessage,
                                color = Color(0xFFFECACA),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Generating UX
                if (uiState.isGenerating) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryGold,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = uiState.generationProgressText.ifBlank { "Analyzing dilemma with Gemini AI..." },
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Evaluating trade-offs across all dimensions",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    // Primary Action Button
                    Button(
                        onClick = {
                            onSubmit(title, options.toList(), contextText, selectedCategory)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_decision_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Break The Tie with AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
