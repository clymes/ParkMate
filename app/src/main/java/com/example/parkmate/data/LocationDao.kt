package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.parkmate.model.Location
import kotlinx.coroutines.flow.Flow

// Data Access Object per le posizioni
@Dao
interface LocationDao {
    @Insert
    suspend fun addLocation(parking: Location)

    @Update
    suspend fun updateLocation(location: Location)

    @Query("SELECT * FROM locations WHERE isDeleted = 0")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(id: Int): Flow<Location?>

    @Query("UPDATE locations SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteLocation(id: Int)
}