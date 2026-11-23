package com.example.parkmate.ui.view.vehicle

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.VehicleType
import com.example.parkmate.ui.component.BackButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailView(
    vehicleId: Int,
    onBackButtonClick: () -> Unit,
    viewModel: VehicleViewModel = remember { VehicleViewModel() },
) {
    val context = LocalContext.current
    val vehicle by viewModel.getVehicleById(context, vehicleId).collectAsState(initial = null)
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showDeleteDialog && vehicle != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Sei sicuro?")
            },
            text = {
                Text(text = "Sei sicuro di voler eliminare il veicolo ${vehicle!!.name}? Quest'azione è irreversibile")
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Indietro")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.softdeleteVehicle(context, vehicle!!.id) {
                            onBackButtonClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Elimina")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dettagli") },
                navigationIcon = {
                    BackButton(onBackButtonClick = onBackButtonClick)
                },
                actions = {
                    if (vehicle != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Elimina veicolo",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (vehicle == null) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Veicolo non trovato.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                val currentVehicle = vehicle!!
                val vehicleType = VehicleType.fromString(currentVehicle.type)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        // Card del nome veicolo e simbolo
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = vehicleType.icon,
                                    contentDescription = vehicleType.displayName,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = currentVehicle.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                )
                            }
                        }
                    }

                    item {
                        // Seconda card per info dettagliate
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Nome",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = currentVehicle.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Targa",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = currentVehicle.plate,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Categoria",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = vehicleType.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Stato",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = if (currentVehicle.parked) "Parcheggiato" else "Non parcheggiato",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (currentVehicle.parked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showEditBottomSheet = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Modifica")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Parcheggia")
            }
        }
    }

    if (showEditBottomSheet && vehicle != null) {
        var editName by remember(vehicle) { mutableStateOf(vehicle!!.name) }
        var editSelectedType by remember(vehicle) { mutableStateOf(VehicleType.fromString(vehicle!!.type)) }
        var isEditTypeDropdownExpanded by remember { mutableStateOf(false) }
        var editPlate by remember(vehicle) { mutableStateOf(vehicle!!.plate) }

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
                    text = "Modifica Veicolo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nome veicolo") },
                    modifier = Modifier.fillMaxWidth(),
                )

                ExposedDropdownMenuBox(
                    expanded = isEditTypeDropdownExpanded,
                    onExpandedChange = { isEditTypeDropdownExpanded = !isEditTypeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = editSelectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo veicolo") },
                        leadingIcon = {
                            Icon(
                                imageVector = editSelectedType.icon,
                                contentDescription = editSelectedType.displayName
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEditTypeDropdownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                    )

                    ExposedDropdownMenu(
                        expanded = isEditTypeDropdownExpanded,
                        onDismissRequest = { isEditTypeDropdownExpanded = false },
                    ) {
                        VehicleType.entries.forEach { vehicleType ->
                            DropdownMenuItem(
                                text = { Text(vehicleType.displayName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = vehicleType.icon,
                                        contentDescription = vehicleType.displayName,
                                    )
                                },
                                onClick = {
                                    editSelectedType = vehicleType
                                    isEditTypeDropdownExpanded = false
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = editPlate,
                    onValueChange = { editPlate = it },
                    label = { Text("Targa") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.updateVehicle(
                            context = context,
                            vehicle = vehicle!!,
                            name = editName,
                            type = editSelectedType,
                            plate = editPlate,
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            },
                            onSuccess = { successMessage ->
                                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                                scope.launch {
                                    sheetState.hide()
                                    showEditBottomSheet = false
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Modifica")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleDetailViewPreview() {
    VehicleDetailView(
        vehicleId = 1,
        onBackButtonClick = {},
    )
}
