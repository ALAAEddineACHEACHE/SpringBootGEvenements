package com.Gestion.Evenements.dto;

import com.Gestion.Evenements.models.enums.Role;
import jakarta.persistence.Column;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank(message = "name is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$",
            message = "Invalid phone number format")
    @Column(name = "PhoneNumber", nullable = false, length = 20)
    private String phoneNumber;
    private Role role;



}