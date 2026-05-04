package com.example.inventory.repository;

import com.example.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Inventory entity.
 * Provides standard CRUD operations and custom query methods for inventory.
 */
@Repository // Marks this interface as a Spring Data repository bean
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Custom query method automatically implemented by Spring Data JPA.
     * It parses the method name "findByProductId" to generate the SQL query:
     * SELECT * FROM inventory WHERE product_id = ?
     */
    Optional<Inventory> findByProductId(Long productId);
}
