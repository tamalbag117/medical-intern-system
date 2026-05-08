package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.LeaveRequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest>
    findByEmailOrderByAppliedAtDesc(
            String email
    );

    List<LeaveRequest>
    findAllByOrderByAppliedAtDesc();
}