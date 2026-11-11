package com.example.parkmate.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocalShipping
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
    TRUCK(
        displayName = "Camion",
        icon = Icons.Default.LocalShipping,
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
    );

    companion object {
        fun fromString(displayName: String?): VehicleType {
            if (displayName == null) {
                return OTHER
            }
            val vehicleType = entries.find {
                it.displayName.equals(displayName, ignoreCase = true)
            }
            if (vehicleType == null) {
                return OTHER
            }
            return vehicleType
        }
    }
}
