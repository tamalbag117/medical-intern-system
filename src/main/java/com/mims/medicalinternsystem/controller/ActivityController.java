package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.service.ActivityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService service;

    // ✅ CREATE
    @PostMapping
    public ActivityLog log(@RequestBody ActivityLog request) {
        return service.logActivity(
                request.getPatientName(),
                request.getTask(),
                request.getMedicalReason(),
                request.getRemarks()
        );
    }

    // ✅ INTERN
    @GetMapping("/my")
    public List<ActivityLog> myLogs() {
        return service.myLogs();
    }

    // ✅ ADMIN
    @GetMapping("/all")
    public List<ActivityLog> allLogs() {
        return service.allLogs();
    }

    // ✅ DOCTOR REVIEW
    @PostMapping("/review")
    public ActivityLog review(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks
    ) {
        return service.review(id, status, remarks);
    }

    // ✅ DOCTOR PENDING
    @GetMapping("/pending")
    public List<ActivityLog> pending() {
        return service.pendingLogs();
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted successfully";
    }

    // ✅ UPDATE
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

    // ✅ PAGINATION (INTERN)
    @GetMapping("/my/paged")
    public Page<ActivityLog> myLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        return service.myLogsPaged(page, size, search);
    }

    // ✅ PAGINATION (DOCTOR)
    @GetMapping("/pending/paged")
    public Page<ActivityLog> pendingPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return service.pendingPaged(page, size);
    }


}