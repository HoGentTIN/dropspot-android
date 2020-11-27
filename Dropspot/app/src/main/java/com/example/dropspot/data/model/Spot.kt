package com.example.dropspot.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "spots")
open class Spot(
    @PrimaryKey
    open val id: Long,
    open val creatorId: Long,
    open var name: String = "unnamed",
    open var latitude: Double = 0.0,
    open var longitude: Double = 0.0
) : Serializable {

    override fun toString(): String {
        return "Spot(spotId=$id, name=$name, latitude=$latitude, longitude=$longitude, creatorId=$creatorId)"
    }

}