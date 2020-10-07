package com.example.dropspot.data.model


import java.io.Serializable
import java.util.*


data class RelationId(private val senderId: Long, private val recipientId: Long) : Serializable {
}