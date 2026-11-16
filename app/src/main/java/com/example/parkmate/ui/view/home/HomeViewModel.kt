package com.example.parkmate.ui.view.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Location
import com.example.parkmate.model.Parking
import com.example.parkmate.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    fun isParkingActive(parking: Parking, currentTime: Long = System.currentTimeMillis()): Boolean {
        val startValid = parking.startTimestamp <= currentTime
        val endValid = (parking.endTimestamp == null) || (parking.endTimestamp > currentTime)
        return startValid && endValid
    }

    fun getActiveParkings(context: Context): Flow<List<Parking>> {
        val db = AppDatabase.getInstance(context)
        return db.parkingDao().getAllParkings().map { parkings ->
            val currentTime = System.currentTimeMillis()
            parkings.filter { isParkingActive(it, currentTime) }
        }
    }

    fun getAllVehicles(context: Context): Flow<List<Vehicle>> {
        val db = AppDatabase.getInstance(context)
        return db.vehicleDao().getAllVehicles()
    }

    fun getAllLocations(context: Context): Flow<List<Location>> {
        val db = AppDatabase.getInstance(context)
        return db.locationDao().getAllLocations()
    }

    fun stopParking(context: Context, parking: Parking) {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(context)
            val updatedParking = parking.copy(endTimestamp = System.currentTimeMillis())
            db.parkingDao().updateParking(updatedParking)
        }
    }
}
