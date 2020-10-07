package com.example.dropspot.data.model.dto.requests;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParkSpotRequest {
    private String name;

    private double latitude;

    private double longitude;

    private double entranceFee;

    private boolean isIndoor;

    private String street;

    private String houseNumber;

    private String postalCode;

    private String city;

    private String state;

    private String country;
}
