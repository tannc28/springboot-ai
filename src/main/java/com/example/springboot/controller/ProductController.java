package com.example.springboot.controller;

import com.example.springboot.entity.Product;
import com.example.springboot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "Product management APIs")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Retrieves a list of all products from the database with Redis caching"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all products",
            content = @Content(schema = @Schema(implementation = Product.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a specific product by its ID with Redis caching"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the product",
            content = @Content(schema = @Schema(implementation = Product.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
        @Parameter(description = "ID of the product to retrieve", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
        summary = "Create new product",
        description = "Creates a new product and invalidates Redis cache"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = Product.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid product data provided",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(
        @Parameter(description = "Product details to create", required = true)
        @RequestBody Product product
    ) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @Operation(
        summary = "Update product",
        description = "Updates an existing product by ID and invalidates Redis cache"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = Product.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid product data provided",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
        @Parameter(description = "ID of the product to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated product details", required = true)
        @RequestBody Product product
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @Operation(
        summary = "Delete product",
        description = "Deletes a product by ID and invalidates Redis cache"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID of the product to delete", required = true)
        @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
} 