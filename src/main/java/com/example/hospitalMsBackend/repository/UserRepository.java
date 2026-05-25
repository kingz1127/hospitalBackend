package com.example.hospitalMsBackend.repository;

import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleAndIsActiveTrue(Role role);
    Optional<User> findByResetToken(String token);
    Optional<User> findByEmail(String email);

    void deleteById(Long id);
    // findById(UUID) and deleteById(UUID) are inherited
}