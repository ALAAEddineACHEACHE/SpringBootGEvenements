package com.Gestion.Evenements.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Verification code must be a 4-digit number")
    private String verificationCode;
    @Email(message = "Email should be valid")
    private String email;
}

