package com.example.dropspot.data.repos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dropspot.data.dao.SpotDao
import com.example.dropspot.data.model.ParkCategory
import com.example.dropspot.data.model.ParkSpot
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.StreetSpot
import com.example.dropspot.data.model.dto.requests.ParkSpotRequest
import com.example.dropspot.data.model.dto.requests.StreetSpotRequest
import com.example.dropspot.network.SpotService
import com.example.dropspot.utils.Variables

class SpotRepository(
    private val spotDao: SpotDao,
    private val spotService: SpotService
) {

    var spots = MutableLiveData<List<Spot>>()
    var spotsInRadius = MutableLiveData<List<Spot>>()


    suspend fun getAllSpots() {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val onlineSpots: List<Spot> = spotService.getSpots().await()
                val offlineSpots: List<Spot> = spotDao.getAllSpots().value!!
                saveInLocalDb(onlineSpots)
                this.spots.value = onlineSpots + offlineSpots
            } catch (e: Exception) {
                Log.d("spot_response", e.message ?: "Something went wrong with getAllSpots")
            }

        } else {
            this.spots.value = spotDao.getAllSpots().value
        }
    }

    suspend fun saveInLocalDb(onlineSpots: List<Spot>) {
        onlineSpots.forEach {
            spotDao.insert(it)
        }
    }

    suspend fun getSpotsInRadius(latitude: Double, longitude: Double, radius: Double) {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val onlineSpots: List<Spot> =
                    spotService.getSpotsInRadius(latitude, longitude, radius)
                // val offlineSpots: List<Spot> = spotDao.getSpotsInRadius(latitude,longitude,radius).value?: emptyList()
                saveInLocalDb(onlineSpots)
                this.spotsInRadius.value = onlineSpots //+ offlineSpots
            } catch (e: Exception) {
                Log.d("spot_response", e.message ?: "something went wrong with getSpotsInRadius")
            }

        } else {
            this.spotsInRadius.value =
                spotDao.getAllSpots().value//getSpotsInRadius(latitude,longitude,radius).value?: emptyList()
        }
    }

    suspend fun addStreetSpot(name: String, latitude: Double, longitude: Double): StreetSpot? {

        val request = StreetSpotRequest(name, latitude, longitude)
        Log.i("spot_request", request.toString())

        if (Variables.isNetworkConnected.value!!) {
            try {
                val spotResponse = spotService.addStreetSpot(request)
                Log.i("spot_response", spotResponse.toString())
                /*
                if (spotResponse.isSuccessful) {
                    spotDao.insert(spotResponse.body()!!)
                    return spotResponse.body()
                } else {
                    return null
                }*/
                return null

            } catch (e: Exception) {
                Log.d("spot_response", e.message ?: "something went wrong with addStreetSpot")
                return null
            }
        } else {
            return null
        }

    }

    suspend fun addParkSpot(
        name: String,
        latitude: Double,
        longitude: Double,
        street: String,
        houseNumber: String,
        city: String,
        postalCode: String,
        state: String,
        country: String,
        parkCategory: ParkCategory,
        fee: Double
    ): ParkSpot? {

        val request = ParkSpotRequest(
            name,
            latitude,
            longitude,
            fee,
            parkCategory,
            street,
            houseNumber,
            postalCode,
            city,
            state,
            country
        )
        Log.i("spot_request", request.toString())

        if (Variables.isNetworkConnected.value!!) {
            try {
                val spotResponse = spotService.addParkSpot(request)
                Log.i("spot_response", spotResponse.toString())
                if (spotResponse.isSuccessful) {
                    spotDao.insert(spotResponse.body()!!)
                    return spotResponse.body()
                } else {
                    return null
                }
            } catch (e: Exception) {
                Log.d("spot_response", e.message ?: "something went wrong with addParkSpot")
                return null
            }
        } else {
            return null
        }
    }

}