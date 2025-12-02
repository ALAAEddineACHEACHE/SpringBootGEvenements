package com.Gestion.Evenements.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username; // login se fera par username
    private String password;
}
