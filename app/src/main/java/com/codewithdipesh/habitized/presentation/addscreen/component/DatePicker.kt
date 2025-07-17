package com.codewithdipesh.habitized.presentation.addscreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codewithdipesh.habitized.ui.theme.regular
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDatePicker(
    date: LocalDate?, onSelect: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(date ?: LocalDate.now().plusMonths(1)) }
    var datePickerStateHolder: DatePickerState? = null

    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = date?.format(
                DateTimeFormatter.ofPattern("dd MMM yy")
            ) ?: "select date", style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary,
                fontFamily = regular,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ), modifier = Modifier.clickable { showDatePicker = true })
    }

    if (showDatePicker) {
        val initialMillis =
            selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
            TextButton(onClick = {
                // Take final selection from state
                val millis = datePickerStateHolder?.selectedDateMillis ?: initialMillis
                val finalDate =
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                selectedDate = finalDate
                onSelect(finalDate)
                showDatePicker = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
        }) {
            key(selectedDate.year to selectedDate.monthValue) {

                // Build a new DatePickerState when month/year changes
                val datePickerState =
                    rememberDatePickerState(initialSelectedDateMillis = initialMillis)
                // expose to confirm button
                datePickerStateHolder = datePickerState

                Column {
                    MonthHeaderDatePicker(
                        selectedDate = selectedDate, onMonthSelected = { newMonthDate ->
                            // update to 1st-of-month to drive re-key
                            selectedDate = newMonthDate
                        }, datePickerState = datePickerState
                    )
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthHeaderDatePicker(
    selectedDate: LocalDate, onMonthSelected: (LocalDate) -> Unit, datePickerState: DatePickerState
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val months = Month.values().toList()
    val currentYear = selectedDate.year

    Box {
        Text(
            text = "Select Month",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clickable { expanded = true },
            textAlign = TextAlign.Center,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            months.forEachIndexed { index, monthEnum ->
                val monthLabel = monthEnum.name.lowercase().replaceFirstChar { it.titlecase() }
                DropdownMenuItem(text = { Text(monthLabel) }, onClick = {
                    expanded = false

                    val targetDate = LocalDate.of(currentYear, index + 1, 1)
                    val millis =
                        targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    coroutineScope.launch {
                        datePickerState.selectedDateMillis = millis
                    }

                    onMonthSelected(targetDate.plusDays(1))
                })
            }
        }
    }
}
