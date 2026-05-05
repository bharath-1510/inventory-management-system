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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(AuthRequestDTO request) {
        // Authenticate the user. This will throw an exception if credentials are bad
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Fetch user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);

        // Convert User to UserResponseDTO
        UserResponseDTO userResponse = UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .user(userResponse)
                .build();
    }
}
