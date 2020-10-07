package com.example.dropspot.data.model

import androidx.room.Entity

@Entity
data class StreetSpot(override val spotId: Long,
                      override val creatorId:Long,
                      override var name: String,
                      override var latitude: Double,
                      override var longitude: Double
                    ) : Spot(spotId, creatorId, name, latitude, longitude) {
}