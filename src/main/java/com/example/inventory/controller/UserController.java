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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Any authenticated user can update their own profile
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UpdateProfileDTO request, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(request, principal.getName()));
    }

    // Only Admin and Clerk can add users
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLERK')")
    public ResponseEntity<UserResponseDTO> addUser(@Valid @RequestBody CreateUserDTO request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(request, principal.getName()));
    }

    // Only Admin can view all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Only Admin can delete users
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
