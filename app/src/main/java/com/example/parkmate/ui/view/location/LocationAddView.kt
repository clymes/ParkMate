package com.example.parkmate.ui.view.location

import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.component.BackButton
import com.example.parkmate.ui.component.MapLibre
import com.example.parkmate.ui.component.SearchTextInput
import com.example.parkmate.ui.component.getMapState
import java.util.Locale
import kotlinx.coroutines.Dispatchers
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
            Button(
                onClick = {
                    val lat = mapState.cameraPosition.center.latitude
                    val long = mapState.cameraPosition.center.longitude

                    // TODO: separalrlo in una classe/funzione apposita
                    scope.launch(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                geocoder.getFromLocation(lat, long, 1) { addresses ->
                                    val addressString = addresses.firstOrNull()?.getAddressLine(0) ?: "Indirizzo non trovato"
                                    Log.d("LocationAddView", "Lat: $lat, Long: $long | Indirizzo: $addressString")
                                }
                            } else {
                                @Suppress("DEPRECATION")
                                val addresses = geocoder.getFromLocation(lat, long, 1)
                                val addressString = addresses?.firstOrNull()?.getAddressLine(0) ?: "Indirizzo non trovato"
                                Log.d("LocationAddView", "Lat: $lat, Long: $long | Indirizzo: $addressString")
                            }
                        } catch (e: Exception) {
                            Log.e("LocationAddView", "Errore Geocoder: ${e.message}", e)
                            Log.d("LocationAddView", "Lat: $lat, Long: $long | Indirizzo: non disponibile")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                Text("Aggiungi Posizione")
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
