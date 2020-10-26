package com.example.dropspot.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.repos.SpotRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val spotRepository: SpotRepository) : ViewModel() {

    //spots
    private var _spots: LiveData<List<Spot>> = spotRepository.spots

    init {
        /*_selectedSpot.value = Spot(0,0,"select a spot", 0.0, 0.0)
        viewModelScope.launch {
            initSpots()
        }*/
    }

    private suspend fun initSpots() {
        viewModelScope.launch {
            spotRepository.getAllSpots()
        }
    }

}
