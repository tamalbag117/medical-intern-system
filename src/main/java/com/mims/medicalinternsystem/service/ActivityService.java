package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityLogRepository repo;

    // 🔥 INTERN logs activity
    @PreAuthorize("hasRole('INTERN')")
    public ActivityLog logActivity(String patientName, String task) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        ActivityLog log = new ActivityLog();
        log.setInternEmail(email);
        log.setPatientName(patientName);
        log.setTask(task);
        log.setTimestamp(LocalDateTime.now());
        log.setStatus("PENDING"); // 🔥 important

        return repo.save(log);
    }

    // 🔥 INTERN sees own logs
    @PreAuthorize("hasRole('INTERN')")
    public List<ActivityLog> myLogs() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return repo.findByInternEmail(email);
    }

    // 🔥 ADMIN sees all
    @PreAuthorize("hasRole('ADMIN')")
    public List<ActivityLog> allLogs() {
        return repo.findAll();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    public ActivityLog review(Long id, String status, String remarks) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        String doctorEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        log.setStatus(status); // APPROVED / REJECTED
        log.setReviewedBy(doctorEmail);
        log.setRemarks(remarks);

        return repo.save(log);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    public List<ActivityLog> pendingLogs() {
        return repo.findAll()
                .stream()
                .filter(log -> "PENDING".equals(log.getStatus()))
                .toList();
    }
}
