package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Represents a User in the system.
 * Implements Spring Security's UserDetails interface so Spring can use this entity for Authentication.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@Builder // Provides a fluent builder API (e.g. User.builder().name("...").build())
@NoArgsConstructor // Required by JPA
@AllArgsConstructor // Required by the Builder pattern
@Entity // Marks this as a JPA Entity
@Table(name = "users") // Maps this entity to the 'users' table in the DB
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 15)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Store the Role as a string ('ADMIN', 'CLERK') instead of a number
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp // Hibernate automatically sets this to the current time when inserted
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * Required by Spring Security. Converts our custom Role into a format Spring understands (GrantedAuthority).
     * By convention, Spring expects roles to start with "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Required by Spring Security. Returns the unique identifier for the user (we use email).
     */
    @Override
    public String getUsername() {
        return email;
    }

    // The following methods are for account expiration, locking, and disabling features.
    // We are not using these features currently, so we return true for all.

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
