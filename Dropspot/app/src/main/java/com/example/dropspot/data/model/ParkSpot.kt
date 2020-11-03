package com.example.dropspot.data.model

import androidx.room.Embedded
import androidx.room.Entity


@Entity
data class ParkSpot(
    override val spotId: Long,
    override val creatorId: Long,
    override var name: String,
    override var latitude: Double,
    override var longitude: Double,
    var entranceFee: Double = 0.0,
    @Embedded
    private var parkCategory: ParkCategory,
    @Embedded
    private var address: Address
) : Spot(spotId, creatorId, name, latitude, longitude) {

    val isFree: Boolean
        get() = entranceFee == 0.0

}