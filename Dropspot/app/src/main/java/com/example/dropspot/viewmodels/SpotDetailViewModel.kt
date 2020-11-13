package com.example.dropspot.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.dto.SpotDetail
import com.example.dropspot.data.repos.SpotDetailRepository
import kotlinx.coroutines.launch

class SpotDetailViewModel(private val spotDetailRepository: SpotDetailRepository) : ViewModel() {

    fun getSpotDetail(id: Long): LiveData<SpotDetail> {
        return spotDetailRepository.getSpotDetailBySpotId(id)
    }

    fun setSpotId(id: Long) {
        viewModelScope.launch {
            spotDetailRepository.fetchSpotDetailBySpotId(id)
        }
    }

}