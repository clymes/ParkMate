package com.example.parkmate.ui.view.vehicle

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Vehicle
import com.example.parkmate.model.VehicleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehicleViewModel : ViewModel() {

    fun validateVehicle(name: String, type: String, plate: String): String? {
        if (name.isBlank()) return "Il nome del veicolo non può essere vuoto."
        if (type.isBlank()) return "Il tipo di veicolo non può essere vuoto."
        if (plate.isBlank()) return "La targa non può essere vuota."
        return null
    }

    fun vehicleTypeToString(type: VehicleType?): String {
        if (type == null) {
            return ""
        }
        return type.displayName.uppercase()
    }

    fun addVehicle(
        context: Context,
        name: String,
        type: VehicleType?,
        plate: String,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit,
    ) {
        val typeString = vehicleTypeToString(type)
        val errorMessage = validateVehicle(name, typeString, plate)
        val successMessage = "Veicolo salvato con successo"

        if (errorMessage != null) {
            onError(errorMessage)
            return
        }

        val db = AppDatabase.getInstance(context)
        viewModelScope.launch(Dispatchers.IO) {
            db.vehicleDao().addVehicle(
                Vehicle(
                    name = name.trim(),
                    type = typeString,
                    plate = plate.trim(),
                ),
            )
            withContext(Dispatchers.Main) {
                onSuccess(successMessage)
            }
        }
    }

    fun updateVehicle(
        context: Context,
        vehicle: Vehicle,
        name: String,
        type: VehicleType?,
        plate: String,
        onError: (String) -> Unit,
        onSuccess: (String) -> Unit,
    ) {
        val typeString = vehicleTypeToString(type)
        val errorMessage = validateVehicle(name, typeString, plate)
        val successMessage = "Veicolo modificato con successo"

        if (errorMessage != null) {
            onError(errorMessage)
            return
        }

        val db = AppDatabase.getInstance(context)
        viewModelScope.launch(Dispatchers.IO) {
            db.vehicleDao().updateVehicle(
                vehicle.copy(
                    name = name.trim(),
                    type = typeString,
                    plate = plate.trim(),
                )
            )
            withContext(Dispatchers.Main) {
                onSuccess(successMessage)
            }
        }
    }

    fun getAllVehicles(context: Context): Flow<List<Vehicle>> {
        val db = AppDatabase.getInstance(context)
        return combine(
            db.vehicleDao().getAllVehicles(),
            db.parkingDao().getAllParkings()
        ) { vehicles, parkings ->
            val currentTime = System.currentTimeMillis()
            val activeVehicleIds = parkings
                .filter { it.isActive(currentTime) }
                .map { it.vehicleId }
                .toSet()

            vehicles.map { vehicle ->
                val isParked = vehicle.id in activeVehicleIds
                if (vehicle.parked != isParked) {
                    viewModelScope.launch(Dispatchers.IO) {
                        db.vehicleDao().updateParkedStatus(vehicle.id, isParked)
                    }
                }
                vehicle.copy(parked = isParked)
            }
        }
    }

    fun getVehicleById(context: Context, vehicleId: Int): Flow<Vehicle?> {
        val db = AppDatabase.getInstance(context)
        return combine(
            db.vehicleDao().getVehicleById(vehicleId),
            db.parkingDao().getAllParkings()
        ) { vehicle, parkings ->
            if (vehicle == null) {
                null
            } else {
                val currentTime = System.currentTimeMillis()
                val isParked = parkings.any { it.vehicleId == vehicle.id && it.isActive(currentTime) }
                if (vehicle.parked != isParked) {
                    viewModelScope.launch(Dispatchers.IO) {
                        db.vehicleDao().updateParkedStatus(vehicle.id, isParked)
                    }
                }
                vehicle.copy(parked = isParked)
            }
        }
    }

    fun softdeleteVehicle(context: Context, vehicleId: Int, onSuccess: () -> Unit) {
        val db = AppDatabase.getInstance(context)
        viewModelScope.launch(Dispatchers.IO) {
            db.vehicleDao().softDeleteVehicle(vehicleId)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}
