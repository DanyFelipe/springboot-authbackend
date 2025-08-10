package com.daniel.authbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateUserRequest {
    @NotBlank
    private String username;
    // Otros campos como email, nombre, etc. si necesitas
}