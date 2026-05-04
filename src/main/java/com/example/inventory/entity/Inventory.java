package com.example.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents the Inventory entity mapped to the "inventory" table in the database.
 * Tracks stock quantities for products.
 */
@Entity // Specifies that this class is an entity and is mapped to a database table
@Table(name = "inventory") // Maps the entity to the "inventory" table
@Data // Lombok annotation for getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-args constructor (required by Hibernate)
@AllArgsConstructor // Generates a constructor with 1 parameter for each field
public class Inventory {

    @Id // Denotes the primary key of this entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments the ID column
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Defines a 1-to-1 relationship with Product. LAZY fetching means it won't load Product until explicitly accessed.
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, unique = true) // Foreign key to the products table
    @OnDelete(action = OnDeleteAction.CASCADE) // Tells Hibernate to cascade deletions (if Product is deleted, delete its Inventory)
    private Product product;

    @NotNull(message = "Quantity cannot be null") // Validation to prevent null values
    @Min(value = 0, message = "Quantity cannot be less than 0") // Prevents negative inventory
    @Column(nullable = false) // Database column constraint preventing nulls
    private Integer quantity;

    @NotNull(message = "Reorder level cannot be null")
    @Min(value = 0, message = "Reorder level cannot be less than 0")
    @Column(name = "reorder_level", columnDefinition = "INT DEFAULT 10") // Maps to "reorder_level" and sets a DB default of 10
    private Integer reorderLevel = 10;
}
