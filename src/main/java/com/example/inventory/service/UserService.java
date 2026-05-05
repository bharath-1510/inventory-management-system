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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO addUser(CreateUserDTO request, String currentUsername) {
        // Fetch current user to check role logic
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Role enforcement
        if (currentUser.getRole() == Role.CLERK) {
            if (request.getRole() != Role.CUSTOMER) {
                throw new RuntimeException("Clerks can only create CUSTOMER accounts");
            }
        } else if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Unauthorized to create users");
        }

        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

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

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

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
