package com.example.parkmate.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val lat: Float,
    val long: Float,
    val address: String,
    val isDeleted: Boolean = false,
)