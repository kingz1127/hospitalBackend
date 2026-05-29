package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.CreateStaffRequest;
import com.example.hospitalMsBackend.model.dto.request.UpdateProfileRequest;
import com.example.hospitalMsBackend.model.dto.response.UserResponse;
import com.example.hospitalMsBackend.repository.UserRepository;
import com.example.hospitalMsBackend.service.AuthService;
import com.example.hospitalMsBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Staff Management and Profile Updates")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthService authService;

    // 1. LIST ALL STAFF WITH PAGINATION (SUPER_ADMIN ONLY)
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Super Admin views all staff members (Paginated)")
    public ResponseEntity<Page<UserResponse>> getAllStaff(@ParameterObject Pageable pageable) { // Added @ParameterObject
        Page<UserResponse> page = userRepository.findAll(pageable)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .email(user.getEmail())
                        .isActive(user.getIsActive())
                        .createdAt(user.getCreatedAt())
                        .build());
        return ResponseEntity.ok(page);
    }

    // 2. CREATE STAFF (SUPER_ADMIN ONLY)
    // This replaces the old /signup. It generates a password and emails it.
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Super Admin creates a new staff account (Generates UUID/Pass and emails user)")
    public ResponseEntity<UserResponse> createStaff(@RequestBody CreateStaffRequest request) {
        return ResponseEntity.ok(authService.registerStaff(request));
    }

    // 3. UPDATE PROFILE & PICK IMAGE (PATCH)
    // Combined text fields and file upload using @ModelAttribute
    @PatchMapping(value = "/update-me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Staff updates profile and picks an image file in one form")
    public ResponseEntity<?> updateMe(
            Authentication authentication,
            @ModelAttribute UpdateProfileRequest request) {

        userService.updateMyProfile(authentication.getName(), request);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    // 4. DELETE USER (SUPER_ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Super Admin deletes a staff member")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // 5. UPDATE ROLE (SUPER_ADMIN ONLY)
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Super Admin changes a staff member's role")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody String newRole) {
        userService.updateUserRole(id, newRole);
        return ResponseEntity.ok(Map.of("message", "Role updated successfully"));
    }
}