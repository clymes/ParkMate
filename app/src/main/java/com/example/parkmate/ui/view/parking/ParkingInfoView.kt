package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Parking
import com.example.parkmate.ui.component.BackButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class PickerType {
    NONE, START_DATE, START_TIME, END_DATE, END_TIME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingInfoView(
    parkingType: String = Parking.FREE.displayName,
    onBackButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
) {
    val parking = Parking.fromString(parkingType)
    var hourlyRate by remember { mutableStateOf("") }

    // Pre-compilato con data e ora correnti
    val now = Calendar.getInstance()
    var selectedStartDate by remember { mutableStateOf<Long?>(now.timeInMillis) }
    var selectedStartTime by remember { mutableStateOf<Pair<Int, Int>?>(Pair(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))) }

    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    var activePicker by remember { mutableStateOf(PickerType.NONE) }

    val startDateTimeText = formatDateTime(selectedStartDate, selectedStartTime)
    val endDateTimeText = formatDateTime(selectedEndDate, selectedEndTime)

    val isStartDateValid = selectedStartDate != null && selectedStartTime != null
    val isEndDateValid = !parking.requiresEndTime || (selectedEndDate != null && selectedEndTime != null)
    val isHourlyRateValid = !parking.hasHourlyRate || hourlyRate.isNotBlank()

    val isFormValid = isStartDateValid && isEndDateValid && isHourlyRateValid

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Informazioni parcheggio") },
                navigationIcon = {
                    BackButton(onBackButtonClick = onBackButtonClick)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Data e Ora Inizio obbligatorio per tutte le tipologie
                OutlinedTextField(
                    value = startDateTimeText,
                    onValueChange = {},
                    label = { Text("Data e Ora Inizio *") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activePicker = PickerType.START_DATE },
                    trailingIcon = {
                        IconButton(onClick = { activePicker = PickerType.START_DATE }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data e Ora Inizio")
                        }
                    }
                )

                if (parking.hasHourlyRate) {
                    OutlinedTextField(
                        value = hourlyRate,
                        onValueChange = { hourlyRate = it },
                        label = { Text("Tariffa oraria (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Scadenza (Data e Ora): Obbligatorio per FIXED, opzionale per FREE e HOURLY
                val isEndTimeRequired = parking.requiresEndTime
                val endTimeLabel = if (isEndTimeRequired) "Scadenza (Data e Ora) *" else "Scadenza (Data e Ora) (opzionale)"

                OutlinedTextField(
                    value = endDateTimeText,
                    onValueChange = {},
                    label = { Text(endTimeLabel) },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activePicker = PickerType.END_DATE },
                    trailingIcon = {
                        IconButton(onClick = { activePicker = PickerType.END_DATE }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleziona Data e Ora Scadenza")
                        }
                    }
                )
            }

            Button(
                onClick = onNextButtonClick,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Avanti")
            }
        }
    }

    when (activePicker) {
        PickerType.START_DATE, PickerType.END_DATE -> {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { activePicker = PickerType.NONE },
                confirmButton = {
                    TextButton(onClick = {
                        if (activePicker == PickerType.START_DATE) {
                            selectedStartDate = datePickerState.selectedDateMillis
                            activePicker = PickerType.START_TIME
                        } else {
                            selectedEndDate = datePickerState.selectedDateMillis
                            activePicker = PickerType.END_TIME
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activePicker = PickerType.NONE }) {
                        Text("Annulla")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        PickerType.START_TIME, PickerType.END_TIME -> {
            val timePickerState = rememberTimePickerState()
            AlertDialog(
                onDismissRequest = { activePicker = PickerType.NONE },
                confirmButton = {
                    TextButton(onClick = {
                        if (activePicker == PickerType.START_TIME) {
                            selectedStartTime = Pair(timePickerState.hour, timePickerState.minute)
                        } else {
                            selectedEndTime = Pair(timePickerState.hour, timePickerState.minute)
                        }
                        activePicker = PickerType.NONE
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activePicker = PickerType.NONE }) {
                        Text("Annulla")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
        PickerType.NONE -> {}
    }
}

private fun formatDateTime(dateMillis: Long?, time: Pair<Int, Int>?): String {
    if (dateMillis == null || time == null) return ""
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateString = sdf.format(Date(dateMillis))
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", time.first, time.second)
    return "$dateString $timeString"
}

@Preview(showBackground = true)
@Composable
fun ParkingInfoViewPreview() {
    ParkingInfoView(
        onBackButtonClick = {},
        onNextButtonClick = {},
    )
}


