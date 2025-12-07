package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.models.UserPrincipal;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.service.EventService.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        if (!principal.getUser().getRoles().contains(Role.ROLE_ORGANIZER)) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "Only organizers can create events"
            ));
        }

        request.setOrganizerId(principal.getUser().getId());
        EventResponse event = eventService.create(request);

        return ResponseEntity.status(201).body(Map.of(
                "message", "Event created successfully",
                "event", event
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

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadOrUpdateImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (!principal.getUser().getRoles().contains(Role.ROLE_ORGANIZER)) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "Only organizers can upload/update images"
            ));
        }

        EventResponse updatedEvent = eventService.updateImage(id, image);

        return ResponseEntity.ok(Map.of(
                "message", "Image uploaded/updated successfully",
                "event", updatedEvent
        ));
    }

}
