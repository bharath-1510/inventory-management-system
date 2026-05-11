package com.example.inventory.repository;

import com.example.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for the User entity.
 * Provides out-of-the-box CRUD operations (save, findById, delete, etc.) via JpaRepository.
 */
@Repository // Marks this interface as a Spring Data JPA repository bean
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Custom query method automatically implemented by Spring Data JPA.
     * It generates the SQL: SELECT * FROM users WHERE email = ?
     * Used heavily in authentication to look up a user.
     */
    Optional<User> findByEmail(String email);
}
