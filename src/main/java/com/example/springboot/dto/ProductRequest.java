package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product Request DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @Schema(description = "Name of the product", example = "iPhone 15")
    @NotBlank(message = "Product name is required")
    private String name;
    
    @Schema(description = "Description of the product", example = "Latest iPhone model")
    private String description;
    
    @Schema(description = "Price of the product", example = "999.99")
    @NotNull(message = "Product price is required")
    @Positive(message = "Price must be positive")
    private Double price;
} 