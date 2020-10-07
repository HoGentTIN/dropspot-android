package com.example.dropspot.data.model


import java.io.Serializable

data class Criterion(private val criterionId: Long,
                     private var criterionName: String = "unnamed",
                     private var description : String = "undefined") : Serializable {
}