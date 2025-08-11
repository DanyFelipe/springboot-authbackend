package com.daniel.authbackend.controller;

import com.daniel.authbackend.dto.UpdateUserRequest;
import com.daniel.authbackend.dto.UserProfileResponse;
import com.daniel.authbackend.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * UserController - Gestión de usuarios (solo admins pueden gestionar otros usuarios).
 * Los usuarios autenticados pueden ver/editar su propio perfil.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Obtener todos los usuarios.
     * Solo puede acceder un ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener un usuario por username.
     * Solo puede acceder un ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUser(@PathVariable String username) {
        UserProfileResponse user = userService.getProfile(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Eliminar usuario.
     * Solo puede acceder un ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    /**
     * Promover usuario a admin.
     * Solo puede acceder un ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/promote/{username}")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable String username) {
        userService.promoteToAdmin(username);
        return ResponseEntity.ok().build();
    }

    /**
     * Cambiar email/username de un usuario.
     * Solo puede acceder un ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{username}")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable String username,
            @RequestBody UpdateUserRequest request) {
        UserProfileResponse updatedUser = userService.updateProfile(username, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Obtener el perfil propio del usuario autenticado.
     * Todos los usuarios autenticados pueden acceder.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getOwnProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileResponse user = userService.getProfile(username);
        return ResponseEntity.ok(user);
    }
}