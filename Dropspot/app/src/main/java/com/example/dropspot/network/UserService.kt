package com.example.dropspot.network

import com.example.dropspot.data.model.AppUser
import com.example.dropspot.data.model.Spot
import com.example.dropspot.data.model.responses.MessageResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @GET("users/me")
    suspend fun getMe(): AppUser

    @GET("user/mySpots")
    suspend fun getMySpots(): List<Spot>

    @GET("users/favorites")
    suspend fun getMyFavoriteSpots(): List<Spot>

    @POST("users/favorites/{spotId}")
    suspend fun addFavoriteSpot(@Path("spotId") id: Long): MessageResponse

    @DELETE("users/favorites/{spotId}")
    suspend fun removeFavoriteSpot(@Path("spotId") id: Long): MessageResponse

    /*
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
     */

}