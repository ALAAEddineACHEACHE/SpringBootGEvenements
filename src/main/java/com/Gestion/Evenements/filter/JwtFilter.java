package com.Gestion.Evenements.filter;

import com.Gestion.Evenements.exception.CustomAppException;
import com.Gestion.Evenements.models.enums.TokenType;
import com.Gestion.Evenements.service.JWTService;
import com.Gestion.Evenements.service.MyUserDetailsService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Incoming request: " + request.getMethod() + " " + request.getServletPath());
        System.out.println("Authorization header raw: " + request.getHeader("Authorization"));

        // 1️⃣ Ignore public endpoints
        if (request.getServletPath().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Check Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "No Bearer token provided.");
            return;
        }

        String token = authHeader.substring(7);

        if (token.trim().isEmpty()) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Token is empty.");
            return;
        }

        String email;
        TokenType tokenType;

        try {
            // validate the token first
            if (!jwtService.isTokenValid(token)) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "The provided token is not valid.");
                return;
            }

            // claims extraction with safe parsing
            email = jwtService.extractEmail(token);

            try {
                tokenType = jwtService.extractTokenType(token);
               // System.out.println("TOKEN TYPE RAW = " + jwtService.extractAllClaims(token).get("type"));
            } catch (MalformedJwtException e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Malformed JWT token.");
                return;
            } catch (UnsupportedJwtException e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Unsupported JWT token.");
                return;
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Token claims are missing or empty.");
                return;
            } catch (Exception e) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Failed to parse token type.");
                return;
            }

            // load the user
            UserDetails userDetails = context.getBean(MyUserDetailsService.class)
                    .loadUserByUsernameTokenType(email, tokenType, token);
            //System.out.println("Checking user with email: " + email + " and tokenType: " + tokenType);

            System.out.println("Authorization header: " + request.getHeader("Authorization"));

            if (userDetails == null) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "User not found", "User does not exist for this token.");
                return;
            }

            // Set authentication context
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (CustomAppException e) {
            sendErrorResponse(response, e.getStatus(), e.getTitle(), e.getMessage());
            return;
        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token", "Unexpected error processing token.");
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication set in context: " + auth);
        System.out.println("Authorities: " + (auth != null ? auth.getAuthorities() : "null"));

        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String error, String message)
            throws IOException {

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
