package com.example.parkmate.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute
@Serializable
object VehicleListRoute
@Serializable
object AddVehicleRoute
@Serializable
data class VehicleDetailRoute(val vehicleId: Int)