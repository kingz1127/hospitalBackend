package com.example.hospitalMsBackend.config;

import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${application.security.jwt.secret-key}") // Ensure this matches your application.yml
    private String secret;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // ─── STAFF TOKEN GENERATION ──────────────────────────────────────────

    /**
     * Generates a token for Staff members (Admin, Doctor, etc.)
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getRole().name());
        claims.put("type", "STAFF"); // Crucial for JwtAuthenticationFilter
        return createToken(claims, user.getUsername());
    }

    // ─── PATIENT TOKEN GENERATION ────────────────────────────────────────

    /**
     * Generates a token for Patients after OTP verification
     */
    public String generatePatientToken(Patient patient) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_PATIENT");
        claims.put("type", "PATIENT"); // Crucial for JwtAuthenticationFilter
        return createToken(claims, patient.getTokenNumber()); // Subject is HOS-XXXXX
    }

    // ─── CORE TOKEN BUILDER ──────────────────────────────────────────────

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey()) // JJWT 0.12 auto-detects HS256
                .compact();
    }

    // ─── EXTRACTION & VALIDATION ─────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}