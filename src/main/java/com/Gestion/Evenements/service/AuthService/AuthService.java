package com.Gestion.Evenements.service.AuthService;

import com.Gestion.Evenements.dto.AuthResponse;
import com.Gestion.Evenements.dto.LoginRequest;
import com.Gestion.Evenements.dto.MessageResponse;
import com.Gestion.Evenements.dto.RegisterRequest;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.repo.UserRepository;


import com.Gestion.Evenements.service.EmailService;
import com.Gestion.Evenements.utils.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService; // 🔥 À ajouter

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) throws MessagingException {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String verificationCode = generateVerificationCode();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setEnabled(false);
        user.setVerificationCode(verificationCode);

        userRepository.save(user);

        emailService.sendVerificationEmail(request.getEmail(), verificationCode);

        // Crée l'objet AuthResponse
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwtUtils.generateRegistrationToken(user.getEmail(), "REGISTER_TOKEN"));
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setRoles(user.getRoles());

        return authResponse;
    }

    @Transactional
    @Override
    public MessageResponse verifyAccount(String verificationCode, String email) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getVerificationCode().equals(verificationCode)) {
            throw new Exception("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        return new MessageResponse("Account verified successfully");
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRoles());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }


}
