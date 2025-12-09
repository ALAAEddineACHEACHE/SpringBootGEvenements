// com/Gestion/Evenements/service/PaymentService/PaymentService.java
package com.Gestion.Evenements.service.PaymentService;

import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.PaymentRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.NotificationService.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final ReservationRepository reservationRepo;
    private final PaymentRepository paymentRepo;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Payment pay(Long reservationId, String method, String cardNumber,
                       String cardHolder, String expiryDate, String cvv,
                       Double amount, Long userId) {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Vérifier que l'utilisateur correspond
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("You cannot pay for someone else's reservation");
        }

        // ❌ Empêcher le paiement si déjà payé
        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new RuntimeException("This reservation is already paid");
        }

        Event event = eventRepository.findById(reservation.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Calculer le montant attendu
        double expectedAmount = reservation.getQuantity() * event.getTicketPrice();

        // Vérifier que le montant correspond
        if (Math.abs(amount - expectedAmount) > 0.01) { // Tolérance de 0.01
            throw new RuntimeException(String.format(
                    "Amount mismatch. Expected: $%.2f, Received: $%.2f",
                    expectedAmount, amount
            ));
        }

        // Créer le paiement
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .paidAt(LocalDateTime.now())
                .build();

        // Simulation du paiement avec les détails de carte
        boolean success = simulateCardPayment(cardNumber, cardHolder, expiryDate, cvv, amount);
        payment.setStatus(success ? "SUCCESS" : "FAILED");

        Payment savedPayment = paymentRepo.save(payment);

        // 🔹 Si paiement réussi, mettre à jour la réservation
        if (success) {
            reservation.setStatus(ReservationStatus.PAID);
            reservationRepo.save(reservation);

            // Envoi email via notificationService
            User user = userRepository.findById(userId).orElseThrow();
            notificationService.sendPaymentEmail(savedPayment, user.getUsername(), user.getEmail());
        }

        return savedPayment;
    }

    // Méthode de simulation de paiement par carte
    private boolean simulateCardPayment(String cardNumber, String cardHolder,
                                        String expiryDate, String cvv, Double amount) {

        System.out.println("=== CARD PAYMENT SIMULATION ===");
        System.out.println("Card Number: " + maskCardNumber(cardNumber));
        System.out.println("Card Holder: " + cardHolder);
        System.out.println("Expiry Date: " + expiryDate);
        System.out.println("CVV: ***");
        System.out.println("Amount: $" + amount);
        System.out.println("==============================");

        // Simulation: 80% de chance de succès
        // Vous pouvez ajouter plus de logique ici
        boolean isTestCard = cardNumber.replace(" ", "").endsWith("4242");
        if (isTestCard) {
            System.out.println("Test card detected - Payment will succeed");
            return true;
        }

        // Pour les autres cartes, simulation aléatoire
        double successRate = 0.8; // 80% de succès
        boolean success = Math.random() < successRate;

        System.out.println("Payment simulation result: " + (success ? "SUCCESS" : "FAILED"));
        return success;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleaned = cardNumber.replace(" ", "");
        return "**** **** **** " + cleaned.substring(cleaned.length() - 4);
    }
    public Payment findByReservationId(Long reservationId) {
        List<Payment> payments = paymentRepo.findByReservationId(reservationId);
        return payments.isEmpty() ? null : payments.get(0);
    }

}