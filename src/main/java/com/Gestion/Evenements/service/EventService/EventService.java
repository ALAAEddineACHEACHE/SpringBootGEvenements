package com.Gestion.Evenements.service.EventService;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.repo.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return toResponse(event);
    }

    public EventResponse create(EventRequest request) {
        Event event = toEntity(request);
        event.setTicketsSold(0);
        return toResponse(eventRepository.save(event));
    }

    public EventResponse update(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setOrganizerId(request.getOrganizerId());
        event.setTotalTickets(request.getTotalTickets());
        event.setTicketPrice(request.getTicketPrice());

        return toResponse(eventRepository.save(event));
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    // Méthodes de conversion Event <-> DTO
    private EventResponse toResponse(Event event) {
        EventResponse resp = new EventResponse();
        resp.setId(event.getId());
        resp.setTitle(event.getTitle());
        resp.setDescription(event.getDescription());
        resp.setLocation(event.getLocation());
        resp.setStartAt(event.getStartAt());
        resp.setEndAt(event.getEndAt());
        resp.setOrganizerId(event.getOrganizerId());
        resp.setTotalTickets(event.getTotalTickets());
        resp.setTicketsSold(event.getTicketsSold());
        resp.setTicketPrice(event.getTicketPrice());
        resp.setTicketsRemaining(event.getTotalTickets() - event.getTicketsSold());
        return resp;
    }

    private Event toEntity(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setOrganizerId(request.getOrganizerId());
        event.setTotalTickets(request.getTotalTickets());
        event.setTicketPrice(request.getTicketPrice());
        return event;
    }
    public void reserveTicket(Long eventId, int quantity) {
        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getTotalTickets() - event.getTicketsSold() < quantity) {
            throw new RuntimeException("Not enough tickets");
        }
        event.setTicketsSold(event.getTicketsSold() + quantity);
        eventRepository.save(event);
    }

}
