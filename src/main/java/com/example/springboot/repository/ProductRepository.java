package com.example.springboot.repository;

import com.example.springboot.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByIsActiveTrue();
    
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    Optional<Product> findByIdAndIsActiveTrue(Long id);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Product> findActiveProductsByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN ?1 AND ?2")
    List<Product> findActiveProductsByPriceRange(Double minPrice, Double maxPrice);
} 