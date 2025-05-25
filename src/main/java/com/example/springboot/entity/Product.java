package com.example.springboot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Product Entity")
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Schema(description = "Unique identifier of the product", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Schema(description = "Name of the product", example = "iPhone 15")
    private String name;
    
    @Schema(description = "Description of the product", example = "Latest iPhone model")
    private String description;
    
    @Schema(description = "Price of the product", example = "999.99")
    private Double price;
} 