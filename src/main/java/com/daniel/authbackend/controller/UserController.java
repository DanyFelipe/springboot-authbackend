package com.daniel.authbackend.controller;

import com.daniel.authbackend.dto.*;
import com.daniel.authbackend.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/profile")
    public UserProfileResponse profile() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getProfile(username);
    }

    @PutMapping("/update")
    public UserProfileResponse update(@RequestBody UpdateUserRequest request) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateProfile(username, request);
    }

    @DeleteMapping("/delete")
    public String delete() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.deleteUser(username);
        return "Usuario eliminado";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.changePassword(username, request);
        return "Contraseña actualizada";
    }
}