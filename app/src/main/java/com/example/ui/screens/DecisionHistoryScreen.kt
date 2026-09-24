package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.json.JsonHelper
import com.example.data.model.DecisionEntity
import com.example.ui.DecisionUiState
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DecisionHistoryScreen(
    uiState: DecisionUiState,
    onSelectDecision: (DecisionEntity) -> Unit,
    onDeleteDecision: (Long) -> Unit,
    onToggleFavorite: (DecisionEntity) -> Unit,
    onSearchChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onToggleFavoritesOnly: () -> Unit,
    onStartNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    val categories = listOf("All", "Career", "Finance", "Lifestyle", "Tech", "Personal")

    val filteredList = remember(
        uiState.savedDecisions,
        uiState.searchQuery,
        uiState.selectedCategoryFilter,
        uiState.filterFavoritesOnly
    ) {
        uiState.savedDecisions.filter { decision ->
            val matchesSearch = uiState.searchQuery.isBlank() ||
                    decision.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    decision.context.contains(uiState.searchQuery, ignoreCase = true)

            val matchesCategory = uiState.selectedCategoryFilter == "All" ||
                    decision.category.equals(uiState.selectedCategoryFilter, ignoreCase = true)

            val matchesFav = !uiState.filterFavoritesOnly || decision.isFavorite

            matchesSearch && matchesCategory && matchesFav
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("decision_history_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Decision Vault",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${uiState.savedDecisions.size} decisions recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onToggleFavoritesOnly,
                modifier = Modifier.testTag("filter_favs_button")
            ) {
                Icon(
                    imageVector = if (uiState.filterFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Filter Favorites",
                    tint = if (uiState.filterFavoritesOnly) ConRed else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search past decisions...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryGold,
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = uiState.selectedCategoryFilter == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChanged(cat) },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGold,
                        selectedLabelColor = Color.Black,
                        containerColor = SurfaceDark,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = if (uiState.savedDecisions.isEmpty()) "No decisions analyzed yet" else "No matching decisions found",
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Input a new dilemma to get strategic clarity.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Button(
                        onClick = onStartNew,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start First Decision", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val options = remember(item.optionsJson) {
                        JsonHelper.optionsFromJson(item.optionsJson)
                    }
                    val analysis = remember(item.analysisJson) {
                        JsonHelper.fullAnalysisFromJson(item.analysisJson)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDecision(item) }
                            .testTag("decision_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SurfaceVariantDark
                                ) {
                                    Text(
                                        text = item.category.uppercase(),
                                        color = SecondaryCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.isResolved) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF064E3B),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = "RESOLVED",
                                                color = ProGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onToggleFavorite(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (item.isFavorite) ConRed else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteDecision(item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (analysis != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = PrimaryGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Verdict: ${analysis.verdict.recommendedOption}",
                                        color = PrimaryGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${options.size} options evaluated",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = dateFormat.format(Date(item.createdAt)),
                                    color = Color.DarkGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
