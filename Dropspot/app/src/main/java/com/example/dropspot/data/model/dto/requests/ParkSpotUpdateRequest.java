package com.example.dropspot.data.model.dto.requests;

import com.example.dropspot.data.model.Address;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParkSpotUpdateRequest {

    private String name;

    private double latitude;

    private double longitude;

    private double entranceFee;

    private boolean indoor;

    private Address address;
}
