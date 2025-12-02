package com.Gestion.Evenements.filter;

import com.Gestion.Evenements.exception.CustomAppException;
import com.Gestion.Evenements.models.enums.TokenType;
import com.Gestion.Evenements.service.JWTService;
import com.Gestion.Evenements.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import org.example.springsecuritydemo.exception.CustomAppException;
//import org.example.springsecuritydemo.model.enums.TokenType;
//import org.example.springsecuritydemo.service.MyUserDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter  extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;
    @Autowired
    ApplicationContext context;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ Exclure les endpoints publics
        if (request.getServletPath().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Vérifier le header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "No Bearer token provided.");
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "The provided token is not valid.");
                return;
            }

            String email = jwtService.extractEmail(token);
            TokenType tokenType = jwtService.extractTokenType(token);

            UserDetails userDetails = context.getBean(MyUserDetailsService.class)
                    .loadUserByUsernameTokenType(email, tokenType, token);
            if (userDetails == null) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "User not found", "No user found for the provided token.");
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (CustomAppException e) {
            sendErrorResponse(response, e.getStatus(), e.getTitle(), e.getMessage());
            return;
        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed", "An unexpected error occurred during authentication.");
            return;
        }

        filterChain.doFilter(request, response);
    }



    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                new java.util.Date(), status.value(), error, message, "JWT Authentication"
        );

        response.getWriter().write(jsonResponse);
   }
}