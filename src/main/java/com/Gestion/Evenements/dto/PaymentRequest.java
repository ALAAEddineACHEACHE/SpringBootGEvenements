package com.Gestion.Evenements.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long reservationId;
    private String method; // exemple: "CARD"
}
