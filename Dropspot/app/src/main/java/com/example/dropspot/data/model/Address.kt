package com.example.dropspot.data.model

import java.io.Serializable


data class Address(
        private val street: String = "",
        private val houseNumber: String = "",
        private val postalCode: String = "",
        private val city: String = "",
        private val state: String = "",
        private val country: String = "") : Serializable {

}