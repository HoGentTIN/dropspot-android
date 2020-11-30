package com.example.dropspot.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.roundToInt

@Parcelize
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
    var liked: Boolean,
    var owner: Boolean,
    var parkCategory: ParkCategory?,
    var entranceFee: Double?
) : Parcelable {

    fun getLocationString(): String {
        if (address == null) {
            return "LAT: $latitude\nLONG: $longitude"
        } else {
            return address!!.getAddressString()
        }
    }

    fun isPark(): Boolean {
        return address != null
    }

    fun getParkCatString(): String {
        var res: String = ""
        when (parkCategory) {
            ParkCategory.INDOOR -> res = "Indoor"
            ParkCategory.OUTDOOR -> res = "Outdoor"
            ParkCategory.OUTDOOR_INDOOR -> res = "Outdoor / Indoor"
        }
        return res
    }

    fun getDamageString(): String {
        return Currency.getInstance("EUR").symbol + String.format("%.2f", entranceFee)
    }

    fun getOverallScore(): Int {
        var res: Double = 0.0
        criteriaScore.forEach {
            res += it.score
        }

        return (res / criteriaScore.size).roundToInt()
    }

}