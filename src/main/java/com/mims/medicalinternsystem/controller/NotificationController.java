package com.mims.medicalinternsystem.controller;


import com.mims.medicalinternsystem.entity.Notification;
import com.mims.medicalinternsystem.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping
    public List<Notification> get() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.getMy(email);
    }

    @GetMapping("/count")
    public long count() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.countUnread(email);
    }

    @PostMapping("/read-all")
    public void readAll() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        service.markAllRead(email);
    }
}