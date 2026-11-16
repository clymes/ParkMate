package com.example.parkmate.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute
//VEICOLI
@Serializable
object VehicleListRoute
@Serializable
object VehicleAddRoute
@Serializable
data class VehicleDetailRoute(val vehicleId: Int)

//POSIZIONI
@Serializable
object LocationListRoute
@Serializable
object LocationAddRoute

//PARCHEGGIO
@Serializable
object ActiveParkingsRoute
@Serializable
object ParkingLocationRoute
@Serializable
object ParkingVehicleRoute
@Serializable
object ParkingTypeRoute
@Serializable
data class ParkingInfoRoute(val parkingType: String)
@Serializable
object ParkingSummaryRoute