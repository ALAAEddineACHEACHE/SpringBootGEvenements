package com.Gestion.Evenements.service;

public interface NotificationService {
    void notifyReservation(Long userId, Long reservationId);
    void notifyPayment(Long userId, Long reservationId, boolean success);
}
