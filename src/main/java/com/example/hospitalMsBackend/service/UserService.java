package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.dto.request.UpdateProfileRequest;
import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.Gender;
import com.example.hospitalMsBackend.model.enums.Role;
import com.example.hospitalMsBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateMyProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail().toLowerCase());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getNationality() != null) user.setNationality(request.getNationality());
        if (request.getGender() != null) user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());

        // Manual Base64 string update
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());

        userRepository.save(user);
    }

    @Transactional
    public void uploadProfileImage(String username, MultipartFile file) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();

        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        user.setProfileImage("data:" + file.getContentType() + ";base64," + base64Image);

        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(UUID userId, String newRole) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(Role.valueOf(newRole.toUpperCase()));
        userRepository.save(user);
    }
}