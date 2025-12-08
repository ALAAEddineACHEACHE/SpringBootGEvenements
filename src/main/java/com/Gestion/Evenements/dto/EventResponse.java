package com.Gestion.Evenements.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long organizerId;
    private int totalTickets;
    private int ticketsSold;
    private double ticketPrice;
    private int ticketsRemaining;
    private String category;
    private String imageUrl;
}
