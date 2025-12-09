// com/Gestion/Evenements/service/PaymentService/IPaymentService.java
package com.Gestion.Evenements.service.PaymentService;

import com.Gestion.Evenements.models.Payment;

public interface IPaymentService {
    Payment pay(Long reservationId, String method, String cardNumber,
                String cardHolder, String expiryDate, String cvv,
                Double amount, Long userId);
    Payment findByReservationId(Long reservationId); // Ajoutez cette ligne

}