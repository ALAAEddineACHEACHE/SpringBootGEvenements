package com.Gestion.Evenements.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long eventId;
    private int quantity;
}
