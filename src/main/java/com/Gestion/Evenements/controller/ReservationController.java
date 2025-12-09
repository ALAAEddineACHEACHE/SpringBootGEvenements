package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.ReservationRequest;
import com.Gestion.Evenements.dto.ReservationResponse;
import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.service.EventService.EventService;
import com.Gestion.Evenements.service.PaymentService.PaymentService;
import com.Gestion.Evenements.service.ReservationService.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final EventService eventService;

    @PostMapping("/reserve")
public ResponseEntity<?> reserve(@RequestBody ReservationRequest request,
                                 @AuthenticationPrincipal UserPrincipal principal) {

    // Vérifier que l'utilisateur n'est pas un ORGANIZER
    if (principal.getUser().getRoles().contains(Role.ROLE_ORGANIZER)) {
        return ResponseEntity.status(403).body(Map.of(
                "message", "Organizers cannot make reservations"
        ));
    }

    Long userId = principal.getUser().getId();

    Reservation reservation = reservationService.createReservation(
            request.getEventId(),
            userId,
            request.getQuantity()
    );

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
//    @DeleteMapping("/{reservationId}")
//    public ResponseEntity<?> deleteReservation(
//            @PathVariable Long reservationId,
//            @AuthenticationPrincipal UserPrincipal principal
//    ) {
//        Long userId = principal.getUser().getId();
//
//        reservationService.deleteReservation(reservationId, userId);
//
//        return ResponseEntity.ok(Map.of(
//                "message", "Reservation deleted successfully"
//        ));
//    }

//    @GetMapping("/my-reservations")
//    public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal UserPrincipal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(401).body(Map.of(
//                    "success", false,
//                    "message", "Unauthorized"
//            ));
//        }
//
//        Long userId = principal.getUser().getId();
//        try {
//            List<Reservation> reservations = reservationService.getUserReservations(userId);
//
//            // Formater la réponse pour le frontend
//            List<Map<String, Object>> formattedReservations = reservations.stream()
//                    .map(reservation -> {
//                        Map<String, Object> reservationMap = new HashMap<>();
//                        reservationMap.put("id", reservation.getId());
//                        reservationMap.put("quantity", reservation.getQuantity());
//                        reservationMap.put("status", reservation.getStatus().name());
//                        reservationMap.put("reservedAt", reservation.getReservedAt()); // Utilisez reservedAt au lieu de getCreatedAt()
//                        reservationMap.put("eventId", reservation.getEventId());
//                        reservationMap.put("userId", reservation.getUserId());
//                        reservationMap.put("totalAmount", reservation.getTotalAmount());
//
//                        // Récupérer l'événement via service
//                        Event event = eventService.getEventById(reservation.getEventId());
//                        if (event != null) {
//                            Map<String, Object> eventMap = new HashMap<>();
//                            eventMap.put("id", event.getId());
//                            eventMap.put("title", event.getTitle());
//                            eventMap.put("description", event.getDescription());
//                            eventMap.put("location", event.getLocation());
//                            eventMap.put("startAt", event.getStartAt());
//                            eventMap.put("endAt", event.getEndAt());
//                            eventMap.put("ticketPrice", event.getTicketPrice());
//                            eventMap.put("totalTickets", event.getTotalTickets());
//                            eventMap.put("imageUrl", event.getImageUrl());
//                            eventMap.put("category", event.getCategory());
//                            reservationMap.put("event", eventMap);
//                        }
//
//                        // Récupérer le paiement via service (si vous avez un service Payment)
//                        Payment payment = paymentService.findByReservationId(reservation.getId());
//                        if (payment != null) {
//                            Map<String, Object> paymentMap = new HashMap<>();
//                            paymentMap.put("id", payment.getId());
//                            paymentMap.put("amount", payment.getAmount());
//                            paymentMap.put("status", payment.getStatus());
//                            paymentMap.put("method", payment.getMethod());
//                            paymentMap.put("paidAt", payment.getPaidAt());
//                            reservationMap.put("payment", paymentMap); // Correction: mettre paymentMap, pas reservation.getPayment()
//                        }
//
//                        return reservationMap;
//                    })
//                    .toList();
//
//            return ResponseEntity.ok(Map.of(
//                    "success", true,
//                    "reservations", formattedReservations,
//                    "count", formattedReservations.size()
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "success", false,
//                    "message", "Failed to load reservations: " + e.getMessage()
//            ));
//        }
//    }
@GetMapping("/my-reservations")
public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal UserPrincipal principal) {
    if (principal == null) {
        return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Unauthorized"
        ));
    }

    Long userId = principal.getUser().getId();
    try {
        // Utilisez directement la méthode qui retourne ReservationResponse
        List<ReservationResponse> reservations = reservationService.getUserReservationsWithDetails(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "reservations", reservations,
                "count", reservations.size()
        ));

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to load reservations: " + e.getMessage()
        ));
    }
}


    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long reservationId,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Unauthorized"
            ));
        }

        Long userId = principal.getUser().getId();

        try {
            reservationService.deleteReservation(reservationId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Reservation cancelled successfully"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
