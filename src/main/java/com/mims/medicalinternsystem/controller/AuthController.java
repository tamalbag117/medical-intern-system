package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.LoginRequest;
import com.mims.medicalinternsystem.dto.RegisterRequest;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.Role;
import com.mims.medicalinternsystem.repository.UserRepository;
import com.mims.medicalinternsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public String refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String refreshToken) {
        return authService.logout(refreshToken);
    }

    @PostMapping("/seed")
    public String seed() {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword(passwordEncoder.encode("1234")); // 🔐 encoded
        user.setRole(Role.INTERN);
        user.setAccountLocked(false);
        user.setFailedAttempts(0);

        userRepository.save(user);

        return "User created";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.INTERN);
        user.setAccountLocked(false);
        user.setFailedAttempts(0);

        userRepository.save(user);

        return "User registered";
    }
}