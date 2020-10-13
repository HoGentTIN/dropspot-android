package com.example.dropspot.data.model.dto.requests

data class ParkSpotRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val entranceFee: Double = 0.0,
    val isIndoor: Boolean = false,
    private val street: String,
    private val houseNumber: String,
    private val postalCode: String,
    private val city: String,
    private val state: String,
    private val country: String
)