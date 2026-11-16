package com.example.parkmate.ui.view.parking

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkmate.model.Location
import com.example.parkmate.model.Parking
import com.example.parkmate.model.Vehicle
import com.example.parkmate.ui.view.location.LocationViewModel
import com.example.parkmate.ui.view.vehicle.VehicleViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParkingViewModel(
    private val vehicleViewModel: VehicleViewModel = VehicleViewModel(),
    private val locationViewModel: LocationViewModel = LocationViewModel(),
) : ViewModel() {
    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    private val _selectedParking = MutableStateFlow<Parking?>(null)
    val selectedParking: StateFlow<Parking?> = _selectedParking.asStateFlow()

    fun selectVehicle(vehicle: Vehicle) {
        _selectedVehicle.value = vehicle
    }

    fun selectLocation(location: Location) {
        _selectedLocation.value = location
    }

    fun selectParking(parking: Parking) {
        _selectedParking.value = parking
    }

    fun clearSelection() {
        _selectedVehicle.value = null
        _selectedLocation.value = null
        _selectedParking.value = null
    }

    //riprende dal VehicleViewModel
    fun getAllVehicles(context: Context): Flow<List<Vehicle>> {
        return vehicleViewModel.getAllVehicles(context)
    }

    //riprende dal LocationViewModel
    fun getAllLocations(context: Context): Flow<List<Location>> {
        return locationViewModel.getAllLocations(context)
    }
}
