package com.Gestion.Evenements.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;
    private String method;// exemple: "CARD"
    private String cardNumber;
    private String cardHolder;
    private String expiryDate; // Format: "MM/YY"
    private String cvv;
    private Double amount;
}
