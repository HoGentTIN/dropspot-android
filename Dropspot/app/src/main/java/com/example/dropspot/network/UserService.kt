package com.example.dropspot.network

import com.example.dropspot.data.model.AppUser
import com.example.dropspot.data.model.Relation
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.dto.responses.MessageResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface UserService {
    @GET("users/me")
    fun getMe(): Deferred<AppUser>

    @GET("user/mySpots")
    fun getMySpots(): Deferred<List<Spot>>

    @GET("users/favorites")
    fun getMyFavoriteSpots(): Deferred<List<Spot>>

    @POST("users/favorites/{spotId}")
    fun addFavoriteSpot(@Path("spotId") id : Long): Deferred<MessageResponse>

    @DELETE("users/favorites/{spotId}")
    fun removeFavoriteSpot(@Path("spotId") id:Long):Deferred<MessageResponse>

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