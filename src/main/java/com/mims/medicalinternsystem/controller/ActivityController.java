package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.ActivityRequest;
import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.service.ActivityService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private static final Logger log =
            LoggerFactory.getLogger(ActivityController.class);

    private final ActivityService service;

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<ActivityLog> log(
            @Valid @RequestBody ActivityRequest req
    ) {

        ActivityLog created =
                service.logActivity(
                        req.getPatientName(),
                        req.getTask(),
                        req.getMedicalReason(),
                        req.getRemarks()
                );

        return ResponseEntity.ok(created);
    }

    // ✅ INTERN LOGS
    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<List<ActivityLog>> myLogs() {

        return ResponseEntity.ok(
                service.myLogs()
        );
    }

    // ✅ ADMIN
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ActivityLog>> allLogs() {

        return ResponseEntity.ok(
                service.allLogs()
        );
    }

    // ✅ REVIEW
    @PostMapping("/review")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ActivityLog> review(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks
    ) {

        // ✅ validation
        if (!List.of(
                "COMPLETED",
                "REJECTED",
                "PENDING"
        ).contains(status)) {

            throw new RuntimeException(
                    "Invalid status value"
            );
        }

        ActivityLog updated =
                service.review(
                        id,
                        status,
                        remarks
                );

        return ResponseEntity.ok(updated);
    }

    // ✅ PENDING
    @GetMapping("/pending")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<ActivityLog>> pending() {

        return ResponseEntity.ok(
                service.pendingLogs()
        );
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','INTERN')")
    public ResponseEntity<String> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.ok(
                "Deleted successfully"
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<ActivityLog> update(
            @PathVariable Long id,
            @Valid @RequestBody ActivityRequest req
    ) {

        ActivityLog updated =
                service.update(
                        id,
                        req.getPatientName(),
                        req.getTask(),
                        req.getMedicalReason(),
                        req.getRemarks()
                );

        return ResponseEntity.ok(updated);
    }

    // ✅ PAGED
    @GetMapping("/my/paged")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Page<ActivityLog>> myLogsPaged(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size,

            @RequestParam(required = false)
            String search
    ) {

        return ResponseEntity.ok(
                service.myLogsPaged(
                        page,
                        size,
                        search
                )
        );
    }

    // ✅ DOCTOR PAGED
    @GetMapping("/pending/paged")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Page<ActivityLog>> pendingPaged(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        return ResponseEntity.ok(
                service.pendingPaged(
                        page,
                        size
                )
        );
    }
}