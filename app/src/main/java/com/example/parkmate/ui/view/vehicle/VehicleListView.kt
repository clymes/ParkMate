package com.example.parkmate.ui.view.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.Vehicle
import com.example.parkmate.model.VehicleType
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListView(
    onBackButtonClick: () -> Unit,
    goToAddVehicleView: () -> Unit,
    goToVehicleDetailView: (vehicleId: Int) -> Unit,
    viewModel: VehicleViewModel = remember { VehicleViewModel() },
) {
    val context = LocalContext.current
    val vehicleList by viewModel.getAllVehicles(context).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Veicoli") },
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
            if (vehicleList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nessun veicolo salvato.",
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
                        items = vehicleList,
                        key = { vehicle -> vehicle.id },
                    ) { vehicle ->
                        VehicleItem(
                            vehicle = vehicle,
                            onVehicleDetailClick = { goToVehicleDetailView(vehicle.id) },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = goToAddVehicleView,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Aggiungi Veicolo")
            }
        }
    }
}

@Composable
fun VehicleItem(
    vehicle: Vehicle,
    onVehicleDetailClick: () -> Unit,
) {
    val vehicleType = VehicleType.fromString(vehicle.type)

    Button(
        onClick = onVehicleDetailClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = vehicleType.icon,
                contentDescription = vehicleType.displayName,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = vehicle.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = vehicle.plate,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Vai al dettaglio",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleListViewPreview() {
    VehicleListView(
        onBackButtonClick = {},
        goToAddVehicleView = {},
        goToVehicleDetailView = {},
    )
}
