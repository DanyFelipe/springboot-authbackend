package com.daniel.authbackend.controller;

import com.daniel.authbackend.dto.*;
import com.daniel.authbackend.service.IUserService;
import com.daniel.authbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtUtil.invalidateToken(token);
            return "Sesión cerrada correctamente";
        }
        return "No autenticado";
    }

    @PostMapping("/refresh")
    public LoginResponse refreshToken(@RequestBody RefreshRequest request) {
        return userService.refreshToken(request);
    }
}