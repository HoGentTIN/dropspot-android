package com.example.dropspot.network

import com.example.dropspot.data.model.dto.requests.LoginRequest
import com.example.dropspot.data.model.dto.requests.RegisterRequest
import com.example.dropspot.data.model.dto.responses.JwtResponse
import com.example.dropspot.data.model.dto.responses.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup/user")
    suspend fun register(@Body signUpRequest: RegisterRequest): Response<MessageResponse>

    @POST("auth/signup/mod")
    suspend fun registerMod(@Body signUpRequest: RegisterRequest): Response<MessageResponse>

    @POST("auth/signup/admin")
    suspend fun registerAdmin(@Body signUpRequest: RegisterRequest): Response<MessageResponse>

    @POST("auth/signin")
    suspend fun login(@Body loginRequest: LoginRequest): Response<JwtResponse>

}