package com.example.springboot.service;

import com.example.springboot.dto.ProductRequest;
import com.example.springboot.dto.ProductResponse;
import com.example.springboot.entity.Product;
import com.example.springboot.exception.ResourceNotFoundException;
import com.example.springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(value = "products")
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products from database");
        List<Product> products = productRepository.findByIsActiveTrue();
        log.info("Found {} active products", products.size());
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getAllProductsPaginated(Pageable pageable) {
        log.info("Fetching products with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> products = productRepository.findByIsActiveTrue(pageable);
        log.info("Found {} products on page {}", products.getContent().size(), pageable.getPageNumber());
        return products.map(this::mapToResponse);
    }

    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        log.info("Found product: {}", product.getName());
        return mapToResponse(product);
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest.getName());
        
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setIsActive(true);
        
        Product savedProduct = productRepository.save(product);
        log.info("Created product with id: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Updated product: {}", updatedProduct.getName());
        return mapToResponse(updatedProduct);
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);
        
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product soft deleted successfully with id: {}", id);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .isActive(product.getIsActive())
                .build();
    }
} 