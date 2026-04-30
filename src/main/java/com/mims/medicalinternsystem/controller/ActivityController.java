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

    // 🔥 Create activity (UPDATED)
    @PostMapping
    public ActivityLog log(@RequestBody ActivityLog request) {
        return service.logActivity(
                request.getPatientName(),
                request.getTask(),
                request.getMedicalReason(),
                request.getRemarks()
        );
    }

    @GetMapping("/my")
    public List<ActivityLog> myLogs() {
        return service.myLogs();
    }

    @GetMapping("/all")
    public List<ActivityLog> allLogs() {
        return service.allLogs();
    }

    @PostMapping("/review")
    public ActivityLog review(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks
    ) {
        return service.review(id, status, remarks);
    }

    @GetMapping("/pending")
    public List<ActivityLog> pending() {
        return service.pendingLogs();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted successfully";
    }

    @PutMapping("/{id}")
    public ActivityLog update(
            @PathVariable Long id,
            @RequestBody ActivityLog request
    ) {
        return service.update(
                id,
                request.getPatientName(),
                request.getTask(),
                request.getMedicalReason(),
                request.getRemarks()
        );
    }


}