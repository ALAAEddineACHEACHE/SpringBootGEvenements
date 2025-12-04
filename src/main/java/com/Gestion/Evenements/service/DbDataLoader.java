package com.Gestion.Evenements.service;
import com.Gestion.Evenements.models.*;
import com.Gestion.Evenements.models.enums.*;
import com.Gestion.Evenements.repo.EventRepository;
import com.Gestion.Evenements.repo.PaymentRepository;
import com.Gestion.Evenements.repo.ReservationRepository;
import com.Gestion.Evenements.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DbDataLoader {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void loadData() {
        if (userRepository.count() > 0 || eventRepository.count() > 0 ||
                reservationRepository.count() > 0 || paymentRepository.count() > 0) {
            System.out.println("⚠ Data already exists, skipping data loading...");
            return;
        }

        loadUsers();
        loadEvents();
        loadReservations();
        loadPayments();
    }
    private void loadUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(
                null,
                "admin",
                passwordEncoder.encode("admin123"),
                true,
                null,
                "admin@example.com",
                Set.of(Role.ROLE_ADMIN)
        ));

        users.add(new User(
                null,
                "organizer1",
                passwordEncoder.encode("organizer123"),
                true,
                null,
                "org1@example.com",
                Set.of(Role.ROLE_ORGANIZER)
        ));

        users.add(new User(
                null,
                "user1",
                passwordEncoder.encode("user123"),
                true,
                null,
                "user1@example.com",
                Set.of(Role.ROLE_USER)
        ));

        users.add(new User(
                null,
                "ALAE",
                passwordEncoder.encode("user123"),
                true,
                null,
                "user2@example.com",
                Set.of(Role.ROLE_USER)
        ));

        users.add(new User(
                null,
                "MOHAMMED",
                passwordEncoder.encode("user123"),
                true,
                null,
                "user3@example.com",
                Set.of(Role.ROLE_USER)
        ));

        userRepository.saveAll(users);
        System.out.println("✅ Users loaded");
    }

    private void loadEvents() {
        List<User> organizers = userRepository.findAllByRolesContaining(Role.ROLE_ORGANIZER);
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            User organizer = organizers.get(random.nextInt(organizers.size()));

            Event event = new Event();
            event.setTitle("Event " + i);
            event.setDescription("Description for Event " + i);
            event.setLocation("City " + i);
            event.setStartAt(LocalDateTime.now().plusDays(random.nextInt(30)));
            event.setEndAt(LocalDateTime.now().plusDays(random.nextInt(31) + 1));
            event.setOrganizerId(organizer.getId());
            event.setTotalTickets(50 + random.nextInt(100));
            event.setTicketPrice(20 + random.nextDouble() * 80);
            eventRepository.save(event);
        }

        System.out.println("✅ Events loaded");
    }

    private void loadReservations() {
        List<User> users = userRepository.findAllByRolesContaining(Role.ROLE_USER);
        List<Event> events = eventRepository.findAll();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            User user = users.get(random.nextInt(users.size()));
            Event event = events.get(random.nextInt(events.size()));

            Reservation reservation = new Reservation();
            reservation.setEventId(event.getId());
            reservation.setUserId(user.getId());
            reservation.setQuantity(1 + random.nextInt(5));
            reservation.setTotalAmount(event.getTicketPrice() * reservation.getQuantity());
            reservation.setReservedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            reservation.setStatus(ReservationStatus.PENDING);

            reservationRepository.save(reservation);
        }

        System.out.println("✅ Reservations loaded");
    }

        private void loadPayments() {
            List<Reservation> reservations = reservationRepository.findAll();

            for (Reservation res : reservations) {

                Payment payment = Payment.builder()
                        .reservation(res)  // Relation directe
                        .amount(res.getTotalAmount())
                        .status(res.getStatus() == ReservationStatus.PAID ? "SUCCESS" : "FAILED")
                        .method("SIMULATED")  // ou "CARD"
                        .paidAt(LocalDateTime.now())
                        .build();

                paymentRepository.save(payment);
            }

            System.out.println("✅ Payments loaded");
        }


    }

