package com.example.inventory.service;

import com.example.inventory.dto.CreateUserDTO;
import com.example.inventory.dto.UpdateProfileDTO;
import com.example.inventory.dto.UserResponseDTO;
import com.example.inventory.entity.Role;
import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling all business logic related to User operations.
 * Includes enforcing role-based permissions for user creation and hashing passwords.
 */
@Service // Registers this class as a Spring Service bean
@RequiredArgsConstructor // Auto-injects final dependencies like UserRepository and PasswordEncoder
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt encoder injected from SecurityConfig

    /**
     * Adds a new user to the system, enforcing business rules regarding who can create who.
     * @param request The data for the new user
     * @param currentUsername The email of the user making the request
     */
    public UserResponseDTO addUser(CreateUserDTO request, String currentUsername) {
        // Fetch current user from DB to verify their role
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Role enforcement logic:
        // 1. If the creator is a CLERK, they are ONLY allowed to create CUSTOMERs.
        if (currentUser.getRole() == Role.CLERK) {
            if (request.getRole() != Role.CUSTOMER) {
                throw new RuntimeException("Clerks can only create CUSTOMER accounts");
            }
        // 2. If the creator is NOT an ADMIN (and we know they aren't a clerk from above), reject them.
        } else if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized to create users");
        }

        // Prevent duplicate emails
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        // Build the new User entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Always hash passwords before saving!
                .mobile(request.getMobile())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser); // Return a clean DTO
    }

    /**
     * Updates the currently logged-in user's profile.
     */
    public UserResponseDTO updateProfile(UpdateProfileDTO request, String currentUsername) {
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        currentUser.setName(request.getName());
        if (request.getMobile() != null) {
            currentUser.setMobile(request.getMobile());
        }

        User savedUser = userRepository.save(currentUser);
        return mapToDTO(savedUser);
    }

    /**
     * Retrieves all users and converts them into DTOs to hide passwords.
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a user from the database.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Helper method to map a User entity to a UserResponseDTO, 
     * effectively stripping out the sensitive password field.
     */
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
