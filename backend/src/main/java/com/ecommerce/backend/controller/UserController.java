package com.ecommerce.backend.controller;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    private String getEmail() {
        return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();
    }

    // T057: Get profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User user = userService.getUserByEmail(getEmail());
            return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // T057: Update profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> request) {
        try {
            String email = getEmail();
            String newName = request.get("name");

            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Name cannot be empty!");
            }

            User updated = userService.updateProfile(
                email, newName.trim()
            );
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully!",
                "name", updated.getName(),
                "email", updated.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // T058: Update password
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestBody Map<String, String> request) {
        try {
            String email = getEmail();
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body("Both passwords required!");
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                    .body("New password min 6 characters!");
            }

            userService.updatePassword(
                email, currentPassword, newPassword
            );
            return ResponseEntity.ok(
                "Password updated successfully!"
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}