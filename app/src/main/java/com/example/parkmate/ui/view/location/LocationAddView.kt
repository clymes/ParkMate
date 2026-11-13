package com.example.parkmate.ui.view.location

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.CheckInfoModal
import com.example.parkmate.ui.component.MapLibre
import com.example.parkmate.ui.component.SearchTextInput
import com.example.parkmate.ui.component.getMapState
import com.example.parkmate.utils.getAddressFromCoords
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationAddView(
    onBackButtonClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = remember { LocationViewModel() }
    val scope = rememberCoroutineScope()

    val mapState = getMapState()
    var searchQuery by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }
    var geocodedAddress by remember { mutableStateOf("") }

    var showCheckInfoModal by remember { mutableStateOf(false) }

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
                SearchTextInput(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholderText = "Cerca indirizzo o luogo...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .align(Alignment.TopCenter)
                )
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
                        onClick = { showCheckInfoModal = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annulla")
                    }
                    Button(
                        onClick = {
                            showCheckInfoModal = false
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
    )
}
