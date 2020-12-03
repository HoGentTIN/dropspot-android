package com.example.dropspot.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.dropspot.data.dao.SpotDao
import com.example.dropspot.data.dao.SpotDetailDao
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.SpotDetail
import com.example.dropspot.data.model.requests.ParkSpotUpdateRequest
import com.example.dropspot.data.model.requests.StreetSpotRequest
import com.example.dropspot.data.model.requests.VoteRequest
import com.example.dropspot.data.model.responses.MessageResponse
import com.example.dropspot.network.SpotService
import com.example.dropspot.network.UserService
import com.example.dropspot.utils.Variables

class SpotDetailRepository(
    private val spotService: SpotService,
    private val spotDetailDao: SpotDetailDao,
    private val userService: UserService,
    private val spotDao: SpotDao
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

    suspend fun vote(spotId: Long, criterionId: Long, voteRequest: VoteRequest): MessageResponse {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val response: MessageResponse =
                    spotService.voteForSpot(voteRequest, spotId, criterionId)
                Log.i(TAG, "response: $response")
                return response
            } catch (e: Exception) {
                return MessageResponse(
                    false,
                    "Failed to vote: ${e.message}"
                )
            }
        } else {
            return MessageResponse(
                false,
                "Failed to vote: No Connection"
            )
        }
    }

    suspend fun favoriteSpot(spotId: Long): MessageResponse {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val response: MessageResponse =
                    userService.addFavoriteSpot(spotId)
                return response
            } catch (e: Exception) {
                return MessageResponse(
                    false,
                    "Failed to favorite: ${e.message}"
                )
            }

        } else {
            return MessageResponse(
                false,
                "Failed to favorite: No Connection"
            )
        }
    }

    suspend fun unFavoriteSpot(spotId: Long): MessageResponse {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val response: MessageResponse =
                    userService.removeFavoriteSpot(spotId)
                return response
            } catch (e: Exception) {
                return MessageResponse(
                    false,
                    "Failed to unfavorite: ${e.message}"
                )
            }
        } else {
            return MessageResponse(
                false,
                "Failed to unfavorite: No Connection"
            )
        }
    }

    suspend fun deleteSpot(spotDetail: SpotDetail): MessageResponse {
        if (Variables.isNetworkConnected.value!!) {
            try {
                val response: MessageResponse =
                    spotService.deleteSpot(spotDetail.spotId) //MessageResponse(true,"fake")
                if (response.success) removeInCache(spotDetail)
                return response
            } catch (e: java.lang.Exception) {
                return MessageResponse(
                    false,
                    "Failed to delete: ${e.message}"
                )
            }
        } else {
            return MessageResponse(
                false,
                "Failed to delete: No connection"
            )
        }
    }

    suspend fun updateParkSpot(
        request: ParkSpotUpdateRequest,
        spotDetail: SpotDetail
    ): MessageResponse {

        if (!Variables.isNetworkConnected.value!!) {
            return MessageResponse(
                false,
                "Park spot update failed: No connection"
            )
        }

        try {

            val response: SpotDetail = spotService.updateParkSpot(
                request,
                spotDetail.spotId
            )

            saveInCache(response)

            return MessageResponse(true, "Park spot updated")
        } catch (ex: Exception) {
            return MessageResponse(false, ex.message ?: "Park spot update failed")
        }
    }

    suspend fun updateStreetSpot(
        request: StreetSpotRequest,
        spotDetail: SpotDetail
    ): MessageResponse {

        if (!Variables.isNetworkConnected.value!!) {
            return MessageResponse(
                false,
                "Street spot update failed: No connection"
            )
        }

        try {
            val response: SpotDetail = spotService.updateStreetSpot(
                request,
                spotDetail.spotId
            )

            saveInCache(response)

            return MessageResponse(true, "Park spot updated")
        } catch (ex: Exception) {
            return MessageResponse(false, ex.message ?: "Park spot update failed")
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

    private suspend fun removeInCache(response: SpotDetail) {
        spotDao.delete(
            Spot(
                response.spotId,
                response.creatorId,
                response.spotName,
                response.latitude,
                response.longitude
            )
        )

        spotDetailDao.delete(response)
    }


}