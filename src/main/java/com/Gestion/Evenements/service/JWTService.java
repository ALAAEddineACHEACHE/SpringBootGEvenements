package com.Gestion.Evenements.service;

import com.Gestion.Evenements.exception.CustomAppException;
import com.Gestion.Evenements.models.ExpiredToken;
import com.Gestion.Evenements.models.enums.Role;
import com.Gestion.Evenements.models.enums.TokenType;
import com.Gestion.Evenements.repo.ExpiredTokenRepo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class JWTService {

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // EXPIRATIONS
    // 24 hours = 24 * 60 * 60 * 1000 = 86_400_000 ms
    private static final long TOKEN_EXP_24H = 86_400_000L;

    // 30 days = 30 * 24 * 60 * 60 * 1000 = 2_592_000_000 ms
    private static final long TOKEN_EXP_30D = 2_592_000_000L;
    // 24 hours = 86_400_000 ms
    private static final long RESET_PASSWORD_TOKEN = 3600_000L;

    private final ExpiredTokenRepo expiredTokenRepo;

    private final Map<String, Boolean> tokenBlacklistCache = new HashMap<>();


    // ------------------ TOKEN VALIDATION ------------------
    public boolean isTokenValid(String token) {
        if (isTokenBlacklisted(token)) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Token revoked",
                    "This token has been revoked. Please log in again."
            );
        }

        final String email = extractEmail(token);
        final TokenType tokenType = extractTokenType(token);

        return !isTokenExpired(token) && email != null && tokenType != null;
    }

    public boolean isTokenBlacklisted(String token) {
        if (tokenBlacklistCache.containsKey(token)) {
            return tokenBlacklistCache.get(token);
        }

        boolean isBlacklisted = expiredTokenRepo.existsByToken(token);
        tokenBlacklistCache.put(token, isBlacklisted);

        return isBlacklisted;
    }


    public void addToBlacklist(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String email = claims.getSubject();
            TokenType tokenType = TokenType.valueOf(claims.get("type", String.class));
            Date expirationDate = claims.getExpiration();

            ExpiredToken expiredToken = new ExpiredToken(token, tokenType, email, expirationDate);
            expiredTokenRepo.save(expiredToken);

            tokenBlacklistCache.put(token, true);
        } catch (Exception e) {
            throw new CustomAppException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid token",
                    "Cannot blacklist invalid token."
            );
        }
    }


    // ------------------ TOKEN GENERATION ------------------

    public String generateRegistrationToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.REGISTER_TOKEN);

        return createToken(claims, email, TOKEN_EXP_24H);
    }


    /**
     * Generate token based on ROLE (Admin, Organizer, User)
     */
    public String generateAccessToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.ACCESS_TOKEN);
        claims.put("role", role.name());

        long expiration = switch (role) {
            case ROLE_ADMIN -> TOKEN_EXP_24H;
            case ROLE_ORGANIZER -> TOKEN_EXP_24H;
            case ROLE_USER -> TOKEN_EXP_30D; // normal user → token long
            default -> TOKEN_EXP_24H;
        };

        return createToken(claims, email, expiration);
    }


    public String generateResetPasswordToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.RESET_PASSWORD_TOKEN);

        return createToken(claims, email, TOKEN_EXP_24H);
    }


    // Generic createToken
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // ------------------ EXTRACT DATA ------------------
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public TokenType extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        String typeValue = claims.get("type", String.class);

        return TokenType.valueOf(typeValue);
    }


    // ------------------ CHECKS ------------------
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new CustomAppException(HttpStatus.UNAUTHORIZED, "Token expired", "Your token has expired.");
        } catch (Exception e) {
            throw new CustomAppException(HttpStatus.UNAUTHORIZED, "Invalid token", "Failed to parse token.");
        }
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
