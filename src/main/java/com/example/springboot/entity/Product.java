package com.example.springboot.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Schema(description = "Product Entity")
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Schema(description = "Unique identifier of the product", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Schema(description = "Name of the product", example = "iPhone 15")
    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;
    
    @Schema(description = "Description of the product", example = "Latest iPhone model")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Schema(description = "Price of the product", example = "999.99")
    @NotNull(message = "Product price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;
    
    @Schema(description = "Product creation timestamp")
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Schema(description = "Product last update timestamp")
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Product availability status")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
} 