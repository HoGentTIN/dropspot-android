package com.example.dropspot.data.model

import java.io.Serializable
import java.util.*

data class VoteId(    private val voterId: Long,
                      private val spotId: Long,
                      private val criterionId: Long) : Serializable {



}