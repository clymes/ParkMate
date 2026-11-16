package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Location
import com.example.parkmate.ui.component.LocationItem
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingLocationView(
    onBackButtonClick: () -> Unit,
    onLocationSelected: (Location) -> Unit = {},
    onAddLocationClick: () -> Unit = {},
    viewModel: ParkingViewModel = remember { ParkingViewModel() },
) {
    val context = LocalContext.current
    val locationList by viewModel.getAllLocations(context).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Seleziona Posizione") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (locationList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nessuna posizione salvata.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = locationList,
                        key = { (id) -> id },
                    ) { location ->
                        LocationItem(
                            location = location,
                            onLocationClick = {
                                viewModel.selectLocation(location)
                                onLocationSelected(location)
                            },
                        )
                    }
                }
            }

            androidx.compose.material3.Button(
                onClick = onAddLocationClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Aggiungi Posizione")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingLocationViewPreview() {
    ParkingLocationView(
        onBackButtonClick = {},
    )
}
