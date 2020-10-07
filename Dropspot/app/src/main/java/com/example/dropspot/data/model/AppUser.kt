package com.example.dropspot.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class AppUser(@PrimaryKey val userId: Long,
                  var username: String,
                   var firstName: String,
                  var lastName: String,
                  var email: String) : Serializable {




}