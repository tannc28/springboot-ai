package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "Product Response DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;
    
    @Schema(description = "Name of the product", example = "iPhone 15")
    private String name;
    
    @Schema(description = "Description of the product", example = "Latest iPhone model")
    private String description;
    
    @Schema(description = "Price of the product", example = "999.99")
    private Double price;
    
    @Schema(description = "Product creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Product last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Product availability status")
    private Boolean isActive;
} 