package com.Gestion.Evenements.service.NotificationService;

import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Lire un fichier HTML dans /templates/
    private String loadHtmlTemplate(String fileName) {
        try {
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("templates/" + fileName);

            if (inputStream == null) {
                throw new RuntimeException("Template not found: " + fileName);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Error loading HTML template: " + fileName);
        }
    }

    // Remplacer les variables ${variable}
    private String replaceVariables(String html, Map<String, Object> vars) {
        String result = html;
        for (String key : vars.keySet()) {
            result = result.replace("${" + key + "}", String.valueOf(vars.get(key)));
        }
        return result;
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // === RESERVATION EMAIL ===
    public void sendReservationEmail(Reservation reservation, String userName, String toEmail) {

        String html = loadHtmlTemplate("Reservation-Confirmation.html");

        Map<String, Object> vars = Map.of(
                "userName", userName,
                "reservationId", reservation.getId(),
                "eventId", reservation.getEventId(),
                "quantity", reservation.getQuantity(),
                "totalAmount", reservation.getTotalAmount(),
                "status", reservation.getStatus()
        );

        html = replaceVariables(html, vars);

        sendHtmlEmail(toEmail, "Reservation Confirmation", html);
    }

    // === PAYMENT EMAIL ===
    public void sendPaymentEmail(Payment payment, String userName, String toEmail) {

        String html = loadHtmlTemplate("Payment-Confirmation.html");

        Map<String, Object> vars = Map.of(
                "userName", userName,
                "reservationId", payment.getReservation().getId(),
                "amount", payment.getAmount(),
                "method", payment.getMethod(),
                "status", payment.getStatus()
        );

        html = replaceVariables(html, vars);

        sendHtmlEmail(toEmail, "Payment Confirmation", html);
    }
}
