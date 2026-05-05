package com.mims.medicalinternsystem.controller;


import com.mims.medicalinternsystem.entity.Notification;
import com.mims.medicalinternsystem.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private NotificationService service;

    @GetMapping
    public List<Notification> get() {
        return service.getMy();
    }

    @GetMapping("/count")
    public long count() {
        return service.unreadCount();
    }

    @PostMapping("/read/{id}")
    public void read(@PathVariable Long id) {
        service.markRead(id);
    }
}
