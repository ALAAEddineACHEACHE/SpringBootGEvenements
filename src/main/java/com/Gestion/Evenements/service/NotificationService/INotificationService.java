package com.Gestion.Evenements.service.NotificationService;

import com.Gestion.Evenements.models.Payment;
import com.Gestion.Evenements.models.Reservation;

public interface INotificationService {
    void sendReservationEmail(Reservation reservation, String toEmail);
    void sendPaymentEmail(Payment payment, String toEmail);
}
