package com.example.springboot.service;

import com.example.springboot.dto.ProductRequest;
import com.example.springboot.dto.ProductResponse;
import com.example.springboot.entity.Product;
import com.example.springboot.exception.ResourceNotFoundException;
import com.example.springboot.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setIsActive(true);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        testProductRequest = new ProductRequest();
        testProductRequest.setName("Test Product");
        testProductRequest.setDescription("Test Description");
        testProductRequest.setPrice(99.99);
    }

    @Test
    void getAllProducts_ShouldReturnAllActiveProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByIsActiveTrue()).thenReturn(products);

        // When
        List<ProductResponse> result = productService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        verify(productRepository).findByIsActiveTrue();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.createProduct(testProductRequest);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse result = productService.updateProduct(1L, testProductRequest);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).findByIdAndIsActiveTrue(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, testProductRequest));
        verify(productRepository).findByIdAndIsActiveTrue(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldSoftDelete() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).findByIdAndIsActiveTrue(1L);
        verify(productRepository).save(any(Product.class));
        assertFalse(testProduct.getIsActive());
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Given
        when(productRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository).findByIdAndIsActiveTrue(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
} 