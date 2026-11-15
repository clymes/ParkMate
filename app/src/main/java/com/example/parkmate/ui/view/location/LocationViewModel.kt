package com.example.parkmate.ui.view.location

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Location
import com.example.parkmate.model.PhotonFeature
import com.example.parkmate.network.PhotonService
import kotlinx.coroutines.flow.Flow

class LocationViewModel: ViewModel() {
    private val photonService = PhotonService()

    suspend fun searchPhotonLocations(query: String): List<PhotonFeature> {
        return photonService.searchLocations(query)
    }

    fun getAllLocations(context: Context): Flow<List<Location>> {
        val db = AppDatabase.getInstance(context)
        return db.locationDao().getAllLocations()
    }
}