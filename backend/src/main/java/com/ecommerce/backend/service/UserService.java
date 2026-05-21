package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // T057: Get user profile
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found!")
            );
    }

    // T057: Update profile name
    public User updateProfile(String email, String newName) {
        User user = getUserByEmail(email);
        user.setName(newName);
        return userRepository.save(user);
    }

    // T058: Update password
    public void updatePassword(String email,
            String currentPassword, String newPassword) {

        User user = getUserByEmail(email);

        // Verify current password
        if (!passwordEncoder.matches(
                currentPassword, user.getPassword())) {
            throw new RuntimeException(
                "Current password is incorrect!"
            );
        }

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}