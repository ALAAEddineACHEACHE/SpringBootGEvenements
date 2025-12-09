package com.Gestion.Evenements.service.ReservationService;

import com.Gestion.Evenements.exception.ReservationException;
import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.NotificationService.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Reservation createReservation(Long eventId, Long userId, int quantity) {
//        if (quantity <= 0) throw new RuntimeException("Quantity must be greater than 0");
//        if (quantity > 4) throw new RuntimeException("Maximum 4 tickets per reservation");
//
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        int available = event.getTotalTickets() - event.getTicketsSold();
//        if (quantity > available) throw new RuntimeException("Not enough tickets available");
//
//        // Vérifier les réservations existantes pour limiter à 4 tickets par user
//        List<Reservation> existing = reservationRepository.findByUserIdAndEventId(userId, eventId);
//        int alreadyReserved = existing.stream().mapToInt(Reservation::getQuantity).sum();
//        if (alreadyReserved + quantity > 4)
//            throw new RuntimeException("You cannot reserve more than 4 tickets for this event");
        if (quantity <= 0)
            throw new ReservationException("La quantité doit être supérieure à 0");

        if (quantity > 4)
            throw new ReservationException("Tu ne peux pas réserver plus de 4 tickets");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ReservationException("Event introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ReservationException("Utilisateur introuvable"));

        int available = event.getTotalTickets() - event.getTicketsSold();
        if (quantity > available)
            throw new ReservationException("Pas assez de tickets disponibles");

        List<Reservation> existing = reservationRepository.findByUserIdAndEventId(userId, eventId);
        int alreadyReserved = existing.stream().mapToInt(Reservation::getQuantity).sum();
        if (alreadyReserved + quantity > 4)
            throw new ReservationException("Tu ne peux pas dépasser 4 tickets pour cet événement");


        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setEventId(eventId);
        reservation.setUserId(userId);
        reservation.setQuantity(quantity);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalAmount(quantity * event.getTicketPrice());

        Reservation saved = reservationRepository.save(reservation);

        // Mettre à jour ticketsSold
        event.setTicketsSold(event.getTicketsSold() + quantity);
        eventRepository.save(event);

        // 🔹 Envoi email via l'instance injectée
        User user1 = userRepository.findById(userId).orElseThrow();
        notificationService.sendReservationEmail(saved, user.getUsername(), user.getEmail());

        return saved;
    }


    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getEventReservations(Long eventId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getEventId().equals(eventId))
                .toList();
    }
    @Transactional
    public void deleteReservation(Long reservationId, Long userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("Réservation introuvable"));

        // Vérifier propriétaire
        if (!reservation.getUserId().equals(userId)) {
            throw new ReservationException("Vous n'avez pas le droit de supprimer cette réservation");
        }

        Event event = eventRepository.findById(reservation.getEventId())
                .orElseThrow(() -> new ReservationException("Événement introuvable"));

        // Restaurer les tickets
        event.setTicketsSold(event.getTicketsSold() - reservation.getQuantity());
        eventRepository.save(event);

        // Supprimer réservation
        reservationRepository.delete(reservation);
    }

}
