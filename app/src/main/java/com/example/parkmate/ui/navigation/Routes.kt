package com.example.parkmate.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute
//VEICOLI
@Serializable
object VehicleListRoute
@Serializable
object AddVehicleRoute
@Serializable
data class VehicleDetailRoute(val vehicleId: Int)

//POSIZIONI
@Serializable
object LocationListRoute