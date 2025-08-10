package com.daniel.authbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RefreshRequest {
    @NotBlank
    private String refreshToken;
}