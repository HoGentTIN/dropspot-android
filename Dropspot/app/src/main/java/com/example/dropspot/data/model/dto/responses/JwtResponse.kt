package com.example.dropspot.data.model.dto.responses

data class JwtResponse(
    val token: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>,
    val success: Boolean,
    val message: String
) {
    private val type = "Bearer"

}