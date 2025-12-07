package com.Gestion.Evenements.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class EventRequest {

    private String title;
    private String description;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long organizerId;
    private int totalTickets;
    private double ticketPrice;
    private String category;
    private MultipartFile image;
}
