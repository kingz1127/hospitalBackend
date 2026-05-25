package com.example.hospitalMsBackend.config;

import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.Role;
import com.example.hospitalMsBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = User.builder()
                    .username("superadmin")
                    .fullName("System Super Admin")
                    .email("kingzornaments@gmail.com")
                    .password(passwordEncoder.encode("Temporary123!")) // Temp password
                    .role(Role.SUPER_ADMIN)
                    .isActive(true)
                    .needsPasswordReset(true)
                    .build();
            userRepository.save(superAdmin);
            System.out.println("SuperAdmin account created: superadmin / Temporary123!");
        }
    }
}