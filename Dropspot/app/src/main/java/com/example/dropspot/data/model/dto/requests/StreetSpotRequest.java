package com.example.dropspot.data.model.dto.requests;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StreetSpotRequest {
    private String name;

    private double latitude;

    private double longitude;

}
