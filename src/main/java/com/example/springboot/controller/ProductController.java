package com.example.springboot.controller;

import com.example.springboot.dto.ApiResponse;
import com.example.springboot.dto.ProductRequest;
import com.example.springboot.dto.ProductResponse;
import com.example.springboot.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "Product management APIs")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Retrieves a list of all active products from the database with Redis caching"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all products",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }

    @Operation(
        summary = "Get products with pagination",
        description = "Retrieves a paginated list of active products"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved paginated products"
        )
    })
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsPaginated(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Sort field", example = "name")
        @RequestParam(defaultValue = "id") String sortBy,
        @Parameter(description = "Sort direction", example = "ASC")
        @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductResponse> products = productService.getAllProductsPaginated(pageable);
        
        ApiResponse<Page<ProductResponse>> response = ApiResponse.<Page<ProductResponse>>builder()
                .status("success")
                .message("Products retrieved successfully")
                .data(products)
                .timestamp(java.time.LocalDateTime.now())
                .totalCount(products.getTotalElements())
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a specific product by its ID with Redis caching"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the product",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
        @Parameter(description = "ID of the product to retrieve", required = true)
        @PathVariable Long id
    ) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    @Operation(
        summary = "Create new product",
        description = "Creates a new product and invalidates Redis cache"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data provided",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
        @Parameter(description = "Product details to create", required = true)
        @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductResponse product = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    @Operation(
        summary = "Update product",
        description = "Updates an existing product by ID and invalidates Redis cache"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product updated successfully",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data provided",
            content = @Content
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @Parameter(description = "ID of the product to update", required = true)
        @PathVariable Long id,
        @Parameter(description = "Updated product details", required = true)
        @Valid @RequestBody ProductRequest productRequest
    ) {
        ProductResponse product = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }

    @Operation(
        summary = "Delete product",
        description = "Soft deletes a product by ID and invalidates Redis cache"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @Parameter(description = "ID of the product to delete", required = true)
        @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }
} 