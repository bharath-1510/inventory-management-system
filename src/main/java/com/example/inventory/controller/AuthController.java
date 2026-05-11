package com.example.inventory.controller;

import com.example.inventory.dto.AuthRequestDTO;
import com.example.inventory.dto.AuthResponseDTO;
import com.example.inventory.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing authentication (login).
 * This controller is intentionally open to the public in SecurityConfig so users can log in.
 */
@RestController // Marks this as a REST Controller returning JSON
@RequestMapping("/api/auth") // Base URI
@RequiredArgsConstructor // Auto-injects AuthService
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint to authenticate a user and generate a JWT token.
     * @param request Contains the user's email and password, validated by @Valid
     * @return AuthResponseDTO containing the JWT and user profile
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        // Delegate authentication logic to the AuthService
        return ResponseEntity.ok(authService.login(request));
    }
}
