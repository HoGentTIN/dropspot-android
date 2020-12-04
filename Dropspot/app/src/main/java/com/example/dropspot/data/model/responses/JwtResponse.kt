package com.example.dropspot.data.model.responses

import com.example.dropspot.data.model.AppUser

data class JwtResponse(
    val message: String,
    val success: Boolean = false,
    val token: String = "",
    val user: AppUser? = null
) {
    private val type = "Bearer"

}