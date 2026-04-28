package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService service;

    // 🔥 Log activity
    @PostMapping
    public ActivityLog log(@RequestParam String patient,
                           @RequestParam String task) {
        return service.logActivity(patient, task);
    }

    // 🔥 Get my logs
    @GetMapping("/my")
    public List<ActivityLog> myLogs() {
        return service.myLogs();
    }

    // 🔥 Admin view
    @GetMapping("/all")
    public List<ActivityLog> allLogs() {
        return service.allLogs();
    }

    // 🔥 Doctor review
    @PostMapping("/review")
    public ActivityLog review(@RequestParam Long id,
                              @RequestParam String status,
                              @RequestParam(required = false) String remarks) {
        return service.review(id, status, remarks);
    }

    // 🔥 Doctor pending
    @GetMapping("/pending")
    public List<ActivityLog> pending() {
        return service.pendingLogs();
    }

    // 🔥 DELETE (FIXED)
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id); // ✅ FIXED
        return "Deleted successfully";
    }
}