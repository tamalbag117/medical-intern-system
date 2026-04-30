package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByInternEmail(String email);

    Page<ActivityLog> findByInternEmail(String email, Pageable pageable);

    Page<ActivityLog> findByInternEmailAndPatientNameContainingIgnoreCase(
            String email, String patient, Pageable pageable
    );

    Page<ActivityLog> findByStatus(String status, Pageable pageable);
}