package com.pm.authservice.dto;

import jakarta.persistence.Column;

public class LoginResponseDTO
{

    private final String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }
}
