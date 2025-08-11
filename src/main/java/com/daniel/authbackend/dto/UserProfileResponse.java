package com.daniel.authbackend.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String username;
    private String email;
    private String role;
}