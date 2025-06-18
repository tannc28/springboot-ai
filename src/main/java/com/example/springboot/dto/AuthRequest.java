package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication Request DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    
    @Schema(description = "Username for login", example = "john_doe")
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(description = "Password for login", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
} 