package com.example.hospitalMsBackend.config;

import com.example.hospitalMsBackend.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userIdentifier;

        // 1. Skip filter if no Bearer token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userIdentifier = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // If token is malformed or signature is invalid, continue filter chain (will result in 403)
            filterChain.doFilter(request, response);
            return;
        }

        // 2. If we have an identifier and no existing authentication in context
        if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Extract 'type' claim to distinguish Patient vs Staff
            String tokenType = jwtService.extractClaim(jwt, claims -> claims.get("type", String.class));

            if ("PATIENT".equals(tokenType)) {
                /*
                   PATIENT FLOW:
                   Bypasses UserDetailsService (Staff DB).
                   Validates only token integrity and expiration.
                */
                if (!jwtService.isTokenExpired(jwt)) {
                    UserDetails patientDetails = org.springframework.security.core.userdetails.User
                            .withUsername(userIdentifier)
                            .password("") // Passwordless
                            .authorities("ROLE_PATIENT")
                            .build();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            patientDetails,
                            null,
                            patientDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } else {
                /*
                   STAFF FLOW:
                   Standard DB lookup via CustomUserDetailsService.
                */
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}