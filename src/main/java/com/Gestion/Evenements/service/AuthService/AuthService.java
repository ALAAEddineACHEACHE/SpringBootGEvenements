package com.Gestion.Evenements.service.AuthService;

import com.Gestion.Evenements.dto.AuthResponse;
import com.Gestion.Evenements.dto.LoginRequest;
import com.Gestion.Evenements.dto.MessageResponse;
import com.Gestion.Evenements.dto.RegisterRequest;
import com.Gestion.Evenements.models.User;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.repo.UserRepository;
import com.Gestion.Evenements.service.EmailService;
import com.Gestion.Evenements.service.JWTService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;   // 🔥 ON UTILISE LE NOUVEAU SERVICE
    private final EmailService emailService;

    // ----------------- REGISTER -----------------

//    @Transactional
//    @Override
//    public AuthResponse register(RegisterRequest request) throws MessagingException {
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        String verificationCode = generateVerificationCode();
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRoles(Set.of(Role.ROLE_USER));
//        user.setEnabled(false);
//        user.setVerificationCode(verificationCode);
//
//        userRepository.save(user);
//
//        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
//
//        // Création du token d’inscription via JWTService
//        String registrationToken = jwtService.generateRegistrationToken(user.getEmail());
//
//        return new AuthResponse(
//                registrationToken,
//                user.getUsername(),
//                user.getEmail(),
//                user.getRoles()
//        );
//    }
@Transactional
@Override
public AuthResponse register(RegisterRequest request) throws MessagingException {

    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("Email already exists");
    }

    if (userRepository.existsByUsername(request.getUsername())) {
        throw new RuntimeException("Username already exists");
    }

    // --------- Sécurité : éviter que n'importe qui devienne ADMIN ----------
    Role requestedRole = request.getRole();
    if (requestedRole == null) {
        requestedRole = Role.ROLE_USER; // Default
    } else if (requestedRole == Role.ROLE_ADMIN) {
        throw new RuntimeException("You cannot create an admin account manually");
    }

    // -------- Création utilisateur ----------
    String verificationCode = generateVerificationCode();

    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRoles(Set.of(requestedRole));   // ⭐ Role choisi
    user.setEnabled(false);
    user.setVerificationCode(verificationCode);

    userRepository.save(user);

    // -------- Email verification ----------
    emailService.sendVerificationEmail(request.getEmail(), verificationCode);

    String registrationToken = jwtService.generateRegistrationToken(user.getEmail());

    return new AuthResponse(
            registrationToken,
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
    );
}


    // -------------------- VERIFY ACCOUNT -----------------------

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


    // ----------------- LOGIN -----------------

//    @Override
//    public AuthResponse login(LoginRequest request) {
//
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//        // Token 100% via JWTService
//        String accessToken = jwtService.generateAccessToken(user.getEmail(), Role.ROLE_USER);
//
//        return new AuthResponse(
//                accessToken,
//                user.getUsername(),
//                user.getEmail(),
//                user.getRoles()
//        );
//    }
@Override
public AuthResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid credentials");
    }

    // Correct : inclure le vrai rôle de l'utilisateur
    String token = jwtService.generateAccessToken(
            user.getEmail(),
            user.getRoles().iterator().next() // Simplification (1 rôle)
    );

    return new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
    );
}


    // ------------------ UTIL ------------------

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
}
