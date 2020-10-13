package com.example.dropspot.data.model.dto.responses

data class JwtResponse(
    private val token: String,
    private val id: Long,
    private val username: String,
    private val email: String,
    private val roles: List<String>
) {
    private val type = "Bearer"

}