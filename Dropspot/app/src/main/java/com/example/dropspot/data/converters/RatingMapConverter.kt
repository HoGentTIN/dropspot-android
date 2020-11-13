package com.example.dropspot.data.converters

import androidx.room.TypeConverter
import com.example.dropspot.data.model.Criterion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RatingMapConverter {

    @TypeConverter
    fun stringToMap(data: String): Map<Criterion, Double> {
        val mapType = object : TypeToken<Map<Criterion, Double>>() {

        }.type
        return Gson().fromJson(data, mapType)
    }

    @TypeConverter
    fun mapToString(map: Map<Criterion, Double>): String {
        return Gson().toJson(map)
    }


}