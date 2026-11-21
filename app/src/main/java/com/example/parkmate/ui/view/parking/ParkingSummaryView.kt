package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.component.BackButton
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.parkmate.ui.component.ExpandableImage
import com.example.parkmate.ui.component.SliderCarousel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingSummaryView(
    onBackButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit = {},
    viewModel: ParkingViewModel,
) {
    val context = LocalContext.current
    val vehicle by viewModel.selectedVehicle.collectAsState()
    val location by viewModel.selectedLocation.collectAsState()
    val parkingType by viewModel.selectedParkingType.collectAsState()

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    val startDateStr: String
    if (viewModel.startTimestamp > 0L) {
        startDateStr = dateFormat.format(Date(viewModel.startTimestamp))
    } else {
        startDateStr = "N/A"
    }

    val endDateStr: String
    if (viewModel.endTimestamp != null) {
        endDateStr = dateFormat.format(Date(viewModel.endTimestamp!!))
    } else {
        endDateStr = "nessuno"
    }
    
    val rateStr: String
    if (viewModel.hourlyRate != null) {
        rateStr = "€ ${viewModel.hourlyRate}/h"
    } else {
        rateStr = "nessuno"
    }
    
    val notesStr: String
    if (viewModel.notes.isNotBlank()) {
        notesStr = viewModel.notes
    } else {
        notesStr = "nessuno"
    }
    
    val vehicleStr: String
    val currentVehicle = vehicle
    if (currentVehicle != null) {
        vehicleStr = "${currentVehicle.name} - ${currentVehicle.plate}"
    } else {
        vehicleStr = "N/A"
    }
    
    val addressStr: String
    val currentLocation = location
    if (currentLocation != null) {
        addressStr = currentLocation.address
    } else {
        addressStr = "N/A"
    }
    
    val parkingTypeStr: String
    val currentParkingType = parkingType
    if (currentParkingType != null) {
        parkingTypeStr = currentParkingType.displayName
    } else {
        parkingTypeStr = "N/A"
    }

    var selectedImageBitmaps by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }

    LaunchedEffect(viewModel.imageUris) {
        val bitmaps = mutableListOf<ImageBitmap>()
        for (uriString in viewModel.imageUris) {
            try {
                val uri = Uri.parse(uriString)
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                bitmaps.add(bitmap.asImageBitmap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        selectedImageBitmaps = bitmaps
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Riepilogo Parcheggio") },
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryItem(label = "Tipo Parcheggio", value = parkingTypeStr)
                SummaryItem(label = "Veicolo", value = vehicleStr)
                SummaryItem(label = "Indirizzo", value = addressStr)
                SummaryItem(label = "Data inizio", value = startDateStr)
                SummaryItem(label = "Data fine", value = endDateStr)
                SummaryItem(label = "Tariffa oraria", value = rateStr)
                SummaryItem(label = "Note", value = notesStr)

                if (selectedImageBitmaps.isNotEmpty()) {
                    Text(
                        text = "Foto",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    SliderCarousel(
                        items = selectedImageBitmaps,
                        itemQuantity = 5,
                        showAddButton = false,
                        modifier = Modifier.fillMaxWidth()
                    ) { bitmap ->
                        ExpandableImage(
                            bitmap = bitmap,
                            contentDescription = "Foto salvata",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.saveParking(context) {
                        onNextButtonClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Parcheggia")
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingSummaryViewPreview() {
    ParkingSummaryView(
        onBackButtonClick = {},
        viewModel = ParkingViewModel()
    )
}
