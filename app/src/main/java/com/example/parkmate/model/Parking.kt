package com.example.parkmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parkings")
data class Parking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehicleId: Int,
    val locationId: Int,
    val parkingType: String,
    val startTimestamp: Long,
    val endTimestamp: Long?,
    val hourlyRate: Double?,
    val notes: String,
    val imageUri: String?
)
