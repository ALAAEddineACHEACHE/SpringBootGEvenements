package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.PaymentRequest;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.service.MyUserDetailsService;
import com.Gestion.Evenements.service.PaymentService.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Gestion.Evenements.models.UserPrincipal;
import java.util.Map;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request,
                                 @AuthenticationPrincipal UserPrincipal principal) {

        // Vérification que l'utilisateur est authentifié
        if (principal == null || principal.getUser() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        // Vérification du rôle USER
        boolean isUser = principal.getUser().getRoles().contains(Role.ROLE_USER);
        if (!isUser) {
            return ResponseEntity.status(403).body(Map.of("message", "Only users can make payments"));
        }

        Long userId = principal.getUser().getId();

        Payment payment = paymentService.pay(
                request.getReservationId(),
                request.getMethod(),
                userId
        );

        return ResponseEntity.ok(Map.of(
                "message", payment.getStatus().equals("SUCCESS") ? "Payment successful" : "Payment failed",
                "payment", payment
        ));
    }
}
