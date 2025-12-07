package com.Gestion.Evenements.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long organizerId; // user id of organizer
    private int totalTickets;
    private int ticketsSold = 0;
    private double ticketPrice;
    private String category;
    private String imageUrl;
}
