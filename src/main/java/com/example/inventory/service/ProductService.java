package com.example.inventory.service;

import com.example.inventory.entity.Inventory;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for managing Products.
 * Contains the business logic for creating, updating, retrieving, and deleting products.
 */
@Service // Marks this class as a Spring Service component containing business logic
@Slf4j // Lombok annotation to auto-generate a logger (log variable) for this class
@RequiredArgsConstructor // Automatically generates a constructor that injects final fields (Dependency Injection)
public class ProductService {

    private final ProductRepository productRepository; // Injected repository for Product DB operations
    private final InventoryRepository inventoryRepository; // Injected repository for Inventory DB operations

    /**
     * Retrieves all products from the database.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a single product by its ID. Throws an exception if not found.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /**
     * Creates a new product and initializes its corresponding inventory record.
     */
    @Transactional // Ensures atomicity: if saving inventory fails, the product save is also rolled back
    public Product createProduct(Product product) {
        // Save the product first so we get an auto-generated ID
        Product savedProduct = productRepository.save(product);

        // Initialize inventory for the new product with quantity 0
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct); // Links inventory to the product
        inventory.setQuantity(0); // Starts with zero stock
        inventory.setReorderLevel(10); // Default reorder level
        inventoryRepository.save(inventory);

        return savedProduct;
    }

    /**
     * Updates an existing product's details (name, category, price).
     */
    @Transactional // Ensures the update happens in a single database transaction
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id); // Fetch existing product

        // Update fields
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
    }

    /**
     * Updates only the price of an existing product.
     */
    @Transactional
    public Product updatePrice(Long id, BigDecimal newPrice) {
        Product product = getProductById(id);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    /**
     * Deletes a product. Due to cascade settings, its inventory is also deleted.
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
