package com.mims.medicalinternsystem.service;



import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    // 🔥 MOCK VALIDATION (for now)
    public boolean verify(String token) {
        return token != null && token.equals("1234"); // test token
    }
}
