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
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "https://medical-intern-system-one.onrender.com"
        },
        allowCredentials = "true"
)
public class ActivityController {

    private static final Logger log =
            LoggerFactory.getLogger(ActivityController.class);

    private final ActivityService service;

    // ✅ CREATE ACTIVITY
    @PostMapping
    @PreAuthorize("hasAnyRole('INTERN','DOCTOR')")
    public ResponseEntity<ActivityLog> log(
            @Valid @RequestBody ActivityRequest req
    ) {

        log.info("Creating activity for patient: {}",
                req.getPatientName());

        ActivityLog created =
                service.logActivity(
                        req.getPatientName(),
                        req.getTask(),
                        req.getMedicalReason(),
                        req.getRemarks()
                );

        return ResponseEntity.ok(created);
    }

    // ✅ MY LOGS
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivityLog>> myLogs() {

        return ResponseEntity.ok(
                service.myLogs()
        );
    }

    // ✅ ALL LOGS
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ActivityLog>> allLogs() {

        return ResponseEntity.ok(
                service.allLogs()
        );
    }

    // ✅ REVIEW / APPROVAL
    @PostMapping("/review")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ActivityLog> review(

            @RequestParam Long id,

            @RequestParam String status,

            @RequestParam(required = false)
            String remarks
    ) {

        String normalized =
                status.toUpperCase().trim();

        // ✅ SAFE VALIDATION
        if (!List.of(
                "COMPLETED",
                "REJECTED",
                "PENDING"
        ).contains(normalized)) {

            throw new IllegalArgumentException(
                    "Invalid status value"
            );
        }

        log.info(
                "Reviewing activity {} with status {}",
                id,
                normalized
        );

        ActivityLog updated =
                service.review(
                        id,
                        normalized,
                        remarks
                );

        return ResponseEntity.ok(updated);
    }

    // ✅ PENDING LIST
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
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

        log.warn("Deleting activity {}", id);

        service.delete(id);

        return ResponseEntity.ok(
                "Deleted successfully"
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INTERN','DOCTOR')")
    public ResponseEntity<ActivityLog> update(

            @PathVariable Long id,

            @Valid @RequestBody ActivityRequest req
    ) {

        log.info("Updating activity {}", id);

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

    // ✅ MY LOGS PAGED
    @GetMapping("/my/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ActivityLog>> myLogsPaged(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size,

            @RequestParam(required = false)
            String search
    ) {

        // ✅ SAFE LIMITS
        page = Math.max(page, 0);

        size = Math.min(
                Math.max(size, 1),
                50
        );

        return ResponseEntity.ok(
                service.myLogsPaged(
                        page,
                        size,
                        search
                )
        );
    }

    // ✅ PENDING PAGED
    @GetMapping("/pending/paged")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Page<ActivityLog>> pendingPaged(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        // ✅ SAFE LIMITS
        page = Math.max(page, 0);

        size = Math.min(
                Math.max(size, 1),
                50
        );

        return ResponseEntity.ok(
                service.pendingPaged(
                        page,
                        size
                )
        );
    }
}