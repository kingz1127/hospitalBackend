//package com.example.hospitalMsBackend.repository;
//
//import com.example.hospitalMsBackend.model.entity.User;
//import com.example.hospitalMsBackend.model.enums.Role;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, UUID> {
//    Optional<User> findByUsername(String username);
//    boolean existsByUsername(String username);
//    List<User> findByRoleAndIsActiveTrue(Role role);
//    Optional<User> findByResetToken(String token);
//    Optional<User> findByEmail(String email);
//
//    void deleteById(Long id);
//
//
//    Optional<User> findByUsernameIgnoreCase(String username);
//
//    boolean existsByUsernameIgnoreCase(String username);
//    boolean existsByEmailIgnoreCase(String email);
//    Optional<User> findByEmailIgnoreCase(String email);
//    // findById(UUID) and deleteById(UUID) are inherited
//}

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

    // --- Case-Insensitive methods for Security and Registration ---
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    // --- Password Reset ---
    Optional<User> findByResetToken(String token);

    // --- Staff Management ---
    List<User> findByRoleAndIsActiveTrue(Role role);

    boolean existsByUsername(String superadmin);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);




    // findById(UUID) and deleteById(UUID) are inherited

    // NOTE: findById(UUID) and deleteById(UUID) are already provided
    // by JpaRepository<User, UUID>.
    // NEVER add a method using 'Long id' here, as it conflicts with the UUID type.
}