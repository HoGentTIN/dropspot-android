package com.example.dropspot.data.converters

import androidx.room.TypeConverter
import com.example.dropspot.data.model.CriterionScore
import com.example.dropspot.data.model.ParkCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object Converters {

    @JvmStatic
    @TypeConverter
    fun fromParkCategory(pc: ParkCategory?): String? {
        if (pc == null) {
            return null
        }
        return pc.name
    }

    @JvmStatic
    @TypeConverter
    fun toParkCategory(s: String?): ParkCategory? {
        if (s == null) {
            return null
        }
        return ParkCategory.valueOf(s)
    }

    @JvmStatic
    @TypeConverter
    fun fromString(value: String?): ArrayList<CriterionScore> {
        val listType =
            object : TypeToken<ArrayList<CriterionScore?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @JvmStatic
    @TypeConverter
    fun fromArrayList(list: ArrayList<CriterionScore?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

}