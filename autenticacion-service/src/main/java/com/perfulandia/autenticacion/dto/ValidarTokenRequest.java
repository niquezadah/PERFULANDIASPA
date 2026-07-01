package com.perfulandia.autenticacion.dto;

import jakarta.validation.constraints.NotBlank;

public class ValidarTokenRequest {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    public ValidarTokenRequest() {
    }

    public ValidarTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}