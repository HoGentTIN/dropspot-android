package com.example.dropspot.data.model


import java.io.Serializable


data class Vote(
    private val spotId: Long,
    private val voterId: Long,
    private val criterionId: Long,
    private var value: Double = 0.0
) : Serializable