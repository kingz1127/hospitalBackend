package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.UpdateProfileRequest;
import com.example.hospitalMsBackend.model.dto.request.UpdateRoleRequest;
import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.repository.UserRepository;
import com.example.hospitalMsBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "User Controller", description = "Super admin only has access")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @GetMapping
    public List<User> getAllStaff() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createStaff(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteUser(@PathVariable UUID id) { // CHANGED TO UUID
        userRepository.deleteById(id);
    }

    @PutMapping("/update-me")
    @Operation(summary = "Logged-in staff (incl. Super Admin) updates their own profile fields and image")
    public ResponseEntity<?> updateMe(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        userService.updateMyProfile(authentication.getName(), request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Only Super Admin can change a staff member's role")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody String newRole) {
        userService.updateUserRole(id, newRole);
        return ResponseEntity.ok("User role updated successfully");
    }

    @PatchMapping("/update-me")
    public ResponseEntity<?> patchMe(Authentication authentication, @RequestBody UpdateProfileRequest request) {
        userService.updateMyProfile(authentication.getName(), request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // POST: For Picking and Uploading a File
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadProfileImage(authentication.getName(), file);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Could not upload image: " + e.getMessage());
        }
    }
}