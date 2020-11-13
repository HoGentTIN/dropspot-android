package com.example.dropspot.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spot_details")
data class SpotDetail(
    @PrimaryKey
    val spotId: Long,
    var spotName: String,
    val creatorId: Long,
    val creatorName: String,
    val latitude: Double,
    val longitude: Double,
    @Embedded
    var address: Address?
    //, val rating: Map<Criterion,Double>
)