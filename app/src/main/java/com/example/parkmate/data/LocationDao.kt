package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parkmate.model.Location
import kotlinx.coroutines.flow.Flow

// Data Access Object per le posizioni
@Dao
interface LocationDao {
    @Insert
    suspend fun addLocation(parking: Location)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(id: Int): Flow<Location?>
}