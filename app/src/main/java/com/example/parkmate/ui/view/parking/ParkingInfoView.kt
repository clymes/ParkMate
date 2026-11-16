package com.example.parkmate.ui.view.parking

import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Parking
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.CheckInfoModal
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
    var note by remember { mutableStateOf("") }

    // Pre-compilato con data e ora correnti
    val now = Calendar.getInstance()
    var selectedStartDate by remember { mutableStateOf<Long?>(now.timeInMillis) }
    var selectedStartTime by remember { mutableStateOf<Pair<Int, Int>?>(Pair(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))) }

    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // galleria foto
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    var activePicker by remember { mutableStateOf(PickerType.NONE) }
    var showPhotoModal by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            try {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                selectedImageBitmap = bitmap.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
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
                var endTimeLabel = "Scadenza (Data e Ora) (opzionale)"
                if (isEndTimeRequired) {
                    endTimeLabel = "Scadenza (Data e Ora) *"
                }

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

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (opzionale)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                val dashColor = MaterialTheme.colorScheme.outline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showPhotoModal = true }
                        .drawBehind {
                            if (selectedImageBitmap == null) {
                                drawRoundRect(
                                    color = dashColor,
                                    style = Stroke(
                                        width = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                                    ),
                                    cornerRadius = CornerRadius(12.dp.toPx())
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageBitmap != null) {
                        Image(
                            bitmap = selectedImageBitmap!!,
                            contentDescription = "Foto selezionata",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Aggiungi foto",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aggiungi foto",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
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

    CheckInfoModal(
        isVisible = showPhotoModal,
        onDismiss = { showPhotoModal = false }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Aggiungi foto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = { showPhotoModal = false },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Fotocamera"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Fotocamera")
                }
            }

            OutlinedButton(
                onClick = {
                    showPhotoModal = false
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Galleria"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Galleria")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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


