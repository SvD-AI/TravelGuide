package com.example.travelguide.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<Trip>>

    @Insert
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun getTripById(id: Long): Trip?

    suspend fun insertOrUpdate(updatedTrip: Trip) {
        val existingTrip = getTripById(updatedTrip.id)
        if (existingTrip == null) {
            insert(updatedTrip)
        } else {
            update(updatedTrip)
        }
    }
}
