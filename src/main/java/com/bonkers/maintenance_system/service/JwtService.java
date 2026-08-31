package com.bonkers.maintenance_system.service;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public static String normalizeRole(String role) {
        if (role == null) {
            return null;
        }

        String normalizedRole = role.trim();
        if (normalizedRole.startsWith("ROLE_")) {
            return normalizedRole.substring(5);
        }

        return normalizedRole;
    }

    public String generateToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String normalizedRole = normalizeRole(role);

        return Jwts.builder()
                .subject(username)
                .claim("role", normalizedRole)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractRole(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String role = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return normalizeRole(role);
    }

    public String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}