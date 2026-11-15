package com.example.parkmate.ui.view.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingTypeView(
    onBackButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit = {},
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
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            //WIP: placeholder x creare spazio
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )

            Button(
                onClick = onNextButtonClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "procedi")
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
