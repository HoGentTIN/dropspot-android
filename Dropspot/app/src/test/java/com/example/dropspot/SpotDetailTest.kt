package com.example.dropspot

import com.example.dropspot.data.model.Address
import com.example.dropspot.data.model.ParkCategory
import com.example.dropspot.data.model.SpotDetail
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SpotDetailTest {

    @Test
    fun safeSliderValueForEntranceFee_alreadySafeValue_TheSameValue() {
        val inputEntranceFee = 0.10000
        val expectedValue: Float = inputEntranceFee.toFloat()

        val spotDetail = SpotDetail(
            0,
            "test",
            0,
            "test",
            0.0,
            0.0,
            Address("", "", "", "", "", ""),
            ArrayList(),
            false,
            false,
            ParkCategory.OUTDOOR_INDOOR,
            inputEntranceFee
        )

        assertThat(spotDetail.getSaveSliderValueForEntranceFee()).isEqualTo(expectedValue)
    }

    @Test
    fun safeSliderValueForEntranceFee_NotSafeValueA_SafeValue() {
        val inputEntranceFee = 0.10234
        val expectedValue: Float = 0.10.toFloat()

        val spotDetail = SpotDetail(
            0,
            "test",
            0,
            "test",
            0.0,
            0.0,
            Address("", "", "", "", "", ""),
            ArrayList(),
            false,
            false,
            ParkCategory.OUTDOOR_INDOOR,
            inputEntranceFee
        )

        assertThat(spotDetail.getSaveSliderValueForEntranceFee()).isEqualTo(expectedValue)
    }

    @Test
    fun safeSliderValueForEntranceFee_NotSafeValueB_SafeValue() {
        val inputEntranceFee = 0.12345
        val expectedValue: Float = 0.10.toFloat()

        val spotDetail = SpotDetail(
            0,
            "test",
            0,
            "test",
            0.0,
            0.0,
            Address("", "", "", "", "", ""),
            ArrayList(),
            false,
            false,
            ParkCategory.OUTDOOR_INDOOR,
            inputEntranceFee
        )

        assertThat(spotDetail.getSaveSliderValueForEntranceFee()).isEqualTo(expectedValue)
    }

    @Test
    fun safeSliderValueForEntranceFee_NotSafeValueC_SafeValue() {
        val inputEntranceFee = 0.13456
        val expectedValue: Float = 0.15.toFloat()

        val spotDetail = SpotDetail(
            0,
            "test",
            0,
            "test",
            0.0,
            0.0,
            Address("", "", "", "", "", ""),
            ArrayList(),
            false,
            false,
            ParkCategory.OUTDOOR_INDOOR,
            inputEntranceFee
        )

        assertThat(spotDetail.getSaveSliderValueForEntranceFee()).isEqualTo(expectedValue)
    }

}