package com.ecommerce.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    // T017: This API is SECURED - needs JWT token
    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        // Get logged in user's email from JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                "Hello! Your email is: " + email +
                        " | You accessed a SECURED API! ✅"
        );
    }

    // T017: Another secured endpoint
    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                "Welcome to Dashboard! Logged in as: " + email
        );
    }
}