package com.example.springboot.controller;

import com.example.springboot.dto.ProductRequest;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setIsActive(true);
        productRepository.save(product);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(get("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductById_WhenProductDoesNotExist_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_WithValidData_ShouldCreateProduct() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("New Description");
        request.setPrice(149.99);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("New Product"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName(""); // Invalid: empty name
        request.setPrice(-10.0); // Invalid: negative price

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateProduct_WithValidData_ShouldUpdateProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setName("Original Name");
        product.setDescription("Original Description");
        product.setPrice(99.99);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);

        ProductRequest request = new ProductRequest();
        request.setName("Updated Name");
        request.setDescription("Updated Description");
        request.setPrice(199.99);

        // When & Then
        mockMvc.perform(put("/api/v1/products/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setName("To Delete");
        product.setDescription("Will be deleted");
        product.setPrice(99.99);
        product.setIsActive(true);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(delete("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void getAllProducts_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }
} 