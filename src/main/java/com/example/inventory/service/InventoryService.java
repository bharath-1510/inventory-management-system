package com.example.inventory.service;

import com.example.inventory.entity.Inventory;
import com.example.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Inventory operations.
 * Handles stock updates, availability checks, and low-stock alerts.
 */
@Service // Registers this class as a Spring Service bean
@Slf4j // Injects a logger instance for logging events
@RequiredArgsConstructor // Auto-generates a constructor injecting the required final dependencies
public class InventoryService {

    private final InventoryRepository inventoryRepository; // Interacts with the Inventory table in the database

    /**
     * Helper method to fetch the inventory for a given product ID.
     */
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product id: " + productId));
    }

    /**
     * Updates the stock quantity for a product. Positive quantities add stock, negative reduce it.
     */
    @Transactional // Ensures the stock update is atomic
    public Inventory updateStock(Long productId, Integer quantityToAdd) {
        Inventory inventory = getInventoryByProductId(productId);
        int newQuantity = inventory.getQuantity() + quantityToAdd;
        
        // Prevent stock from going below zero
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock. Cannot reduce stock below 0.");
        }
        
        inventory.setQuantity(newQuantity);
        return inventoryRepository.save(inventory); // Save the updated stock back to the database
    }

    /**
     * Checks if a product's stock has fallen to or below its defined reorder level.
     */
    public boolean isLowStock(Long productId) {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getQuantity() <= inventory.getReorderLevel();
    }

    /**
     * Checks if there is enough stock available to meet the requested quantity.
     */
    public boolean isAvailable(Long productId, Integer requiredQuantity) {
        Inventory inventory = getInventoryByProductId(productId);
        return inventory.getQuantity() >= requiredQuantity;
    }
}
