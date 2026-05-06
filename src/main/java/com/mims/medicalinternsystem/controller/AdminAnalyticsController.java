package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.AdminAnalytics;
import com.mims.medicalinternsystem.service.AdminAnalyticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AdminAnalytics analytics() {
        return service.getAnalytics();
    }
}
