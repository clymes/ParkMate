package com.example.parkmate.ui.view.vehicle

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.VehicleType
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleView(
    onBackButtonClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = remember { VehicleViewModel() }

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<VehicleType?>(null) }
    var isTypeDropdownExpanded by remember { mutableStateOf(value = false) }
    var plate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome veicolo") },
                modifier = Modifier.fillMaxWidth(),
            )

            ExposedDropdownMenuBox(
                expanded = isTypeDropdownExpanded,
                onExpandedChange = { isTypeDropdownExpanded = !isTypeDropdownExpanded },
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = selectedType?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo veicolo") },
                    leadingIcon = selectedType?.let { type ->
                        {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = type.displayName,
                            )
                        }
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeDropdownExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )

                ExposedDropdownMenu(
                    expanded = isTypeDropdownExpanded,
                    onDismissRequest = { isTypeDropdownExpanded = false },
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
                                selectedType = vehicleType
                                isTypeDropdownExpanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Targa") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    viewModel.addVehicle(
                        context = context,
                        name = name,
                        type = selectedType,
                        plate = plate,
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = { successMessage ->
                            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                            onBackButtonClick()
                        },
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                Text("Aggiungi Veicolo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddVehicleViewPreview() {
    AddVehicleView(
        onBackButtonClick = {},
    )
}
