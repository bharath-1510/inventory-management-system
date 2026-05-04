package com.example.inventory.controller;

import com.example.inventory.entity.Product;
import com.example.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Product entities.
 * Handles HTTP requests and routes them to the ProductService.
 */
@RestController // Marks this class as a RESTful web controller, returning data (JSON) directly instead of views
@RequestMapping("/api/products") // Maps all requests starting with "/api/products" to this controller
@RequiredArgsConstructor // Lombok annotation to automatically generate a constructor for the final fields (dependency injection)
public class ProductController {

    private final ProductService productService; // Injected service layer to handle business logic

    /**
     * Retrieves a list of all products.
     */
    @GetMapping // Maps HTTP GET requests to this method
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts()); // Returns 200 OK with the list of products
    }

    /**
     * Retrieves a single product by its ID.
     */
    @GetMapping("/{id}") // Maps HTTP GET requests for a specific ID
    public ResponseEntity<Product> getProductById(@PathVariable Long id) { // @PathVariable binds the URL template variable {id} to the method parameter
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Creates a new product.
     */
    @PostMapping // Maps HTTP POST requests to this method
    public ResponseEntity<Product> createProduct(@jakarta.validation.Valid @RequestBody Product product) { // @RequestBody binds the HTTP request body to the domain object. @Valid triggers validation constraints.
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product)); // Returns 201 Created
    }

    /**
     * Updates an existing product fully.
     */
    @PutMapping("/{id}") // Maps HTTP PUT requests for a specific ID
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @jakarta.validation.Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    /**
     * Updates just the price of a specific product.
     */
    @PatchMapping("/{id}/price") // Maps HTTP PATCH requests. PATCH is used for partial updates.
    public ResponseEntity<Product> updatePrice(@PathVariable Long id, @RequestParam java.math.BigDecimal price) { // @RequestParam binds a query parameter (e.g., ?price=10.5)
        return ResponseEntity.ok(productService.updatePrice(id, price));
    }

    /**
     * Deletes a product by its ID.
     */
    @DeleteMapping("/{id}") // Maps HTTP DELETE requests
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
