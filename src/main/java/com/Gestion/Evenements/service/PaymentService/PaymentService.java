package com.Gestion.Evenements.service.PaymentService;

import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.PaymentRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final ReservationRepository reservationRepo;
    private final PaymentRepository paymentRepo;
    private final EventRepository eventRepository;

    public Payment pay(Long reservationId, String method, Long userId) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Vérification que l'utilisateur correspond
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("You cannot pay for someone else's reservation");
        }

        Event event = eventRepository.findById(reservation.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        double total = reservation.getQuantity() * event.getTicketPrice();

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(total);
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());

        // Simulation du paiement
        boolean success = Math.random() > 0.2; // 80% SUCCESS
        payment.setStatus(success ? "SUCCESS" : "FAILED");

        Payment savedPayment = paymentRepo.save(payment);

        // 🔹 Si paiement réussi, mettre à jour le statut de la réservation
        if (success) {
            reservation.setStatus(ReservationStatus.PAID);
            reservationRepo.save(reservation);
        }
        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new RuntimeException("This reservation is already paid");
        }


        return savedPayment;
    }

}
