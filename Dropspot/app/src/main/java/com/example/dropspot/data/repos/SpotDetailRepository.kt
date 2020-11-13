package com.example.dropspot.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.dropspot.data.dao.SpotDetailDao
import com.example.dropspot.data.model.SpotDetail
import com.example.dropspot.network.SpotService
import com.example.dropspot.utils.Variables

class SpotDetailRepository(
    private val spotService: SpotService,
    private val spotDetailDao: SpotDetailDao
) {
    companion object {
        private val TAG = "spot_detail_repo"
    }

    fun getSpotDetailBySpotId(id: Long): LiveData<SpotDetail> {
        return spotDetailDao.getSpotDetailById(id)
    }

    suspend fun fetchSpotDetailBySpotId(id: Long) {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val response: SpotDetail = spotService.getSpotDetailById(id)
                Log.i(TAG, "response: $response")
                spotDetailDao.insert(response)
            } catch (e: Exception) {
                Log.d(TAG, e.message ?: "Something went wrong with getSpotDetail")
            }
        }
    }

}