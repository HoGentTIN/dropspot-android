package com.example.dropspot.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.dto.SpotDetail
import com.example.dropspot.data.model.dto.responses.MessageResponse
import com.example.dropspot.data.repos.SpotDetailRepository
import kotlinx.coroutines.launch

class SpotDetailViewModel(private val spotDetailRepository: SpotDetailRepository) : ViewModel() {

    companion object {
        private const val TAG = "spot_detail_vm"
    }

    private var spotId: Long? = null

    private val _voteSuccess = MutableLiveData<MessageResponse>()
    val voteSuccess: LiveData<MessageResponse?> get() = _voteSuccess

    fun getSpotDetail(id: Long): LiveData<SpotDetail> {
        return spotDetailRepository.getSpotDetailBySpotId(id)
    }

    fun setSpotId(id: Long) {
        spotId = spotId
        viewModelScope.launch {
            spotDetailRepository.fetchSpotDetailBySpotId(id)
        }
    }

    fun vote(criterionId: Long, value: Double) {
        Log.i(TAG, "spotId:$spotId criterionId:$criterionId val:$value")
        /*
        viewModelScope.launch {
           _voteSuccess.value =  spotDetailRepository.vote(spotId,criterionId,VoteRequest(value))
            _voteSuccess.value = null
        }*/
    }


}