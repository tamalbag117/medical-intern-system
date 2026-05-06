package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.LeaveRequest;
import com.mims.medicalinternsystem.repository.LeaveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository repo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ APPLY
    public LeaveRequest apply(
            String reason,
            LocalDate from,
            LocalDate to
    ) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        LeaveRequest leave = new LeaveRequest();

        leave.setEmail(email);
        leave.setReason(reason);

        leave.setFromDate(from);
        leave.setToDate(to);

        leave.setStatus("PENDING");

        leave.setAppliedAt(LocalDateTime.now());

        LeaveRequest saved = repo.save(leave);

        notificationService.send(
                "doctor@email.com",
                "New leave request submitted"
        );

        messagingTemplate.convertAndSend(
                "/topic/activity",
                Map.of(
                        "type", "ACTIVITY_UPDATED",
                        "timestamp", System.currentTimeMillis()
                )
        );

        return saved;
    }

    // ✅ MY LEAVES
    public List<LeaveRequest> myLeaves() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return repo.findByEmailOrderByAppliedAtDesc(email);
    }

    // ✅ PENDING
    public List<LeaveRequest> pendingLeaves() {
        return repo.findByStatusOrderByAppliedAtDesc(
                "PENDING"
        );
    }

    // ✅ REVIEW
    public LeaveRequest review(
            Long id,
            String status,
            String remarks
    ) {

        LeaveRequest leave = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Leave not found")
                );

        String doctor = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        leave.setStatus(status);

        leave.setReviewedBy(doctor);

        leave.setReviewedAt(LocalDateTime.now());

        leave.setRemarks(remarks);

        LeaveRequest updated = repo.save(leave);

        notificationService.send(
                leave.getEmail(),
                "Your leave request was " + status
        );

        messagingTemplate.convertAndSend(
                "/topic/leave",
                "updated"
        );

        return updated;
    }
}