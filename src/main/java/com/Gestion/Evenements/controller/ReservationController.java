package com.Gestion.Evenements.controller;


import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import com.Gestion.Evenements.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 EventRepository eventRepository,
                                 UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/reserve")
    public Reservation reserve(@RequestParam Long eventId, @RequestParam Long userId, @RequestParam int qty) {
        if (qty <= 0) throw new RuntimeException("Quantity > 0 required");
        if (qty > 4) throw new RuntimeException("Max 4 tickets per reservation");

        Event event = eventRepository.findById(eventId).orElseThrow();
        int available = event.getTotalTickets() - event.getTicketsSold();
        if (qty > available) throw new RuntimeException("Not enough tickets available");

        // check user's existing reservations for this event (enforce per-user limit across reservations)
        List<Reservation> existing = reservationRepository.findByUserIdAndEventId(userId, eventId);
        int already = existing.stream().mapToInt(Reservation::getQuantity).sum();
        if (already + qty > 4) throw new RuntimeException("You cannot have more than 4 tickets in total for this event");

        // create reservation PENDING
        Reservation r = new Reservation();
        r.setEventId(eventId);
        r.setUserId(userId);
        r.setQuantity(qty);
        r.setReservedAt(LocalDateTime.now());
        r.setStatus(ReservationStatus.PENDING);
        r.setTotalAmount(qty * event.getTicketPrice());
        Reservation saved = reservationRepository.save(r);

        // temporarily increment ticketsSold to reserve (optional: implement hold + TTL)
        event.setTicketsSold(event.getTicketsSold() + qty);
        eventRepository.save(event);

        return saved;
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> byUser(@PathVariable Long userId) {
        return reservationRepository.findAll().stream().filter(r -> r.getUserId().equals(userId)).toList();
    }
}
