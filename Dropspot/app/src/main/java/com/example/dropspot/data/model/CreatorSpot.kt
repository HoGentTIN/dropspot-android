package com.example.dropspot.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CreatorSpot(@Embedded val creator : AppUser,
@Relation(parentColumn = "userId",
entityColumn = "creatorId")
val spots: List<Spot>) {

}