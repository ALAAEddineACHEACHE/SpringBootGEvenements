//package com.Gestion.Evenements.utils;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.Set;
//
//@Component
//public class JwtUtils {
//
////    // LA MÊME clé utilisée partout
////    private static final String SECRET_KEY =
////            "test1234567890test1234567890test1234567890test1234567890";
//
//    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h
//
//    // clé convertie (pas Base64)
//
//    public String generateToken(String username, Set<?> roles) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("roles", roles)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//}
