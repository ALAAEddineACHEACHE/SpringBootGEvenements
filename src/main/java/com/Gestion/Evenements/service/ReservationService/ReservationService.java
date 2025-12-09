package com.Gestion.Evenements.service.ReservationService;

import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.dto.PaymentResponse;
import com.Gestion.Evenements.dto.ReservationResponse;
import com.Gestion.Evenements.exception.ReservationException;
import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.EventService.EventService;
import com.Gestion.Evenements.service.NotificationService.NotificationService;
import com.Gestion.Evenements.service.PaymentService.PaymentService;
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
    private final EventService eventService;
    private final PaymentService paymentService;

    @Transactional
    public Reservation createReservation(Long eventId, Long userId, int quantity) {
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
    public List<ReservationResponse> getUserReservationsWithDetails(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        return reservations.stream()
                .map(reservation -> {
                    ReservationResponse dto = new ReservationResponse();
                    dto.setId(reservation.getId());
                    dto.setQuantity(reservation.getQuantity());
                    dto.setStatus(reservation.getStatus().name());
                    dto.setCreatedAt(reservation.getReservedAt());
                    dto.setEventId(reservation.getEventId());
                    dto.setUserId(reservation.getUserId());
                    dto.setTotalAmount(reservation.getTotalAmount());

                    // Récupérer et mapper l'événement en utilisant EventResponse
                    Event event = eventService.getEventById(reservation.getEventId());
                    if (event != null) {
                        dto.setEvent(mapToEventResponse(event));
                    }

                    // Récupérer et mapper le paiement en utilisant PaymentResponse
                    Payment payment = paymentService.findByReservationId(reservation.getId());
                    if (payment != null) {
                        dto.setPayment(mapToPaymentResponse(payment));
                    }

                    return dto;
                })
                .toList();
    }
    private EventResponse mapToEventResponse(Event event) {
        if (event == null) return null;

        EventResponse eventResponse = new EventResponse();
        eventResponse.setId(event.getId());
        eventResponse.setTitle(event.getTitle());
        eventResponse.setDescription(event.getDescription());
        eventResponse.setLocation(event.getLocation());
        eventResponse.setStartAt(event.getStartAt());
        eventResponse.setEndAt(event.getEndAt());
        eventResponse.setTicketPrice(event.getTicketPrice());
        eventResponse.setTotalTickets(event.getTotalTickets());
        eventResponse.setTicketsSold(event.getTicketsSold());
        eventResponse.setTicketsRemaining(event.getTotalTickets() - event.getTicketsSold());
        eventResponse.setImageUrl(event.getImageUrl());
        eventResponse.setCategory(event.getCategory());
        eventResponse.setOrganizerId(event.getOrganizerId());

        return eventResponse;
    }

    // Utilisez PaymentResponse existant
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        if (payment == null) return null;

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(payment.getId());
        paymentResponse.setAmount(payment.getAmount());
        paymentResponse.setStatus(payment.getStatus());
        paymentResponse.setMethod(payment.getMethod());
        paymentResponse.setPaidAt(payment.getPaidAt());

        return paymentResponse;
    }
}
