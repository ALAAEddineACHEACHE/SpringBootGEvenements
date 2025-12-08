package com.Gestion.Evenements.service.EventService;

import com.Gestion.Evenements.dto.EventRequest;
import com.Gestion.Evenements.dto.EventResponse;
import com.Gestion.Evenements.models.Event;
import com.Gestion.Evenements.repo.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventRepository eventRepository;
    private final String uploadDir = "uploads";
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
        event.setCategory(request.getCategory());
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
        resp.setCategory(event.getCategory());
        resp.setImageUrl(event.getImageUrl());
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
    event.setCategory(request.getCategory());

    // Si tu stockes l'image localement ou dans S3, mets le path/url ici
    if (request.getImage() != null) {
        String imagePath = "data/uploads/" + UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();
        try {
            request.getImage().transferTo(new File(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image");
        }
        event.setImageUrl(imagePath);
    }

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
//    public EventResponse updateImage(Long eventId, MultipartFile image) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//        if (image == null || image.isEmpty()) {
//            throw new RuntimeException("No image provided");
//        }
//
//        try {
//            // Chemin absolu
//            String uploadRoot = System.getProperty("user.dir") + "/uploads";
//            Path dirPath = Paths.get(uploadRoot);
//
//            // Crée le dossier si nécessaire
//            if (!Files.exists(dirPath)) {
//                Files.createDirectories(dirPath);
//            }
//
//            // Génère un nom de fichier sûr
//            String fileName = image.getOriginalFilename();
//            Path filePath = dirPath.resolve(fileName);
//
//            // Écriture réelle du fichier
//            Files.copy(image.getInputStream(), filePath);
//
//            // Met à jour l'URL/path dans l'événement
//            event.setImageUrl("/uploads/" + fileName);
//            eventRepository.save(event);
//
//            return toResponse(event);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to save image", e);
//        }
//    }

//    public EventResponse updateImage(Long eventId, MultipartFile image) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new RuntimeException("Event not found"));
//
//        if (image == null || image.isEmpty()) {
//            throw new RuntimeException("No image provided");
//        }
//
//        try {
//            // Chemin absolu
//            String uploadRoot = System.getProperty("user.dir") + "/uploads";
//            Path dirPath = Paths.get(uploadRoot);
//
//            // AJOUTER CES LOGS POUR DEBUG
//            System.out.println("Current working directory: " + System.getProperty("user.dir"));
//            System.out.println("Upload root path: " + uploadRoot);
//            System.out.println("Absolute path: " + dirPath.toAbsolutePath());
//
//            // Crée le dossier si nécessaire
//            if (!Files.exists(dirPath)) {
//                System.out.println("Creating directory: " + dirPath);
//                Files.createDirectories(dirPath);
//            }
//
//            // Vérifier les permissions
//            System.out.println("Directory exists: " + Files.exists(dirPath));
//            System.out.println("Is directory: " + Files.isDirectory(dirPath));
//            System.out.println("Is writable: " + Files.isWritable(dirPath));
//
//            // Génère un nom de fichier unique pour éviter les collisions
//            String originalFilename = image.getOriginalFilename();
//            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;
//            Path filePath = dirPath.resolve(fileName);
//
//            System.out.println("Saving file to: " + filePath);
//
//            // Écriture réelle du fichier
//            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            // Met à jour l'URL/path dans l'événement
//            event.setImageUrl("/uploads/" + fileName);
//            eventRepository.save(event);
//
//            return toResponse(event);
//
//        } catch (IOException e) {
//            e.printStackTrace(); // Afficher la stack trace complète
//            throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
//        }
//    }
public EventResponse updateImage(Long eventId, MultipartFile image) {
    Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    if (image == null || image.isEmpty()) {
        throw new RuntimeException("No image provided");
    }

    try {
        // Chemin absolu
        String uploadRoot = System.getProperty("user.dir") + "/uploads";
        Path dirPath = Paths.get(uploadRoot);

        // Crée le dossier si nécessaire
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Récupérer le nom original et le nettoyer
        String originalFilename = image.getOriginalFilename();
        String cleanFileName = originalFilename.replaceAll(".*[/\\\\]", "");
        cleanFileName = cleanFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        String baseName = "";
        String extension = "";
        int dotIndex = cleanFileName.lastIndexOf(".");

        if (dotIndex > 0) {
            baseName = cleanFileName.substring(0, dotIndex);
            extension = cleanFileName.substring(dotIndex);
        } else {
            baseName = cleanFileName;
        }

        // Vérifier si le fichier existe déjà
        Path filePath = dirPath.resolve(cleanFileName);
        int counter = 1;

        while (Files.exists(filePath)) {
            String newName = baseName + "_" + counter + extension;
            filePath = dirPath.resolve(newName);
            counter++;
        }

        // Utiliser le dernier nom déterminé
        String finalFileName = filePath.getFileName().toString();

        // Écriture du fichier
        Files.copy(image.getInputStream(), filePath);

        // Met à jour l'URL/path dans l'événement
        event.setImageUrl("/uploads/" + finalFileName);
        eventRepository.save(event);

        return toResponse(event);

    } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
    }
}
}
