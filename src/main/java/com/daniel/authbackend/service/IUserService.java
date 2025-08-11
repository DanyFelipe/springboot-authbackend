package com.daniel.authbackend.service;

import com.daniel.authbackend.dto.*;

import java.util.List;

public interface IUserService {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
    LoginResponse refreshToken(RefreshRequest request);
    UserProfileResponse getProfile(String username);
    UserProfileResponse updateProfile(String username, UpdateUserRequest request);
    void deleteUser(String username);
    void changePassword(String username, ChangePasswordRequest request);
    List<UserProfileResponse> getAllUsers();
    void promoteToAdmin(String username);
}