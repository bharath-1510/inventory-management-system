package com.example.inventory.controller;

import com.example.inventory.dto.InventoryDTO;
import com.example.inventory.entity.Inventory;
import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Inventory.
 * Provides endpoints to check and update product stock levels.
 */
@RestController // Marks this class as a REST controller where every method returns a domain object instead of a view
@RequestMapping("/api/inventory") // Base URI for all inventory-related endpoints
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required arguments (final fields)
public class InventoryController {

    private final InventoryService inventoryService; // Service dependency injected by Spring

    /**
     * Helper method to convert an Inventory entity to an InventoryDTO.
     */
    private InventoryDTO convertToDTO(Inventory inventory) {
        return new InventoryDTO(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getQuantity(),
                inventory.getReorderLevel()
        );
    }

    /**
     * Retrieves the inventory details for a specific product.
     */
    @GetMapping("/product/{productId}") // Handles GET requests to "/api/inventory/product/{productId}"
    public ResponseEntity<InventoryDTO> getInventory(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(convertToDTO(inventory));
    }

    /**
     * Updates the stock for a product by adding or subtracting a given quantity.
     */
    @PutMapping("/product/{productId}/stock") // Handles PUT requests to update stock
    public ResponseEntity<InventoryDTO> updateStock(
            @PathVariable Long productId, // Extracts the productId from the URL path
            @RequestParam Integer quantityToAdd) { // Extracts the quantityToAdd from query parameters
        Inventory updatedInventory = inventoryService.updateStock(productId, quantityToAdd);
        return ResponseEntity.ok(convertToDTO(updatedInventory));
    }

    /**
     * Checks if a product's stock has fallen to or below its reorder level.
     */
    @GetMapping("/product/{productId}/is-low-stock")
    public ResponseEntity<Boolean> isLowStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.isLowStock(productId));
    }

    /**
     * Checks if a product has enough available stock to fulfill a requested quantity.
     */
    @GetMapping("/product/{productId}/is-available")
    public ResponseEntity<Boolean> isAvailable(
            @PathVariable Long productId,
            @RequestParam Integer requiredQuantity) {
        return ResponseEntity.ok(inventoryService.isAvailable(productId, requiredQuantity));
    }
}
