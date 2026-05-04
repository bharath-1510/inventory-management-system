package com.example.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the Product entity mapped to the "products" table in the database.
 */
@Entity // Marks this class as a JPA entity that maps to a database table
@Table(name = "products") // Specifies the exact table name in the database
@Data // Lombok annotation to automatically generate getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok annotation to generate a no-arguments constructor required by JPA
@AllArgsConstructor // Lombok annotation to generate an all-arguments constructor
public class Product {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells the database to auto-increment this ID
    private Long id;

    @NotBlank(message = "Product name is required") // Validation: Ensures the name is not null or whitespace
    @Size(max = 150, message = "Product name must not exceed 150 characters") // Validation: Limits the length
    @Column(nullable = false, length = 150) // Database constraint: Column cannot be null and max length is 150
    private String name;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    @Column(length = 100)
    private String category;

    @NotNull(message = "Price is required") // Validation: Ensures price is not null
    @Positive(message = "Price must be strictly positive") // Validation: Ensures price is greater than 0
    @Column(nullable = false, precision = 10, scale = 2) // Database constraint: Decimal with 10 total digits, 2 decimal places
    private BigDecimal price;

    @CreationTimestamp // Hibernate annotation to automatically set the timestamp when the record is created
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp // Hibernate annotation to automatically update the timestamp whenever the record is modified
    @Column(name = "updated_at", updatable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
