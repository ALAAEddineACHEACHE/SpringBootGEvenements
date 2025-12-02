package com.Gestion.Evenements.dto;

import com.Gestion.Evenements.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor

    public class AuthResponse {
        private String token;
    private String username;
    private String email;
     private Set<Role> roles;


}

