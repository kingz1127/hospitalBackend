package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Change findByUsername to findByUsernameIgnoreCase
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Since your User entity implements UserDetails, just return it.
        // This keeps all custom fields (like fullName, id, role) inside the Security Context.
        return user;
    }
}