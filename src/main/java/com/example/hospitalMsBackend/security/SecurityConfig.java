package com.example.hospitalMsBackend.security;

import com.example.hospitalMsBackend.config.JwtAuthenticationFilter;
import com.example.hospitalMsBackend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Endpoints
                        .requestMatchers("/auth/**", "/public/**").permitAll()
                        .requestMatchers(
                                "/auth/**",
                                "/public/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // 2. Patient Management
                        .requestMatchers("/auth/patient/**").permitAll()
                        .requestMatchers("/portal/**").hasRole("PATIENT")
                        .requestMatchers("/patients/**").hasAnyRole("RECEPTIONIST", "ADMIN", "SUPER_ADMIN")

                        // 3. Appointment Management
                        .requestMatchers("/appointments/**").hasAnyRole("DOCTOR", "RECEPTIONIST", "ADMIN", "SUPER_ADMIN")

                        // 4. Queue Management
                        .requestMatchers("/queue/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/queue/**").hasAnyRole("RECEPTIONIST", "ADMIN", "SUPER_ADMIN")

                        // 5. User/Staff Management
                        .requestMatchers("/users/**").hasRole("SUPER_ADMIN")

                        // 6. Analytics
                        .requestMatchers("/analytics/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/analytics/doctor/**").hasRole("DOCTOR")

                        .anyRequest().authenticated()
                )
                // FIX APPLIED HERE: sessionCreationPolicy instead of setSessionCreationPolicy
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService); // ✅ pass it here
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:5173",
//                "http://localhost:3000",
//                "https://icm-web-beta.vercel.app"
//        ));

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept",
                "X-Requested-With", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}