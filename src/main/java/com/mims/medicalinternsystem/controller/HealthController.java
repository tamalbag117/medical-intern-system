package com.mims.medicalinternsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // ✅ ROOT ENDPOINT (CRITICAL FOR RENDER)
    @GetMapping("/")
    public String home() {
        return "RUNNING";
    }

    // Optional health endpoint
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}