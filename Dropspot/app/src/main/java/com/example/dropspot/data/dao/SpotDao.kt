package com.example.dropspot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dropspot.data.model.Spot

@Dao
interface SpotDao {
    //onconflict replace handles duplicate inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(spot: Spot): Long

    @Update
    fun update(spot: Spot)

    @Delete
    fun delete(spot: Spot)

    @Query("SELECT * FROM spots")
    fun getAllSpots(): LiveData<List<Spot>>


    @Query("SELECT * FROM spots WHERE spotId=:id")
    fun getSpotById(id: Long): LiveData<Spot>

    @Query("SELECT * FROM spots WHERE creatorId=:id")
    fun getSpotByCreatorId(id: Long): LiveData<List<Spot>>
}