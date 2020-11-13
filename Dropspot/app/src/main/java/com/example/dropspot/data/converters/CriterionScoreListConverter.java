package com.example.dropspot.data.converters;

import androidx.room.TypeConverter;

import com.example.dropspot.data.model.dto.CriterionScore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CriterionScoreListConverter {
    @TypeConverter
    public static ArrayList<CriterionScore> fromString(String value) {
        Type listType = new TypeToken<ArrayList<CriterionScore>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<CriterionScore> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
