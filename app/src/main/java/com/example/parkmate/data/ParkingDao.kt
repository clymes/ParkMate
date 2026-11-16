package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.parkmate.model.Parking
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {
    @Insert
    suspend fun insertParking(parking: Parking)

    @Query("SELECT * FROM parkings")
    fun getAllParkings(): Flow<List<Parking>>
}
