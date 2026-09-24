package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DecisionOption
import com.example.ui.theme.PrimaryGold
import com.example.ui.theme.ProGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark

@Composable
fun ResolutionDialog(
    options: List<DecisionOption>,
    currentSelectedIndex: Int,
    initialNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (chosenIndex: Int, notes: String) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(if (currentSelectedIndex >= 0) currentSelectedIndex else 0) }
    var notesText by remember { mutableStateOf(initialNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("resolution_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lock In Your Decision",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_resolution")) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select which option you are committing to and note your rationale:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Options list
                options.forEachIndexed { index, option ->
                    val isSelected = selectedIndex == index
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedIndex = index }
                            .testTag("option_select_$index"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF064E3B) else SurfaceVariantDark,
                        border = if (isSelected) BorderStroke(1.5.dp, ProGreen) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedIndex = index },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ProGreen,
                                    unselectedColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.name,
                                color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Reflection Notes / First Next Step", color = Color.Gray) },
                    placeholder = { Text("e.g. Taking this step because long-term upside outweighs short-term pain. First action: email partner.", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("reflection_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = Color(0xFF475569)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onConfirm(selectedIndex, notesText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_resolution_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ProGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark As Decided",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
