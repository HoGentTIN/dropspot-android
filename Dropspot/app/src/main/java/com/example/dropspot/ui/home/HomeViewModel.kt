package com.example.dropspot.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.repos.SpotRepository
import kotlinx.coroutines.*

class HomeViewModel(private val spotRepository: SpotRepository) : ViewModel() {

    //coroutines
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //selected spot
    private val _selectedSpot = MutableLiveData<Spot>()
    val selectedSpot: LiveData<Spot>
        get() = _selectedSpot

    //spots
    private var _spots: LiveData<List<Spot>> = spotRepository.spots


    init {
        _selectedSpot.value = Spot(0,0,"select a spot", 0.0, 0.0)
        viewModelScope.launch {
            initSpots()
        }
    }

    private suspend fun initSpots() {
        withContext(Dispatchers.IO) {
            spotRepository.getAllSpots()

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}
