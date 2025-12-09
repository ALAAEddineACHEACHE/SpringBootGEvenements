package com.Gestion.Evenements.dto;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private double amount;
    private String status;
    private String method;
    private LocalDateTime paidAt;
}