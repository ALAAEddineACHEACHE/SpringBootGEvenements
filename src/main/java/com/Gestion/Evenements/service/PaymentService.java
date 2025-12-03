package com.Gestion.Evenements.service;

import com.Gestion.Evenements.models.*;
import com.Gestion.Evenements.models.enums.PaymentStatus;
import com.Gestion.Evenements.models.enums.ReservationStatus;
import com.Gestion.Evenements.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {}
//    private final ReservationRepository reservationRepository;
//    private final EventRepository eventRepository;
//    private final PaymentRepository paymentRepository;
//
//    @Transactional
//    public Payment simulatePayment(Long reservationId, boolean success) {
//        Reservation r = reservationRepository.findById(reservationId).orElseThrow();
//        if (r.getStatus() == ReservationStatus.PAID) throw new RuntimeException("Already paid");
//
//        Event e = eventRepository.findById(r.getEventId()).orElseThrow();
//        BigDecimal amount = BigDecimal.valueOf(r.getQuantity() * e.getTicketPrice());
//
//        Payment p = Payment.builder()
//                .reservationId(r.getId())
//                .amount(amount)
//                .provider("SIMULATED")
//                .status(PaymentStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .build();
//        p = paymentRepository.save(p);
//
//        if (success) {
//            r.setStatus(ReservationStatus.PAID);
//            reservationRepository.save(r);
//
//            p.setStatus(PaymentStatus.SUCCESS);
//            p.setUpdatedAt(LocalDateTime.now());
//            paymentRepository.save(p);
//
//           // if (NotificationService != null) notificationService.notifyPayment(r.getUserId(), r.getId(), true);
//        } else {
//            r.setStatus(ReservationStatus.FAILED);
//            reservationRepository.save(r);
//
//            p.setStatus(PaymentStatus.FAILED);
//            p.setFailureReason("Simulated failure");
//            p.setUpdatedAt(LocalDateTime.now());
//            paymentRepository.save(p);
//
//            // release tickets
//            e.setTicketsSold(Math.max(0, e.getTicketsSold() - r.getQuantity()));
//            eventRepository.save(e);
//
//           // if (notificationService != null) notificationService.notifyPayment(r.getUserId(), r.getId(), false);
//       // }
//        //return p;
//    //}
//}

