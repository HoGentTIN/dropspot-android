package com.example.dropspot.network

import com.example.dropspot.data.model.Relation
import com.example.dropspot.data.model.dto.AppUser
import com.example.dropspot.data.model.dto.Spot
import com.example.dropspot.data.model.dto.responses.MessageResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @GET("users/me")
    suspend fun getMe(): Response<AppUser>

    @GET("user/mySpots")
    fun getMySpots(): Deferred<List<Spot>>

    @GET("users/favorites")
    suspend fun getMyFavoriteSpots(): List<Spot>

    @POST("users/favorites/{spotId}")
    suspend fun addFavoriteSpot(@Path("spotId") id: Long): MessageResponse

    @DELETE("users/favorites/{spotId}")
    suspend fun removeFavoriteSpot(@Path("spotId") id: Long): MessageResponse

    @POST("/friendRequests/{userId}")
    fun requestFriend(@Path("userId") userId :Long): Deferred<MessageResponse>

    @GET("/friendRequests")
    fun getFriendRequests():Deferred<List<Relation>>

    @PUT("/friendRequests/{userId}/accept")
    fun acceptFriendRequest(@Path("userId") userId:Long) : Deferred<Relation>

    @PUT("/friendRequests/{userId}/decline")
    fun declineFriendRequest(@Path("userId") userId:Long) : Deferred<Relation>

    @GET("/friends")
    fun getMyFriends():Deferred<List<AppUser>>

    @DELETE("/friends/{userId}")
    fun deleteFriend(@Path("userId") userId : Long):Deferred<MessageResponse>

}