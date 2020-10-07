package com.example.dropspot.network

import com.example.dropspot.data.model.ParkSpot
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.StreetSpot
import com.example.dropspot.data.model.dto.requests.*
import com.example.dropspot.data.model.dto.responses.MessageResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface SpotService {
    //spots
    @GET("spots")
    fun getSpots(): Deferred<List<Spot>>

    @GET("spots/street")
    fun getStreetSpots(): Deferred<List<StreetSpot>>

    @GET("spots/park")
    fun getParkSpots(): Deferred<List<ParkSpot>>

    @GET("spots/{spotId}")
    fun getSpotById(@Path("spotId")id : Long): Deferred<Spot>


    @DELETE("spots/{spotId}")
    fun deleteSpot(@Path("spotId")id : Long): Deferred<MessageResponse>


    @PUT("spots/street/{spotId}")
    fun udpateStreetSpot(@Body spot: StreetSpotUpdateRequest, @Path("spotId")id : Long): Deferred<Spot>

    @PUT("spots/park/{spotId}")
    fun udpateParkSpot(@Body spot: ParkSpotUpdateRequest, @Path("spotId")id : Long): Deferred<Spot>


    @POST("spots/street")
    fun addStreetSpot(@Body spot: StreetSpotRequest):Deferred<StreetSpot>

    @POST("spots/park")
    fun addParkSpot(@Body spot: ParkSpotRequest):Deferred<ParkSpot>

    @POST("spots/{spotId}/criteria/{criterionId}/vote")
    fun voteForSpot(@Body voteRequest : VoteRequest, @Path("spotId") spotId : Long, @Path("criterionId") criterionId : Long):Deferred<MessageResponse>

}