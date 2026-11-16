package com.example.parkmate.ui.view.parking

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkmate.model.Location
import com.example.parkmate.model.ParkingType
import com.example.parkmate.model.Parking
import com.example.parkmate.model.Vehicle
import com.example.parkmate.data.AppDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
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

    private val _selectedParkingType = MutableStateFlow<ParkingType?>(null)
    val selectedParkingType: StateFlow<ParkingType?> = _selectedParkingType.asStateFlow()

    var startTimestamp: Long = 0
    var endTimestamp: Long? = null
    var hourlyRate: Double? = null
    var notes: String = ""
    var imageUri: String? = null

    fun selectVehicle(vehicle: Vehicle) {
        _selectedVehicle.value = vehicle
    }

    fun selectLocation(location: Location) {
        _selectedLocation.value = location
    }

    fun selectParkingType(parkingType: ParkingType) {
        _selectedParkingType.value = parkingType
    }

    fun clearSelection() {
        _selectedVehicle.value = null
        _selectedLocation.value = null
        _selectedParkingType.value = null
        startTimestamp = 0
        endTimestamp = null
        hourlyRate = null
        notes = ""
        imageUri = null
    }

    fun saveParking(context: Context, onSuccess: () -> Unit) {
        val vehicleId = _selectedVehicle.value?.id
        val locationId = _selectedLocation.value?.id
        val type = _selectedParkingType.value?.displayName

        if (vehicleId != null && locationId != null && type != null) {
            val parking = Parking(
                vehicleId = vehicleId,
                locationId = locationId,
                parkingType = type,
                startTimestamp = startTimestamp,
                endTimestamp = endTimestamp,
                hourlyRate = hourlyRate,
                notes = notes,
                imageUri = imageUri
            )
            viewModelScope.launch {
                val db = AppDatabase.getInstance(context)
                db.parkingDao().insertParking(parking)
                onSuccess()
            }
        }
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
