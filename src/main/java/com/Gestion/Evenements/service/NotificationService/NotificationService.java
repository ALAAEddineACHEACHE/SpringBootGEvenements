package com.Gestion.Evenements.service.NotificationService;

import com.Gestion.Evenements.models.Reservation;
import com.Gestion.Evenements.models.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final JavaMailSender mailSender;

    // Notification après réservation
    public void sendReservationEmail(Reservation reservation, String toEmail) {
        String subject = "Reservation Confirmed!";
        String text = String.format("Your reservation #%d for event #%d has been created.\n" +
                        "Quantity: %d\nTotal: %.2f\nStatus: %s",
                reservation.getId(),
                reservation.getEventId(),
                reservation.getQuantity(),
                reservation.getTotalAmount(),
                reservation.getStatus()
        );

        sendEmail(toEmail, subject, text);
    }

    // Notification après paiement
    public void sendPaymentEmail(Payment payment, String toEmail) {
        String subject = "Payment " + payment.getStatus();
        String text = String.format("Your payment for reservation #%d has been processed.\n" +
                        "Amount: %.2f\nMethod: %s\nStatus: %s",
                payment.getReservation().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus()
        );

        sendEmail(toEmail, subject, text);
    }

    // Méthode privée pour envoyer l'email
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
