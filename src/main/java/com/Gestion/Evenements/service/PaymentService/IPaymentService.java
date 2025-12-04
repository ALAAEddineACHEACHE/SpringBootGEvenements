package com.Gestion.Evenements.service.PaymentService;

import com.Gestion.Evenements.models.Payment;

public interface IPaymentService {
    Payment pay(Long reservationId, String method, Long userId);
}
