package com.Gestion.Evenements.service.ReservationService;

import com.Gestion.Evenements.models.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation createReservation(Long eventId, Long userId, int quantity);
    List<Reservation> getUserReservations(Long userId);
    List<Reservation> getEventReservations(Long eventId);
}
