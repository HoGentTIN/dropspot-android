package com.example.dropspot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dropspot.data.model.Spot

@Dao
interface SpotDao {
    //onconflict replace handles duplicate inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: Spot): Long

    @Update
    suspend fun update(spot: Spot)

    @Delete
    suspend fun delete(spot: Spot)

    @Query("SELECT * FROM spots")
    fun getAllSpots(): LiveData<List<Spot>>

    @Query("SELECT * FROM spots WHERE spotId=:id")
    fun getSpotById(id: Long): LiveData<Spot>

    @Query("SELECT * FROM spots WHERE creatorId=:id")
    fun getSpotByCreatorId(id: Long): LiveData<List<Spot>>

    //@Query("SELECT * FROM spots WHERE latitude < (:latitude + :radius) AND latitude > (:latitude - :radius) AND longitude < (:longitude + :radius) and longitude > (:longitude - :radius)")
    //suspend fun getSpotsInRadius(latitude: Double, longitude: Double, radius: Float): LiveData<List<Spot>>
}