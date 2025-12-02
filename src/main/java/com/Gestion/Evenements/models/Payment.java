package com.Gestion.Evenements.models;

import com.Gestion.Evenements.models.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence vers la réservation payée
    @Column(nullable = false)
    private Long reservationId;

    // Montant payé (calculé automatiquement)
    @Column(nullable = false, scale = 2, precision = 19)
    private BigDecimal amount;

    // Mode/simulated gateway id (optionnel)
    private String provider; // ex: "SIMULATED"

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String failureReason;
}
