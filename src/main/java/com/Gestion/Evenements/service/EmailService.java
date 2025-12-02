package com.Gestion.Evenements.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context; // <-- CORRECT
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    private final String FRONT_END_URL = "http://localhost:3000";

    @PostConstruct
    public void init() {
        System.out.println("EmailService initialized:");
        System.out.println("JavaMailSender: " + (emailSender != null ? "OK" : "NULL"));
        System.out.println("TemplateEngine: " + (templateEngine != null ? "OK" : "NULL"));
        System.out.println("From Email: " + fromEmail);
    }

    /**
     * Envoyer un email de vérification lors de l'inscription
     */
    public void sendVerificationEmail(String to, String verificationCode) throws MessagingException {
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("verificationUrl", FRONT_END_URL + "/verify?code=" + verificationCode + "&email=" + to);

        String emailContent = templateEngine.process("verification-email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("MR-Events - Vérification de votre compte");
        helper.setText(emailContent, true);

        emailSender.send(message);
    }

    /**
     * Email de confirmation d'activation du compte
     */
    public void sendAccountActivationEmail(String to, String firstName) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("loginUrl", FRONT_END_URL + "/login");

        String emailContent = templateEngine.process("account-activation-email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Votre compte MR-Events a été activé");
        helper.setText(emailContent, true);

        emailSender.send(message);
    }

    /**
     * Email de réinitialisation de mot de passe
     */
    public void sendPasswordResetEmail(String to, String resetToken) throws MessagingException {
        Context context = new Context();
        context.setVariable("resetUrl", FRONT_END_URL + "/reset-password?token=" + resetToken);

        String emailContent = templateEngine.process("password-reset-email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Réinitialisation de votre mot de passe - MR-Events");
        helper.setText(emailContent, true);

        emailSender.send(message);
    }

    /**
     * Email de notification pour réservation réussie
     */
    public void sendReservationConfirmationEmail(String to, String eventTitle, int quantity) throws MessagingException {
        Context context = new Context();
        context.setVariable("eventTitle", eventTitle);
        context.setVariable("quantity", quantity);

        String emailContent = templateEngine.process("reservation-confirmation-email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Confirmation de réservation - MR-Events");
        helper.setText(emailContent, true);

        emailSender.send(message);
    }

    /**
     * Email de notification pour paiement échoué
     */
    public void sendPaymentFailedEmail(String to, String eventTitle, double amount, String reason) throws MessagingException {
        Context context = new Context();
        context.setVariable("eventTitle", eventTitle);
        context.setVariable("amount", amount);
        context.setVariable("reason", reason);

        String emailContent = templateEngine.process("payment-failed-email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Paiement échoué - MR-Events");
        helper.setText(emailContent, true);

        emailSender.send(message);
    }
}
