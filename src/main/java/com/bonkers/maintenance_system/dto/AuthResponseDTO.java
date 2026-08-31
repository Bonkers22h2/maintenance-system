package com.bonkers.maintenance_system.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthResponseDTO {
    @NotBlank
    private String token;

    @NotBlank
    private String name;


    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}