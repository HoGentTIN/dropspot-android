package com.example.dropspot.data.model.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dropspot.data.model.Address

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
    var address: Address?,
    var criteriaScore: ArrayList<CriterionScore>,
    var liked: Boolean
)