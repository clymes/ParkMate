package com.example.parkmate.ui.view.location

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Location
import com.example.parkmate.utils.PhotonFeature
import com.example.parkmate.utils.PhotonService
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {
    private val photonService = PhotonService()

    suspend fun searchPhotonLocations(query: String): List<PhotonFeature> {
        return photonService.searchLocations(query)
    }

    fun getAllLocations(context: Context): Flow<List<Location>> {
        val db = AppDatabase.getInstance(context)
        return db.locationDao().getAllLocations()
    }

    fun addLocation(
        context: Context,
        name: String,
        lat: Float,
        long: Float,
        address: String
    ) {
        val db = AppDatabase.getInstance(context)
        viewModelScope.launch {
            db.locationDao().addLocation(
                Location(
                    name = name,
                    lat = lat,
                    long = long,
                    address = address
                )
            )
        }
    }

    fun getLocationById(context: Context, id: Int): Flow<Location?> {
        val db = AppDatabase.getInstance(context)
        return db.locationDao().getLocationById(id)
    }

    fun softdeleteLocation(context: Context, id: Int) {
        val db = AppDatabase.getInstance(context)
        viewModelScope.launch {
            db.locationDao().softDeleteLocation(id)
        }
    }

    fun updateLocation(
        context: Context,
        id: Int,
        name: String,
        lat: Float,
        long: Float,
        address: String
    ) {
        val db = AppDatabase.getInstance(context)
        viewModelScope.launch {
            db.locationDao().updateLocation(
                Location(
                    id = id,
                    name = name,
                    lat = lat,
                    long = long,
                    address = address
                )
            )
        }
    }
}