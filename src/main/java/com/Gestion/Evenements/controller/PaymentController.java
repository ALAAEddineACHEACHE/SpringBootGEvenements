package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.PaymentRequest;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.service.PaymentService.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.Gestion.Evenements.models.UserPrincipal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Autoriser le frontend React
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request,
                                 @AuthenticationPrincipal UserPrincipal principal) {

        // Vérification que l'utilisateur est authentifié
        if (principal == null || principal.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized - Please login first"
            ));
        }

        // Vérification du rôle USER
        boolean isUser = principal.getUser().getRoles().contains(Role.ROLE_USER);
        if (!isUser) {
            return ResponseEntity.status(403).body(Map.of(
                    "success", false,
                    "message", "Only regular users can make payments. Organizers and Admins cannot make payments."
            ));
        }

        Long userId = principal.getUser().getId();

        try {
            // Valider les données de la carte (simulation)
            if (!validateCardDetails(request)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid card details"
                ));
            }

            // Traitement du paiement
            Payment payment = paymentService.pay(
                    request.getReservationId(),
                    request.getMethod(),
                    request.getCardNumber(),
                    request.getCardHolder(),
                    request.getExpiryDate(),
                    request.getCvv(),
                    request.getAmount(),
                    userId
            );

            // Réponse formatée pour le frontend
            Map<String, Object> response = Map.of(
                    "success", payment.getStatus().equals("SUCCESS"),
                    "message", payment.getStatus().equals("SUCCESS") ?
                            "Payment successful! Your tickets are confirmed." :
                            "Payment failed. Please try again.",
                    "payment", Map.of(
                            "id", payment.getId(),
                            "amount", payment.getAmount(),
                            "status", payment.getStatus(),
                            "method", payment.getMethod(),
                            "paidAt", payment.getPaidAt(),
                            "reservationId", payment.getReservation() != null ? payment.getReservation().getId() : null
                    ),
                    "timestamp", System.currentTimeMillis()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    // Méthode de validation des détails de carte (simulation)
    private boolean validateCardDetails(PaymentRequest request) {
        if (request.getMethod().equals("CARD")) {
            // Validation basique
            if (request.getCardNumber() == null || request.getCardNumber().replace(" ", "").length() != 16) {
                return false;
            }
            if (request.getCardHolder() == null || request.getCardHolder().trim().isEmpty()) {
                return false;
            }
            if (request.getExpiryDate() == null || !request.getExpiryDate().matches("\\d{2}/\\d{2}")) {
                return false;
            }
            if (request.getCvv() == null || request.getCvv().length() != 3) {
                return false;
            }

            // Valider la date d'expiration
            String[] expiryParts = request.getExpiryDate().split("/");
            if (expiryParts.length != 2) return false;

            try {
                int month = Integer.parseInt(expiryParts[0]);
                int year = Integer.parseInt(expiryParts[1]) + 2000; // Convertir YY en YYYY

                if (month < 1 || month > 12) return false;

                // Vérifier si la carte est expirée
                java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
                if (expiry.isBefore(java.time.YearMonth.now())) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    // Endpoint pour récupérer les paiements de l'utilisateur
    @GetMapping("/user")
    public ResponseEntity<?> getUserPayments(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Long userId = principal.getUser().getId();
        // Implémentez cette méthode dans votre service
        // List<Payment> payments = paymentService.getUserPayments(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "payments", java.util.Collections.emptyList(), // À implémenter
                "count", 0
        ));
    }

    // Endpoint pour récupérer un paiement spécifique
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Endpoint à implémenter"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}