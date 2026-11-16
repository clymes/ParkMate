package com.example.parkmate.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

enum class Parking(
    val displayName: String,
    val requiresEndTime: Boolean,
    val hasHourlyRate: Boolean,
    val icon: ImageVector,
) {
    FREE(
        displayName = "Gratuito",
        requiresEndTime = false,
        hasHourlyRate = false,
        icon = Icons.Default.Timer,
    ),
    HOURLY_PAID(
        displayName = "Pagamento orario",
        requiresEndTime = false,
        hasHourlyRate = true,
        icon = Icons.Default.AttachMoney,
    ),
    FIXED(
        displayName = "Orario fisso",
        requiresEndTime = true,
        hasHourlyRate = false,
        icon = Icons.Default.Schedule,
    );

    companion object {
        fun fromString(displayName: String?): Parking {
            if (displayName == null) {
                return FREE
            }

            for (parking in entries) {
                if (parking.displayName.equals(displayName, ignoreCase = true)) {
                    return parking
                }
            }

            return FREE
        }
    }
}
