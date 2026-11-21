package com.example.parkmate.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.parkmate.model.Parking
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {
    @Insert
    suspend fun insertParking(parking: Parking)

    @Update
    suspend fun updateParking(parking: Parking)

    @Query("SELECT * FROM parkings")
    fun getAllParkings(): Flow<List<Parking>>

    @Query("SELECT * FROM parkings WHERE id = :id")
    fun getParkingById(id: Int): Flow<Parking?>

    @Query("SELECT * FROM parkings WHERE vehicleId = :vehicleId")
    suspend fun getParkingsByVehicle(vehicleId: Int): List<Parking>
}
