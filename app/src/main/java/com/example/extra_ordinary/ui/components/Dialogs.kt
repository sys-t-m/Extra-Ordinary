package com.example.extra_ordinary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.extra_ordinary.ui.theme.SoftBlue
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DefaultRateDialog(
    initialEuros: Int? = null,
    initialCents: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var euros by remember { mutableStateOf(initialEuros?.toString() ?: "") }
    var cents by remember { mutableStateOf(initialCents?.toString() ?: "") }

    val eurosInt = euros.toIntOrNull()
    val centsInt = cents.toIntOrNull()

    val isEurosError = euros.isNotEmpty() && eurosInt == null
    val isCentsError = cents.isNotEmpty() && (centsInt == null || centsInt !in 0..99)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Imposta tariffa oraria",
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = euros,
                    onValueChange = { euros = it },
                    label = { Text("Euro") },
                    isError = isEurosError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = cents,
                    onValueChange = { if (it.length <= 2) cents = it },
                    label = { Text("Centesimi") },
                    isError = isCentsError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(eurosInt ?: 0, centsInt ?: 0)
                },
                enabled = !isEurosError && !isCentsError
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimeAndCurrencyInputDialog(
    initialHours: Int? = null,
    initialMinutes: Int? = null,
    initialEuros: Int? = null,
    initialCents: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int, Int) -> Unit
) {
    var hours by remember { mutableStateOf(initialHours ?: 0) }
    var minutes by remember { mutableStateOf(initialMinutes ?: 0) }
    var euros by remember { mutableStateOf(initialEuros?.toString() ?: "") }
    var cents by remember { mutableStateOf(initialCents?.toString() ?: "") }

    val eurosInt = euros.toIntOrNull()
    val centsInt = cents.toIntOrNull()

    val isEurosError = euros.isNotEmpty() && eurosInt == null
    val isCentsError = cents.isNotEmpty() && (centsInt == null || centsInt !in 0..99)

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hours input
                Text("Ore")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = { if (hours > 0) hours-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrementa Ore")
                    }
                    Text(text = hours.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (hours < 8) hours++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumenta Ore")
                    }
                }

                // Minutes input
                Text("Minuti")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = { minutes = (minutes - 15 + 60) % 60 }) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrementa Minuti")
                    }
                    Text(text = minutes.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { minutes = (minutes + 15) % 60 }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumenta Minuti")
                    }
                }

                OutlinedTextField(
                    value = euros,
                    onValueChange = { euros = it },
                    label = { Text("Euro") },
                    isError = isEurosError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = cents,
                    onValueChange = { if (it.length <= 2) cents = it },
                    label = { Text("Centesimi") },
                    isError = isCentsError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(hours, minutes, eurosInt ?: 0, centsInt ?: 0)
                },
                enabled = !isEurosError && !isCentsError
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MonthYearPickerDialog(
    initialMonth: Int,
    initialYear: Int,
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    val months = remember { Month.values().map { it.getDisplayName(TextStyle.SHORT, Locale.ITALIAN) } }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
                    }
                    Text(text = selectedYear.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Year")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    itemsIndexed(months) { index, month ->
                        if (month.isNotEmpty()) {
                            val isSelected = (index + 1) == selectedMonth
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { selectedMonth = index + 1 }
                                    .background(
                                        color = if (isSelected) SoftBlue else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = month.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(selectedMonth, selectedYear) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
