package com.example.dropspot.data.model.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private Boolean success;
    private String message;

    public MessageResponse(Boolean success,String message) {
        this.success = success;
        this.message = message;
    }
}
