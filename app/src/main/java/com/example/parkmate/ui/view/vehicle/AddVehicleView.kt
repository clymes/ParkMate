package com.example.parkmate.ui.view.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddVehicleView() {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // TODO: freccia indietro
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome veicolo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipo veicolo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = plate,
                onValueChange = { plate = it },
                label = { Text("Targa") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { /* TODO: aggiungi veicolo alla lista */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text("Aggiungi Veicolo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddVehicleViewPreview() {
    AddVehicleView()
}
