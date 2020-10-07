package com.example.dropspot.data.repos

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dropspot.data.dao.SpotDao
import com.example.dropspot.data.model.Spot
import com.example.dropspot.network.SpotService
import java.lang.Exception

class SpotRepository(private val spotDao: SpotDao,
                     private val spotService: SpotService,
                     private val connectivityManager: ConnectivityManager) {

    private var _spots = MutableLiveData<List<Spot>>()
    val spots: LiveData<List<Spot>> get() = _spots

    suspend fun getAllSpots() {
        if (connectedToInternet()) {
            try {
                val onlineSpots: List<Spot> = spotService.getSpots().await()
                val offlineSpots: List<Spot> = spotDao.getAllSpots().value!!
                saveInLocalDb(onlineSpots)
                this._spots.value = onlineSpots + offlineSpots
            } catch (e: Exception) {
                Log.d("spotRepo", e.message)
            }

        } else {
            this._spots.value = spotDao.getAllSpots().value
        }
    }

    private fun saveInLocalDb(onlineSpots: List<Spot>) {
        onlineSpots.forEach {
            spotDao.insert(it)
        }
    }

    private fun connectedToInternet(): Boolean {
        with(connectivityManager) {
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}