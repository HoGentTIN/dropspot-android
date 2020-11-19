package com.example.dropspot.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dropspot.data.model.SpotDetail

@Dao
interface SpotDetailDao {

    @Query("SELECT * FROM spot_details WHERE spotId=:id")
    fun getSpotDetailById(id: Long): LiveData<SpotDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spotDetail: SpotDetail)

    @Delete()
    fun delete(spotDetail: SpotDetail)

}