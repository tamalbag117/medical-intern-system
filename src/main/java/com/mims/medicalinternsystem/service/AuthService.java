package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.LoginRequest;
import com.mims.medicalinternsystem.entity.RefreshToken;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.exception.BadRequestException;
import com.mims.medicalinternsystem.exception.UnauthorizedException;
import com.mims.medicalinternsystem.repository.RefreshTokenRepository;
import com.mims.medicalinternsystem.repository.UserRepository;
import com.mims.medicalinternsystem.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mims.medicalinternsystem.service.CaptchaService;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshRepo;

    @Autowired
    private AuditService auditService;

    @Autowired
    private CaptchaService captchaService;

    @Value("${captcha.enabled:false}")
    private boolean captchaEnabled;



    // 🔐 LOGIN METHOD
    public Map<String, String> login(LoginRequest request) {

        log.info("Login attempt for {}", request.getEmail());

        if (captchaEnabled && !captchaService.verify(request.getCaptchaToken())) {
            log.warn("Captcha failed for {}", request.getEmail());
            throw new BadRequestException("Captcha verification failed");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getEmail());
                    throw new BadRequestException("User not found");
                });

        // 🚫 CHECK IF ACCOUNT LOCKED
        if (user.isAccountLocked()) {
            log.warn("Account locked for {}", user.getEmail());
            throw new UnauthorizedException("Account is locked. Try later.");
        }

        // ❌ WRONG PASSWORD
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            // 🔐 LOCK AFTER 5 ATTEMPTS
            if (attempts >= 5) {
                user.setAccountLocked(true);
                log.error("Account locked due to multiple failures: {}", user.getEmail());
            }

            userRepository.save(user);

            log.warn("Invalid password attempt {} for {}", attempts, user.getEmail());

            // 🔥 ADD CONTROLLED DELAY (ANTI-BRUTE FORCE)
            try {
                Thread.sleep(500); // 0.5 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // ✅ important (don’t ignore)
            }

            throw new UnauthorizedException("Invalid credentials");
        }

        // ✅ SUCCESS LOGIN → RESET COUNTER
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        userRepository.save(user);

        auditService.log("LOGIN", user.getEmail());

        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setEmail(user.getEmail());
        token.setExpiryDate(LocalDateTime.now().plusDays(1));

        refreshRepo.save(token);

        log.info("User {} logged in successfully", user.getEmail());

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }
    // 🔁 REFRESH TOKEN
    public String refresh(String refreshToken) {

        log.info("Refreshing token");

        RefreshToken token = refreshRepo.findByToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Invalid refresh token");
                    return new RuntimeException("Invalid refresh token");
                });

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Expired refresh token for {}", token.getEmail());
            throw new RuntimeException("Token expired");
        }

        log.info("Token refreshed for {}", token.getEmail());

        return jwtUtil.generateToken(token.getEmail());
    }

    // 🚪 LOGOUT
    public String logout(String refreshToken) {

        log.info("Logout attempt");

        refreshRepo.findByToken(refreshToken)
                .ifPresent(token -> {
                    // ✅ token exists here → safe to use
                    auditService.log("LOGOUT", token.getEmail());

                    refreshRepo.delete(token);
                    log.info("User {} logged out", token.getEmail());
                });

        return "Logged out successfully";
    }
}