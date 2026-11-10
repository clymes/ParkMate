package com.example.parkmate.model

data class Vehicle(
    val id: Int,
    val name: String,
    val type: VehicleType,
    val parked: Boolean,
    val plate: String
)
