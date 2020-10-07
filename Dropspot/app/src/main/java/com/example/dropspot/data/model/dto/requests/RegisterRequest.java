package com.example.dropspot.data.model.dto.requests;

import lombok.Getter;


@Getter
public class RegisterRequest {

    private String firstName;

    private String	lastName;

    private String username;


    private String password;


    private String email;

    public RegisterRequest(String firstName, String lastName, String username, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
