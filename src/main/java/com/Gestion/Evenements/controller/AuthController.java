package com.Gestion.Evenements.controller;

import annotations.ToVerifyAccountOnly;
import com.Gestion.Evenements.dto.*;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.AuthService.IAuthService;
import com.Gestion.Evenements.service.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final IAuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) throws MessagingException {
        return authService.register(request);
    }

    @ToVerifyAccountOnly
    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verifyAccount(
            @Valid @RequestBody VerifyRequest request
    ) throws Exception {

        // On utilise directement l'email du body de la requête
        var response = authService.verifyAccount(
                request.getVerificationCode(),
                request.getEmail()
        );

        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @GetMapping("/count")
    public Long getUserCount() {
        return userRepository.count();
    }
    @GetMapping("/users")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }



}
