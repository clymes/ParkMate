package com.example.parkmate.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

enum class VehicleType(
    val displayName: String,
    val icon: ImageVector,
) {
    CAR(
        displayName = "Auto",
        icon = Icons.Default.DirectionsCar,
    ),
    MOTORCYCLE(
        displayName = "Moto",
        icon = Icons.Default.TwoWheeler,
    ),
    BICYCLE(
        displayName = "Bicicletta",
        icon = Icons.AutoMirrored.Filled.DirectionsBike,
    ),
    SCOOTER(
        displayName = "Monopattino",
        icon = Icons.Default.ElectricScooter,
    ),
    OTHER(
        displayName = "Altro",
        icon = Icons.Default.Category,
    ),
}
