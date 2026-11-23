package com.example.parkmate.ui.view.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Location
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.MapLibre
import com.example.parkmate.ui.component.SearchTextInput
import com.example.parkmate.utils.PhotonFeature
import com.example.parkmate.utils.getAddressFromCoords
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailView(
    locationId: Int,
    onBackButtonClick: () -> Unit,
    viewModel: LocationViewModel = remember { LocationViewModel() }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val location by viewModel.getLocationById(context, locationId).collectAsState(initial = null)
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showDeleteDialog && location != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Sei sicuro?") },
            text = { Text(text = "Sei sicuro di voler eliminare la posizione ${location!!.name}? Quest'azione è irreversibile") },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.softdeleteLocation(context, locationId)
                        showDeleteDialog = false
                        onBackButtonClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Elimina")
                }
            }
        )
    }

    if (showEditBottomSheet && location != null) {
        var editName by remember(location) { mutableStateOf(location!!.name) }
        var searchQuery by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<PhotonFeature>>(emptyList()) }
        var isDropdownExpanded by remember { mutableStateOf(false) }
        var isSelectingSuggestion by remember { mutableStateOf(false) }
        var isSaving by remember { mutableStateOf(false) }

        val focusManager = LocalFocusManager.current
        val editMapState = rememberMapState(
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/bright"),
            initialCameraPosition = CameraPosition(
                center = Position(location!!.long.toDouble(), location!!.lat.toDouble()),
                zoom = 15.0
            )
        )

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

        ModalBottomSheet(
            onDismissRequest = { showEditBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Modifica Posizione",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nome posizione") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    MapLibre(
                        modifier = Modifier.fillMaxSize(),
                        mapState = editMapState,
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
                                    .heightIn(max = 200.dp),
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
                                                        scope.launch {
                                                            editMapState.animateCamera(
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

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (isSaving) return@Button
                        isSaving = true
                        val newLat = editMapState.cameraPosition.center.latitude
                        val newLong = editMapState.cameraPosition.center.longitude

                        scope.launch {
                            val newAddress = getAddressFromCoords(context, newLat, newLong)
                            viewModel.updateLocation(
                                context = context,
                                id = location!!.id,
                                name = editName.ifBlank { "Posizione senza nome" },
                                lat = newLat.toFloat(),
                                long = newLong.toFloat(),
                                address = newAddress
                            )
                            sheetState.hide()
                            showEditBottomSheet = false
                            isSaving = false
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSaving) "Salvataggio..." else "Modifica")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dettagli") },
                navigationIcon = {
                    BackButton(onBackButtonClick = onBackButtonClick)
                },
                actions = {
                    if (location != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Elimina posizione",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (location != null) {
                Text(
                    text = location!!.name.ifBlank { "Nessun nome" },
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MapLibre(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mapState = getStaticMapState(location!!),
                    interactions = org.maplibre.compose.interaction.MapInteractions.None,
                    showCenterPin = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showEditBottomSheet = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Modifica")
                }
            } else {
                Text("Caricamento...")
            }
        }
    }
}

@Composable
private fun getStaticMapState(location: Location): MapState {
    val mapState = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/bright"),
        initialCameraPosition = CameraPosition(
            center = Position(location.long.toDouble(), location.lat.toDouble()),
            zoom = 15.0
        )
    )

    LaunchedEffect(location.lat, location.long) {
        mapState.animateCamera(
            CameraUpdate(
                center = Position(location.long.toDouble(), location.lat.toDouble()),
                zoom = 15.0
            )
        )
    }

    return mapState
}
