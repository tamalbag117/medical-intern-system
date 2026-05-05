package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.repository.ActivityLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.data.domain.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityLogRepository repo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ CREATE
    @PreAuthorize("hasRole('INTERN')")
    public ActivityLog logActivity(String patientName, String task, String reason, String remarks) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        ActivityLog log = new ActivityLog();

        log.setPatientId("PAT-" + UUID.randomUUID().toString().substring(0, 8));
        log.setInternEmail(email);
        log.setPatientName(patientName);
        log.setTask(task);

        log.setMedicalReason(reason != null ? reason : "");
        log.setRemarks(remarks != null ? remarks : "");

        log.setVisitDate(LocalDate.now());
        log.setTimestamp(LocalDateTime.now());
        log.setStatus("PENDING");

        ActivityLog saved = repo.save(log);

        notifyUpdate();

        return saved;
    }

    // ✅ INTERN
    @PreAuthorize("hasRole('INTERN')")
    public List<ActivityLog> myLogs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.findByInternEmail(email);
    }

    // ✅ ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public List<ActivityLog> allLogs() {
        return repo.findAll();
    }

    // ✅ REVIEW
    @PreAuthorize("hasRole('DOCTOR')")
    public ActivityLog review(Long id, String status, String remarks) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        String doctorEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        log.setStatus(status);
        log.setReviewedBy(doctorEmail);
        log.setRemarks(remarks != null ? remarks : "");

        ActivityLog updated = repo.save(log);

        notifyUpdate();

        return updated;
    }

    // ✅ PENDING
    @PreAuthorize("hasRole('DOCTOR')")
    public List<ActivityLog> pendingLogs() {
        return repo.findByStatus("PENDING", PageRequest.of(0, 100)).getContent();
    }

    // ✅ DELETE
    public void delete(Long id) {
        ActivityLog log = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        repo.delete(log);
        notifyUpdate();
    }

    // ✅ UPDATE
    public ActivityLog update(Long id, String patientName, String task, String reason, String remarks) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        log.setPatientName(patientName);
        log.setTask(task);
        log.setMedicalReason(reason != null ? reason : "");
        log.setRemarks(remarks != null ? remarks : "");

        ActivityLog updated = repo.save(log);

        notifyUpdate();

        return updated;
    }

    // ✅ PAGINATION
    public Page<ActivityLog> myLogsPaged(int page, int size, String search) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        if (search != null && !search.isEmpty()) {
            return repo.findByInternEmailAndPatientNameContainingIgnoreCase(email, search, pageable);
        }

        return repo.findByInternEmail(email, pageable);
    }

    public Page<ActivityLog> pendingPaged(int page, int size) {
        return repo.findByStatus("PENDING", PageRequest.of(page, size));
    }

    // 🔥 REAL-TIME PUSH
    private void notifyUpdate() {
        messagingTemplate.convertAndSend("/topic/activity", "updated");
    }
}