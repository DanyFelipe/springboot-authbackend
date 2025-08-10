package com.daniel.authbackend.controller;

import com.daniel.authbackend.dto.RegisterRequest;
import com.daniel.authbackend.entity.Role;
import com.daniel.authbackend.entity.User;
import com.daniel.authbackend.repository.RoleRepository;
import com.daniel.authbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Endpoint para registro de usuario
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByUsername("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));

        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // Endpoint para obtener info del usuario autenticado
    @GetMapping("/user/info")
    public String userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return "Usuario autenticado: " + userDetails.getUsername();
    }

    // Endpoint exclusivo para admins
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Panel de administrador";
    }
}
