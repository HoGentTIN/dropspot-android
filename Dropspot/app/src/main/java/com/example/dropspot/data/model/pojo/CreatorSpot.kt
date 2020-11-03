package com.example.dropspot.data.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.example.dropspot.data.model.AppUser
import com.example.dropspot.data.model.Spot

data class CreatorSpot(
    @Embedded val creator: AppUser,
    @Relation(
        parentColumn = "userId",
        entityColumn = "creatorId"
    )
    val spots: List<Spot>
)