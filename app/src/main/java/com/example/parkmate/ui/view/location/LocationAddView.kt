package com.example.parkmate.ui.view.location

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.PhotonFeature
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.CheckInfoModal
import com.example.parkmate.ui.component.MapLibre
import com.example.parkmate.ui.component.SearchTextInput
import com.example.parkmate.ui.component.getMapState
import com.example.parkmate.utils.getAddressFromCoords
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationAddView(
    onBackButtonClick: () -> Unit,
    goToLocationListView: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val viewModel = remember { LocationViewModel() }
    val scope = rememberCoroutineScope()

    var isTrackingLocation by remember { mutableStateOf(true) }
    val mapState = getMapState(isTracking = isTrackingLocation)
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PhotonFeature>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isSelectingSuggestion by remember { mutableStateOf(false) }

    var locationName by remember { mutableStateOf("") }
    var geocodedAddress by remember { mutableStateOf("") }

    var showCheckInfoModal by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (isSelectingSuggestion) {
            isSelectingSuggestion = false
            return@LaunchedEffect
        }

        val trimmedQuery = searchQuery.trim()
        if (trimmedQuery.length < 3) {
            searchResults = emptyList()
            isDropdownExpanded = false
            return@LaunchedEffect
        }

        delay(400.milliseconds)

        val results = viewModel.searchPhotonLocations(trimmedQuery)
        searchResults = results
        isDropdownExpanded = results.isNotEmpty()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Nuova posizione") },
                navigationIcon = {
                    BackButton(onBackButtonClick = onBackButtonClick)
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                MapLibre(
                    modifier = Modifier.fillMaxSize(),
                    mapState = mapState,
                    showCenterPin = true
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SearchTextInput(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholderText = "Cerca indirizzo o luogo...",
                        onClearClick = {
                            searchQuery = ""
                            searchResults = emptyList()
                            isDropdownExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isDropdownExpanded && searchResults.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp),
                            shape = MaterialTheme.shapes.medium,
                            shadowElevation = 6.dp,
                            tonalElevation = 2.dp
                        ) {
                            Column {
                                searchResults.forEachIndexed { index, feature ->
                                    val displayString = feature.toDisplayString()
                                    if (displayString.isNotBlank()) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = displayString,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            },
                                            onClick = {
                                                isSelectingSuggestion = true
                                                searchQuery = displayString
                                                isDropdownExpanded = false
                                                searchResults = emptyList()

                                                focusManager.clearFocus()

                                                val lat = feature.lat
                                                val long = feature.long
                                                if (lat != null && long != null) {
                                                    isTrackingLocation = false // Disable tracking to allow panning to selected location
                                                    scope.launch {
                                                        mapState.animateCamera(
                                                            CameraUpdate(
                                                                center = Position(
                                                                    longitude = long,
                                                                    latitude = lat
                                                                ),
                                                                zoom = 15.0
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                        if (index < searchResults.lastIndex) {
                                            HorizontalDivider(thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Nome posizione") },
                placeholder = { Text("Es. Casa, Lavoro, Parcheggio...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    geocodedAddress = "Caricamento in corso..."

                    val lat = mapState.cameraPosition.center.latitude
                    val long = mapState.cameraPosition.center.longitude

                    scope.launch {
                        val addressString = getAddressFromCoords(context, lat, long)
                        geocodedAddress = addressString
                        Log.d("LocationAddView", "Nome: $locationName | Lat: $lat, Long: $long | Indirizzo: $addressString")
                    }

                    showCheckInfoModal = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Aggiungi Posizione")
            }
        }

        CheckInfoModal(
            isVisible = showCheckInfoModal,
            onDismiss = { showCheckInfoModal = false }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Confermi?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Controlla le tue informazioni",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Nome Posizione",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = locationName.ifBlank { "Nessun nome specificato" },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Indirizzo / Coordinate",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = geocodedAddress,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showCheckInfoModal = false
                            onBackButtonClick()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annulla")
                    }
                    Button(
                        onClick = {
                            viewModel.addLocation(
                                context = context,
                                name = locationName.ifBlank { "Posizione senza nome" },
                                lat = mapState.cameraPosition.center.latitude.toFloat(),
                                long = mapState.cameraPosition.center.longitude.toFloat(),
                                address = geocodedAddress
                            )
                            showCheckInfoModal = false
                            goToLocationListView()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Conferma")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LocationAddViewPreview() {
    LocationAddView(
        onBackButtonClick = {},
        goToLocationListView = {}
    )
}
