package com.example.dropspot.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.dao.SpotDao
import com.example.dropspot.data.dao.SpotDetailDao
import com.example.dropspot.data.model.Address
import com.example.dropspot.data.model.ParkCategory
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.SpotDetail
import com.example.dropspot.data.model.requests.ParkSpotUpdateRequest
import com.example.dropspot.data.model.requests.StreetSpotRequest
import com.example.dropspot.data.model.responses.MessageResponse
import com.example.dropspot.network.SpotService
import com.example.dropspot.utils.Variables
import kotlinx.coroutines.launch

class EditSpotDetailViewModel(
    private val spotService: SpotService,
    private val spotDao: SpotDao,
    private val spotDetailDao: SpotDetailDao
) : ViewModel() {

    var spotDetail: SpotDetail? = null

    private val _updateSuccess = MutableLiveData<MessageResponse>()
    val updateSuccess: LiveData<MessageResponse>
        get() = _updateSuccess

    fun updateParkSpot(
        name: String,
        street: String,
        number: String,
        city: String,
        postal: String,
        state: String,
        country: String,
        cat: String,
        fee: Double
    ) {
        if (!Variables.isNetworkConnected.value!!) {
            _updateSuccess.value = MessageResponse(
                false,
                "Park spot update failed: No connection"
            )
            _updateSuccess.value = null
            return
        }

        val parkCategory: ParkCategory

        when (cat) {
            "Indoor" -> parkCategory = ParkCategory.INDOOR
            "Outdoor" -> parkCategory = ParkCategory.OUTDOOR
            "Out & Indoor" -> parkCategory = ParkCategory.OUTDOOR_INDOOR
            else -> parkCategory = ParkCategory.OUTDOOR_INDOOR
        }

        viewModelScope.launch {

            try {
                val response: SpotDetail = spotService.udpateParkSpot(
                    ParkSpotUpdateRequest(
                        name,
                        spotDetail!!.latitude,
                        spotDetail!!.longitude,
                        Address(street, number, postal, city, state, country),
                        fee,
                        parkCategory
                    ),
                    spotDetail!!.spotId
                )

                saveInCache(response)

                _updateSuccess.value = MessageResponse(true, "Park spot updated")
                _updateSuccess.value = null
            } catch (ex: Exception) {
                _updateSuccess.value =
                    MessageResponse(false, ex.message ?: "Park spot update failed")
                _updateSuccess.value = null
            }

        }
    }

    private suspend fun saveInCache(response: SpotDetail) {
        spotDao.insert(
            Spot(
                response.spotId,
                response.creatorId,
                response.spotName,
                response.latitude,
                response.longitude
            )
        )

        spotDetailDao.insert(response)
    }

    fun updateStreetSpot(name: String) {
        if (!Variables.isNetworkConnected.value!!) {
            _updateSuccess.value = MessageResponse(
                false,
                "Street spot update failed: No connection"
            )
            _updateSuccess.value = null
            return
        }

        viewModelScope.launch {
            try {
                val response: SpotDetail = spotService.updateStreetSpot(
                    StreetSpotRequest(
                        name,
                        spotDetail!!.latitude,
                        spotDetail!!.longitude
                    ),
                    spotDetail!!.spotId
                )

                saveInCache(response)

                _updateSuccess.value = MessageResponse(true, "Park spot updated")
                _updateSuccess.value = null
            } catch (ex: Exception) {
                _updateSuccess.value =
                    MessageResponse(false, ex.message ?: "Park spot update failed")
                _updateSuccess.value = null
            }

        }
    }


}