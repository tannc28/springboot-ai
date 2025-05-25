package com.example.springboot.service;

import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from database");
        List<Product> products = productRepository.findAll();
        log.info("Found {} products", products.size());
        return products;
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new RuntimeException("Product not found");
                });
        log.info("Found product: {}", product);
        return product;
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product);
        Product savedProduct = productRepository.save(product);
        log.info("Created product with id: {}", savedProduct.getId());
        return savedProduct;
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with id: {}", id);
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Updated product: {}", updatedProduct);
        return updatedProduct;
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }
} 