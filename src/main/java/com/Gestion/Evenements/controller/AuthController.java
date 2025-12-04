package com.Gestion.Evenements.controller;

import annotations.ToVerifyAccountOnly;
import com.Gestion.Evenements.dto.*;
import com.Gestion.Evenements.service.AuthService.IAuthService;
import com.Gestion.Evenements.service.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final IAuthService authService;
    private final UserService userService;

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


}
