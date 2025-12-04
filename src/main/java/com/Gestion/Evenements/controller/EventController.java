package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.EventService.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponse> all() {
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getUser().getId();
        request.setOrganizerId(userId);

        EventResponse createdEvent = eventService.create(request);

        return ResponseEntity
                .status(201)
                .body(Map.of(
                        "message", "Event created",
                        "event", createdEvent
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        request.setOrganizerId(principal.getUser().getId());
        EventResponse updatedEvent = eventService.update(id, request);

        return ResponseEntity.ok(Map.of(
                "message", "Event updated",
                "event", updatedEvent
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        eventService.delete(id);
        return ResponseEntity.ok(Map.of(
                "message", "Event deleted",
                "eventId", id
        ));
    }
}
