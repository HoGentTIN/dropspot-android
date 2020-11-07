package com.example.dropspot.network

import com.example.dropspot.data.model.ParkSpot
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.StreetSpot
import com.example.dropspot.data.model.dto.requests.ParkSpotRequest
import com.example.dropspot.data.model.dto.requests.ParkSpotUpdateRequest
import com.example.dropspot.data.model.dto.requests.StreetSpotRequest
import com.example.dropspot.data.model.dto.requests.VoteRequest
import com.example.dropspot.data.model.dto.responses.MessageResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface SpotService {
    //spots
    @GET("spots")
    suspend fun getSpots(): Deferred<List<Spot>>

    @GET("spots/street")
    suspend fun getStreetSpots(): Deferred<List<StreetSpot>>

    @GET("spots/park")
    suspend fun getParkSpots(): Deferred<List<ParkSpot>>

    @GET("spots/{spotId}")
    suspend fun getSpotById(@Path("spotId") id: Long): Deferred<Spot>

    @DELETE("spots/{spotId}")
    suspend fun deleteSpot(@Path("spotId") id: Long): Deferred<MessageResponse>

    @PUT("spots/street/{spotId}")
    suspend fun udpateStreetSpot(
        @Body spot: StreetSpotRequest,
        @Path("spotId") id: Long
    ): Deferred<Spot>

    @PUT("spots/park/{spotId}")
    suspend fun udpateParkSpot(
        @Body spot: ParkSpotUpdateRequest,
        @Path("spotId") id: Long
    ): Deferred<Spot>

    @POST("spots/street")
    suspend fun addStreetSpot(@Body spot: StreetSpotRequest): Spot

    @POST("spots/park")
    suspend fun addParkSpot(@Body spot: ParkSpotRequest): Spot

    @POST("spots/{spotId}/criteria/{criterionId}/vote")
    suspend fun voteForSpot(
        @Body voteRequest: VoteRequest,
        @Path("spotId") spotId: Long,
        @Path("criterionId") criterionId: Long
    ): MessageResponse

    @GET("spots/getByRadius/{lat}/{long}/{radius}")
    suspend fun getSpotsInRadius(
        @Path("lat") latitude: Double,
        @Path("long") longitude: Double,
        @Path("radius") radius: Double
    ): List<Spot>
}