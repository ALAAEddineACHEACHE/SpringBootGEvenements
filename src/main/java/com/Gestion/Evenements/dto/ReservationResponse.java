package com.Gestion.Evenements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse{
    private Long id;
    private int quantity;
    private String status;
    private LocalDateTime createdAt;
    private Long eventId;
    private Long userId;
    private double totalAmount;
    private EventResponse event;
    private PaymentResponse payment;

}