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

    private static final String SECRET_KEY =
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    // Expirations
    private static final long EXP_24H = 86_400_000;
    private static final long EXP_30D = 2_592_000_000L;
    private static final long RESET_PASSWORD_EXP = 3_600_000;

    private final ExpiredTokenRepo expiredTokenRepo;

    private final Map<String, Boolean> tokenBlacklistCache = new HashMap<>();

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ---------------- VALIDATION ----------------

    public boolean isTokenValid(String token) {
        if (isTokenBlacklisted(token))
            throw new CustomAppException(HttpStatus.UNAUTHORIZED, "Token revoked",
                    "This token has been revoked.");

        return !isTokenExpired(token);
    }

    public boolean isTokenBlacklisted(String token) {
        if (tokenBlacklistCache.containsKey(token))
            return tokenBlacklistCache.get(token);

        boolean exists = expiredTokenRepo.existsByToken(token);
        tokenBlacklistCache.put(token, exists);
        return exists;
    }

    public void blacklistToken(String token) {
        Claims claims = extractAllClaims(token);

        ExpiredToken expired = new ExpiredToken(
                token,
                TokenType.valueOf(claims.get("type", String.class)),
                claims.getSubject(),
                claims.getExpiration()
        );

        expiredTokenRepo.save(expired);
        tokenBlacklistCache.put(token, true);
    }

    // ---------------- CREATION ----------------

    public String generateAccessToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.ACCESS_TOKEN);
        claims.put("role", role.name());

        long exp = (role == Role.ROLE_USER) ? EXP_30D : EXP_24H;

        return createToken(claims, email, exp);
    }

    public String generateRegistrationToken(String email) {
        Map<String, Object> claims = Map.of("type", TokenType.REGISTER_TOKEN);
        return createToken(claims, email, EXP_24H);
    }

    public String generateResetPasswordToken(String email) {
        Map<String, Object> claims = Map.of("type", TokenType.RESET_PASSWORD_TOKEN);
        return createToken(claims, email, RESET_PASSWORD_EXP);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // ---------------- EXTRACTION ----------------

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public TokenType extractTokenType(String token) {
        Claims claims = extractAllClaims(token);

        // 🔥 Ligne de debug à ajouter
       // System.out.println("DEBUG TOKEN TYPE RAW = " + claims.get("type"));

        return TokenType.valueOf(claims.get("type", String.class));
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Token expired",
                    "Your token has expired. Please log in again."
            );
        } catch (UnsupportedJwtException e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Unsupported token",
                    "The token format is not supported."
            );
        } catch (MalformedJwtException e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Malformed token",
                    "The token is malformed or corrupted."
            );
        } catch (SignatureException e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token signature",
                    "The token signature is invalid."
            );
        } catch (IllegalArgumentException e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token",
                    "The token is empty or null."
            );
        } catch (Exception e) {
            throw new CustomAppException(
                    HttpStatus.UNAUTHORIZED,
                    "Token validation failed",
                    "Failed to parse the token."
            );
        }
    }

}
