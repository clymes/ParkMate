package com.example.parkmate.ui.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.parkmate.ui.component.MapLibre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    goToVehicleListView: () -> Unit,
    goToLocationListView: () -> Unit,
    goToParkingLocationView: () -> Unit,
    goToActiveParkingsView: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val context = LocalContext.current
    val activeParkingList by viewModel.getActiveParkings(context).collectAsState(initial = emptyList())

    val locations by viewModel.getAllLocations(context).collectAsState(initial = emptyList())
    val activeParkingLocations = locations.filter { location -> activeParkingList.any { it.locationId == location.id } }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // MENU LATERALE
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.4f),
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi menu"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "VEICOLI") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            goToVehicleListView()
                        },
                        shape = RectangleShape
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "POSIZIONI") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            goToLocationListView()
                        },
                        shape = RectangleShape
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "INFORMAZIONI") },
                        selected = false,
                        onClick = { /*TODO*/ },
                        shape = RectangleShape
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "CRONOLOGIA") },
                        selected = false,
                        onClick = { /*TODO*/ },
                        shape = RectangleShape
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "STATISTICHE") },
                        selected = false,
                        onClick = { /*TODO*/ },
                        shape = RectangleShape
                    )
                }
            }
        }
    ) {
        // CONTENUTO PRINCIPALE
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .blur(if (drawerState.isOpen) 8.dp else 0.dp),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("ParkMate") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Apri menu"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp),
            ) {
                MapLibre(
                    modifier = Modifier
                        .weight(1f),
                    mapState = com.example.parkmate.ui.component.getMapState(activeParkings = activeParkingLocations)
                )

                if (activeParkingList.isNotEmpty()) {
                    val count = activeParkingList.size
                    val text = if (count == 1) "1 parcheggio" else "$count parcheggi"
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = goToActiveParkingsView,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = text)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = goToParkingLocationView,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Parcheggia")
                }
            }
        }
    }
}

@Composable
@Preview
fun HomeViewPreview() {
    HomeView(
        goToVehicleListView = {},
        goToLocationListView = {},
        goToParkingLocationView = {},
        goToActiveParkingsView = {}
    )
}