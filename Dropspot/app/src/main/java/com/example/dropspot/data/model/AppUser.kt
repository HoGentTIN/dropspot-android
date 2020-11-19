package com.example.dropspot.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "users")
data class AppUser(
    @PrimaryKey val userId: Long,
    var username: String,
    var firstName: String,
    var lastName: String,
    var email: String
) : Serializable, Parcelable