package com.Gestion.Evenements.service.EventService;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;

import java.util.List;

public interface IEventService {
    List<EventResponse> getAll();
    EventResponse getById(Long id);
    EventResponse create(EventRequest request);
    EventResponse update(Long id, EventRequest request);
    void delete(Long id);
    void reserveTicket(Long eventId, int quantity);
}
