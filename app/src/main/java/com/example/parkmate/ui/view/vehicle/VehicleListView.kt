package com.example.parkmate.ui.view.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListView(
    onBackButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Veicoli") },
                navigationIcon = {
                    BackButton(onBackButtonClick = onBackButtonClick)
                }
            )
            // TODO: aggiungere tasto "aggiungi veicolo"
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // TODO: lista veicoli
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleListViewPreview() {
    VehicleListView(
        onBackButtonClick = {}
    )
}
