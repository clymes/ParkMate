package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.model.ParkingType
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingTypeView(
    onBackButtonClick: () -> Unit,
    onParkingSelected: (ParkingType) -> Unit = {},
    viewModel: ParkingViewModel = remember { ParkingViewModel() },
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Tipo Parcheggio") },
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
            ParkingType.entries.forEach { parkingType ->
                Button(
                    onClick = {
                        viewModel.selectParkingType(parkingType)
                        onParkingSelected(parkingType)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = parkingType.icon,
                            contentDescription = parkingType.displayName,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        Text(
                            text = parkingType.displayName,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParkingTypeViewPreview() {
    ParkingTypeView(
        onBackButtonClick = {},
    )
}
