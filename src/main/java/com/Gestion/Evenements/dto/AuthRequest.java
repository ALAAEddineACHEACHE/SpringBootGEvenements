package com.Gestion.Evenements.dto;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
    private String email;
}

