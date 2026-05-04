package com.example.inventory.repository;

import com.example.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 * Extending JpaRepository provides all standard CRUD operations (save, findById, findAll, delete, etc.) automatically.
 */
@Repository // Marks this interface as a Spring Data repository bean
public interface ProductRepository extends JpaRepository<Product, Long> {
    // No implementation is needed! Spring Data JPA dynamically generates the implementation at runtime.
}
