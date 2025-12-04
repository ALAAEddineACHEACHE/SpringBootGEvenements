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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;         // total à payer
    private String status;         // SUCCESS or FAILED
    private String method;         // OPTIONNEL (CASH, CARD, etc.)
    private LocalDateTime paidAt;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}

