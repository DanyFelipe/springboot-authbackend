package com.daniel.authbackend.mapper;

import com.daniel.authbackend.model.User;
import com.daniel.authbackend.dto.LoginResponse;

public class UserMapper {
    public static LoginResponse toLoginResponse(User user, String token, String refreshToken) {
        return new LoginResponse(token, refreshToken, user.getUsername(), user.getRole());
    }
}