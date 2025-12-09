package com.Gestion.Evenements.models;

import com.Gestion.Evenements.models.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventId;
    private Long userId;
    private int quantity;
    private double totalAmount;
    private LocalDateTime reservedAt;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // PENDING, PAID, CANCELLED
    // Ajouter un getter pour createdAt (qui retourne reservedAt)
    public LocalDateTime getCreatedAt() {
        return this.reservedAt;
    }

}
