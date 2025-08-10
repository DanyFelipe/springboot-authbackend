package com.daniel.authbackend.service;

import com.daniel.authbackend.dto.*;
import com.daniel.authbackend.exception.*;
import com.daniel.authbackend.model.User;
import com.daniel.authbackend.repository.UserRepository;
import com.daniel.authbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Contraseña incorrecta");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new LoginResponse(token, refreshToken, user.getUsername(), user.getRole());
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .build();
        userRepository.save(user);
        return new RegisterResponse("Usuario registrado correctamente", user.getUsername(), user.getRole());
    }

    @Override
    public LoginResponse refreshToken(RefreshRequest request) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (jwtUtil.isTokenExpired(request.getRefreshToken())) {
            throw new UserNotFoundException("Refresh token expirado");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new LoginResponse(token, refreshToken, user.getUsername(), user.getRole());
    }

    @Override
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        UserProfileResponse resp = new UserProfileResponse();
        resp.setUsername(user.getUsername());
        resp.setRole(user.getRole());
        return resp;
    }

    @Override
    public UserProfileResponse updateProfile(String username, UpdateUserRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        user.setUsername(request.getUsername());
        userRepository.save(user);
        UserProfileResponse resp = new UserProfileResponse();
        resp.setUsername(user.getUsername());
        resp.setRole(user.getRole());
        return resp;
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new UserNotFoundException("Contraseña antigua incorrecta");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}