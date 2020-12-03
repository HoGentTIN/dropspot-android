package com.example.dropspot.data.model

enum class ParkCategory {
    OUTDOOR {
        override fun toString(): String {
            return "Outdoor"
        }
    },
    INDOOR {
        override fun toString(): String {
            return "Indoor"
        }
    },
    OUTDOOR_INDOOR {
        override fun toString(): String {
            return "Outdoor & Indoor"
        }
    }
}