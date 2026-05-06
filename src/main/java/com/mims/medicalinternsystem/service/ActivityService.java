package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.ActivityLog;
import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.Role;
import com.mims.medicalinternsystem.repository.ActivityLogRepository;
import com.mims.medicalinternsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.access.AccessDeniedException;
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

    private final ActivityLogRepository repo;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;

    // =====================================================
    // ✅ CREATE
    // =====================================================

    @PreAuthorize("isAuthenticated()")
    public ActivityLog logActivity(
            String patientName,
            String task,
            String reason,
            String remarks
    ) {

        String email = getCurrentUserEmail();

        ActivityLog log = new ActivityLog();

        log.setPatientId(
                "PAT-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
        );

        log.setInternEmail(email);

        log.setPatientName(patientName);

        log.setTask(task);

        log.setMedicalReason(
                reason != null ? reason : ""
        );

        log.setRemarks(
                remarks != null ? remarks : ""
        );

        log.setVisitDate(LocalDate.now());

        log.setTimestamp(LocalDateTime.now());

        log.setStatus("PENDING");

        ActivityLog saved = repo.save(log);

        notifyUpdate();

        notifyDoctors(saved);

        return saved;
    }

    // =====================================================
    // ✅ MY LOGS
    // =====================================================

    @PreAuthorize("isAuthenticated()")
    public List<ActivityLog> myLogs() {

        String email = getCurrentUserEmail();

        return repo.findByInternEmail(email);
    }

    // =====================================================
    // ✅ ADMIN / DOCTOR
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public List<ActivityLog> allLogs() {

        return repo.findAll();
    }

    // =====================================================
    // ✅ REVIEW
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ActivityLog review(
            Long id,
            String status,
            String remarks
    ) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Activity not found"
                        )
                );

        String reviewer = getCurrentUserEmail();

        log.setStatus(status);

        log.setReviewedBy(reviewer);

        log.setReviewedAt(LocalDateTime.now());

        if (remarks != null) {
            log.setRemarks(remarks);
        }

        ActivityLog updated = repo.save(log);

        notifyUpdate();

        notificationService.send(
                log.getInternEmail(),
                "Your activity for patient "
                        + log.getPatientName()
                        + " was "
                        + status
        );

        return updated;
    }

    // =====================================================
    // ✅ PENDING
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public List<ActivityLog> pendingLogs() {

        return repo.findByStatus(
                "PENDING",
                PageRequest.of(0, 100)
        ).getContent();
    }

    // =====================================================
    // ✅ DELETE
    // =====================================================

    @PreAuthorize("isAuthenticated()")
    public void delete(Long id) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Activity not found"
                        )
                );

        String currentUser =
                getCurrentUserEmail();

        boolean isAdmin =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        if (!isAdmin &&
                !log.getInternEmail()
                        .equals(currentUser)) {

            throw new AccessDeniedException(
                    "Not allowed"
            );
        }

        repo.delete(log);

        notifyUpdate();
    }

    // =====================================================
    // ✅ UPDATE
    // =====================================================

    @PreAuthorize("isAuthenticated()")
    public ActivityLog update(
            Long id,
            String patientName,
            String task,
            String reason,
            String remarks
    ) {

        ActivityLog log = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Activity not found"
                        )
                );

        String currentUser =
                getCurrentUserEmail();

        boolean isOwner =
                log.getInternEmail()
                        .equals(currentUser);

        boolean isAdmin =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        if (!isOwner && !isAdmin) {

            throw new AccessDeniedException(
                    "Not allowed"
            );
        }

        log.setPatientName(patientName);

        log.setTask(task);

        log.setMedicalReason(
                reason != null ? reason : ""
        );

        log.setRemarks(
                remarks != null ? remarks : ""
        );

        ActivityLog updated = repo.save(log);

        notifyUpdate();

        return updated;
    }

    // =====================================================
    // ✅ PAGINATION
    // =====================================================

    @PreAuthorize("isAuthenticated()")
    public Page<ActivityLog> myLogsPaged(
            int page,
            int size,
            String search
    ) {

        String email = getCurrentUserEmail();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("timestamp")
                                .descending()
                );

        if (search != null &&
                !search.isBlank()) {

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

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Page<ActivityLog> pendingPaged(
            int page,
            int size
    ) {

        return repo.findByStatus(
                "PENDING",
                PageRequest.of(page, size)
        );
    }

    // =====================================================
    // ✅ AI
    // =====================================================

    public List<ActivityLog> allLogsForAI() {

        try {
            return repo.findAll();
        } catch (Exception e) {
            return List.of();
        }
    }

    // =====================================================
    // ✅ HELPERS
    // =====================================================

    private String getCurrentUserEmail() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null ||
                auth.getName() == null) {

            throw new RuntimeException(
                    "User not authenticated"
            );
        }

        return auth.getName();
    }

    private void notifyDoctors(ActivityLog log) {

        List<User> doctors =
                userRepository.findByRole(
                        Role.DOCTOR
                );

        for (User d : doctors) {

            notificationService.send(
                    d.getEmail(),
                    "🩺 New activity submitted: "
                            + log.getPatientName()
            );
        }
    }

    private void notifyUpdate() {

        try {

            if (messagingTemplate != null) {

                messagingTemplate.convertAndSend(
                        "/topic/activity",
                        "updated"
                );
            }

        } catch (Exception ignored) {
        }
    }
}