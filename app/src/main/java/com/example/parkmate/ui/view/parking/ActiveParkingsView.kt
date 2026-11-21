package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.ItemCarousel
import com.example.parkmate.ui.view.home.HomeViewModel
import android.graphics.ImageDecoder
import androidx.core.net.toUri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
fun ActiveParkingsView(
    onBackButtonClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val activeParkings by viewModel.getActiveParkings(context).collectAsState(initial = emptyList())
    val vehicles by viewModel.getAllVehicles(context).collectAsState(initial = emptyList())
    val locations by viewModel.getAllLocations(context).collectAsState(initial = emptyList())

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Parcheggi Attivi") },
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
                .padding(16.dp)
        ) {
            if (activeParkings.isEmpty()) {
                Text(
                    text = "Nessun parcheggio attivo in questo momento.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                ItemCarousel(
                    items = activeParkings,
                    modifier = Modifier.fillMaxSize()
                ) { parking ->
                    val vehicle = vehicles.find { it.id == parking.vehicleId }
                    val location = locations.find { it.id == parking.locationId }

                    val parkingTypeStr = parking.parkingType.ifBlank { "nessuna" }

                    val vehicleStr = if (vehicle != null) {
                        val name = vehicle.name.trim()
                        val plate = vehicle.plate.trim()
                        when {
                            name.isNotEmpty() && plate.isNotEmpty() -> "$name - $plate"
                            name.isNotEmpty() -> name
                            plate.isNotEmpty() -> plate
                            else -> "nessuna"
                        }
                    } else {
                        "nessuna"
                    }

                    val addressStr = if (location != null && location.address.isNotBlank()) {
                        location.address
                    } else {
                        "nessuna"
                    }

                    val startDateStr = if (parking.startTimestamp > 0L) {
                        dateFormat.format(Date(parking.startTimestamp))
                    } else {
                        "nessuna"
                    }

                    val endDateStr = if (parking.endTimestamp != null && parking.endTimestamp > 0L) {
                        dateFormat.format(Date(parking.endTimestamp))
                    } else {
                        "nessuna"
                    }

                    val rateStr = if (parking.hourlyRate != null) {
                        "€ ${parking.hourlyRate}/h"
                    } else {
                        "nessuna"
                    }

                    val notesStr = if (parking.notes.isNotBlank()) {
                        parking.notes
                    } else {
                        "nessuna"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
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
                                
                                ParkingImages(imageUriString = parking.imageUri)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.stopParking(context, parking)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Termina")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingImages(imageUriString: String?) {
    if (imageUriString.isNullOrBlank()) {
        SummaryItem(label = "Foto", value = "Nessuna")
        return
    }

    val context = LocalContext.current
    var selectedImageBitmaps by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }

    LaunchedEffect(imageUriString) {
        val bitmaps = mutableListOf<ImageBitmap>()
        val uris = imageUriString.split(",")
        for (uriString in uris) {
            if (uriString.isNotBlank()) {
                try {
                    val uri = uriString.trim().toUri()
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    bitmaps.add(bitmap.asImageBitmap())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        selectedImageBitmaps = bitmaps
    }

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
    } else {
        SummaryItem(label = "Foto", value = "Nessuna")
    }
}
