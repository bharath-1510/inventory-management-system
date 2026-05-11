package com.example.inventory.controller;

import com.example.inventory.dto.CreateUserDTO;
import com.example.inventory.dto.UpdateProfileDTO;
import com.example.inventory.dto.UserResponseDTO;
import com.example.inventory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for User Management.
 * Handles adding users, updating profiles, viewing all users, and deleting users.
 * Endpoints are secured using Method-Level Security (@PreAuthorize).
 */
@RestController // Defines this class as a Spring REST Controller
@RequestMapping("/api/users") // Base URL for all user-related endpoints
@RequiredArgsConstructor // Automatically generates a constructor for the injected UserService
public class UserController {

    private final UserService userService;

    /**
     * Endpoint to allow any authenticated user to update their own profile details.
     * @param request The data (name, mobile) to update
     * @param principal The currently logged-in user, automatically injected by Spring Security
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO request, Principal principal) {
        // principal.getName() returns the email (the username in our context)
        return ResponseEntity.ok(userService.updateProfile(request, principal.getName()));
    }

    /**
     * Endpoint to add a new user. 
     * @PreAuthorize restricts access: Only users with the ADMIN or CLERK role can hit this endpoint.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')") // Spring Security checks the JWT roles before allowing execution
    public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody CreateUserDTO request, Principal principal) {
        // We pass the currently logged-in user's email so the service can check if they have permission to create the specific requested role
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(request, principal.getName()));
    }

    /**
     * Endpoint to get a list of all users in the system.
     * @PreAuthorize ensures that only ADMIN users can access this data.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs allowed
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Endpoint to delete a user by ID.
     * @PreAuthorize ensures that only ADMIN users can perform deletions.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs allowed
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }
}
