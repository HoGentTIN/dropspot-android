package com.example.dropspot.data.model.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreetSpotUpdateRequest {

    private String name;

    private double latitude;

    private double longitude;
}
