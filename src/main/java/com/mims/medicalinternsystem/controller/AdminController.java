package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.enums.Role;

import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/role")
    public User updateRole(@PathVariable Long id, @RequestParam String role) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.valueOf(role.toUpperCase())); // ✅ FIX

        return userRepository.save(user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}