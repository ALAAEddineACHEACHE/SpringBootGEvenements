package com.Gestion.Evenements.controller;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.EventService.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    @GetMapping
    public List<EventResponse> all() {
        return eventService.getAll();
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable Long id) {
        return eventService.getById(id);
    }

    @PostMapping
    public EventResponse create(@RequestBody EventRequest request,
                                @AuthenticationPrincipal UserDetails principal) {

        // Récupérer l'ID du user connecté
        Long userId = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        request.setOrganizerId(userId);
        return eventService.create(request);
    }


    @PutMapping("/{id}")
    public EventResponse update(@PathVariable Long id,
                                @RequestBody EventRequest request,
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        request.setOrganizerId(getUserIdFromPrincipal(principal));
        return eventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }
    // Méthode utilitaire pour récupérer l'id de l'utilisateur connecté
    private Long getUserIdFromPrincipal(org.springframework.security.core.userdetails.User principal) {
        // Ici, tu dois récupérer l’utilisateur depuis la base avec principal.getUsername()
        // et renvoyer son ID
        return 1L; // exemple temporaire, à remplacer par la vraie logique
    }
}
