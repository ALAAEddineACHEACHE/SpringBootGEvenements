package com.Gestion.Evenements.service.AuthService;

import com.Gestion.Evenements.dto.*;
import jakarta.mail.MessagingException;

public interface IAuthService {

    AuthResponse register(RegisterRequest request) throws MessagingException;


    MessageResponse verifyAccount(String verificationCode, String email) throws Exception;

    AuthResponse login(LoginRequest request);

}
