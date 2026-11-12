package com.example.parkmate.ui.view.location

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.parkmate.data.AppDatabase
import com.example.parkmate.model.Location
import kotlinx.coroutines.flow.Flow

class LocationViewModel: ViewModel() {
    fun getAllLocations(context: Context): Flow<List<Location>> {
        val db = AppDatabase.getInstance(context)
        return db.locationDao().getAllLocations()
    }
}