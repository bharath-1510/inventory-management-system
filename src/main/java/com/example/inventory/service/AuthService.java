package com.example.inventory.service;

import com.example.inventory.dto.AuthRequestDTO;
import com.example.inventory.dto.AuthResponseDTO;
import com.example.inventory.dto.UserResponseDTO;
import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import com.example.inventory.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service class handling Authentication operations.
 * Responsible for verifying user credentials and generating JWT tokens.
 */
@Service // Registers this as a Spring Service Bean
@RequiredArgsConstructor // Automatically generates a constructor for the injected final dependencies
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Spring's central interface for authentication

    /**
     * Authenticates a user and returns a signed JWT token along with user details.
     * @param request Contains the user's email and plaintext password
     * @return AuthResponseDTO containing the JWT token and user info
     */
    public AuthResponseDTO login(AuthRequestDTO request) {
        // Authenticate the user against the database.
        // This internally uses our CustomUserDetailsService to load the user and 
        // our BCryptPasswordEncoder to verify if the provided password matches the hash.
        // If credentials are bad, it throws an AuthenticationException (returns 401/403).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If execution reaches here, the user's credentials are correct.
        // Fetch the user entity from the DB to get their details (like ID, Role, etc.)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate a new JWT token using the user's email (Username)
        String jwtToken = jwtService.generateToken(user);

        // Convert the raw User entity to a clean UserResponseDTO
        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        // Return the final response containing both the token and the user's profile
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .user(userResponse)
                .build();
    }
}
