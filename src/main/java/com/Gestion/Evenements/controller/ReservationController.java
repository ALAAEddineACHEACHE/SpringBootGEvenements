package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.service.ReservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestParam Long eventId,
                                     @RequestParam int quantity,
                                     @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = principal.getUser().getId();
        Reservation reservation = reservationService.createReservation(eventId, userId, quantity);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Reservation created",
                "reservation", reservation
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<?> userReservations(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getUser().getId();
        List<Reservation> reservations = reservationService.getUserReservations(userId);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "reservations", reservations
        ));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> eventReservations(@PathVariable Long eventId) {
        List<Reservation> reservations = reservationService.getEventReservations(eventId);

        return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "reservations", reservations
        ));
    }
}
