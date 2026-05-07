package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.LeaveRequestDTO;
import com.mims.medicalinternsystem.entity.LeaveRequest;
import com.mims.medicalinternsystem.repository.LeaveRequestRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository repository;

    // ✅ APPLY LEAVE
    public LeaveRequest apply(
            LeaveRequestDTO dto
    ) {

        LeaveRequest leave =
                new LeaveRequest();

        leave.setEmail(currentUser());

        leave.setReason(
                dto.getReason()
        );

        leave.setFromDate(
                dto.getFromDate()
        );

        leave.setToDate(
                dto.getToDate()
        );

        leave.setRemarks(
                dto.getRemarks()
        );

        leave.setStatus("PENDING");

        leave.setAppliedAt(
                LocalDateTime.now()
        );

        return repository.save(leave);
    }

    // ✅ MY LEAVES
    public List<LeaveRequest> myLeaves() {

        return repository
                .findByEmailOrderByAppliedAtDesc(
                        currentUser()
                );
    }

    // ✅ ALL LEAVES
    public List<LeaveRequest> allLeaves() {

        return repository
                .findAllByOrderByAppliedAtDesc();
    }

    // ✅ APPROVE
    public LeaveRequest approve(
            Long id,
            String remarks
    ) {

        LeaveRequest leave =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found"
                                )
                        );

        leave.setStatus("APPROVED");

        leave.setReviewedBy(
                currentUser()
        );

        leave.setReviewedAt(
                LocalDateTime.now()
        );

        leave.setRemarks(
                remarks
        );

        return repository.save(leave);
    }

    // ✅ REJECT
    public LeaveRequest reject(
            Long id,
            String remarks
    ) {

        LeaveRequest leave =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave not found"
                                )
                        );

        leave.setStatus("REJECTED");

        leave.setReviewedBy(
                currentUser()
        );

        leave.setReviewedAt(
                LocalDateTime.now()
        );

        leave.setRemarks(
                remarks
        );

        return repository.save(leave);
    }

    // ✅ CURRENT USER
    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}