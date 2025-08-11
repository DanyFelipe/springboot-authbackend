package com.daniel.authbackend.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}