package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.Role;
import com.mims.medicalinternsystem.repository.ActivityLogRepository;
import com.mims.medicalinternsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private static final Logger log =
            LoggerFactory.getLogger(ActivityService.class);

    private final ActivityLogRepository repo;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;

    // ✅ CREATE
    @PreAuthorize("hasAnyRole('INTERN','DOCTOR')")
    public ActivityLog logActivity(
            String patientName,
            String task,
            String reason,
            String remarks
    ) {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Authentication failed");
        }

        String email = auth.getName();

        log.info("Creating activity by {}", email);

        ActivityLog logEntity = new ActivityLog();

        logEntity.setPatientId(
                "PAT-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
        );

        logEntity.setInternEmail(email);

        logEntity.setPatientName(
                patientName != null ? patientName : ""
        );

        logEntity.setTask(
                task != null ? task : ""
        );

        logEntity.setMedicalReason(
                reason != null ? reason : ""
        );

        logEntity.setRemarks(
                remarks != null ? remarks : ""
        );

        logEntity.setVisitDate(LocalDate.now());

        logEntity.setTimestamp(LocalDateTime.now());

        logEntity.setStatus("PENDING");

        ActivityLog saved = repo.save(logEntity);

        // ✅ realtime safe
        notifyUpdate();

        // ✅ doctor notifications safe
        notifyDoctors(saved);

        return saved;
    }

    // ✅ NOTIFY DOCTORS
    private void notifyDoctors(ActivityLog log) {

        try {

            List<User> doctors =
                    userRepository.findByRole(Role.DOCTOR);

            if (doctors == null || doctors.isEmpty()) {
                return;
            }

            for (User doctor : doctors) {

                if (doctor.getEmail() == null) continue;

                notificationService.send(
                        doctor.getEmail(),
                        "🩺 New activity submitted: "
                                + log.getPatientName()
                );
            }

        } catch (Exception e) {

            log.error(
                    "Doctor notification failed",
                    e
            );
        }
    }

    // ✅ MY LOGS
    @PreAuthorize("isAuthenticated()")
    public List<ActivityLog> myLogs() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null || auth.getName() == null) {
            return List.of();
        }

        String email = auth.getName();

        return repo.findByInternEmail(email);
    }

    // ✅ ALL LOGS
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public List<ActivityLog> allLogs() {

        return repo.findAll(
                Sort.by(Sort.Direction.DESC, "timestamp")
        );
    }

    // ✅ REVIEW
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ActivityLog review(
            Long id,
            String status,
            String remarks
    ) {

        ActivityLog logEntity =
                repo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Activity not found"
                                ));

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String doctorEmail =
                auth != null
                        ? auth.getName()
                        : "SYSTEM";

        logEntity.setStatus(status);

        logEntity.setReviewedBy(doctorEmail);

        logEntity.setReviewedAt(LocalDateTime.now());

        if (remarks != null) {
            logEntity.setRemarks(remarks);
        }

        ActivityLog updated =
                repo.save(logEntity);

        notifyUpdate();

        try {

            notificationService.send(
                    logEntity.getInternEmail(),
                    "Your activity for patient "
                            + logEntity.getPatientName()
                            + " was "
                            + status
            );

        } catch (Exception e) {

            log.error(
                    "Intern notification failed",
                    e
            );
        }

        return updated;
    }

    // ✅ PENDING
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public List<ActivityLog> pendingLogs() {

        return repo.findByStatus(
                        "PENDING",
                        PageRequest.of(0, 100)
                )
                .getContent();
    }

    // ✅ DELETE
    @PreAuthorize("hasAnyRole('ADMIN','INTERN')")
    public void delete(Long id) {

        ActivityLog logEntity =
                repo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Activity not found"
                                ));

        repo.delete(logEntity);

        notifyUpdate();
    }

    // ✅ UPDATE
    @PreAuthorize("hasAnyRole('INTERN','DOCTOR')")
    public ActivityLog update(
            Long id,
            String patientName,
            String task,
            String reason,
            String remarks
    ) {

        ActivityLog logEntity =
                repo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Activity not found"
                                ));

        logEntity.setPatientName(patientName);

        logEntity.setTask(task);

        logEntity.setMedicalReason(
                reason != null ? reason : ""
        );

        logEntity.setRemarks(
                remarks != null ? remarks : ""
        );

        ActivityLog updated =
                repo.save(logEntity);

        notifyUpdate();

        return updated;
    }

    // ✅ PAGED
    public Page<ActivityLog> myLogsPaged(
            int page,
            int size,
            String search
    ) {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null || auth.getName() == null) {

            return Page.empty();
        }

        String email = auth.getName();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp").descending()
                );

        if (search != null && !search.isBlank()) {

            return repo
                    .findByInternEmailAndPatientNameContainingIgnoreCase(
                            email,
                            search,
                            pageable
                    );
        }

        return repo.findByInternEmail(
                email,
                pageable
        );
    }

    // ✅ PENDING PAGED
    public Page<ActivityLog> pendingPaged(
            int page,
            int size
    ) {

        return repo.findByStatus(
                "PENDING",
                PageRequest.of(page, size)
        );
    }

    // ✅ REALTIME SAFE
    private void notifyUpdate() {

        try {

            if (messagingTemplate != null) {

                messagingTemplate.convertAndSend(
                        "/topic/activity",
                        "updated"
                );
            }

        } catch (Exception e) {

            log.error(
                    "WebSocket broadcast failed",
                    e
            );
        }
    }

    // ✅ AI SAFE
    public List<ActivityLog> allLogsForAI() {

        try {

            return repo.findAll(
                    Sort.by(Sort.Direction.DESC, "timestamp")
            );

        } catch (Exception e) {

            log.error(
                    "AI logs fetch failed",
                    e
            );

            return List.of();
        }
    }
}