package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 BUSINESS ID
    @Column(name = "patient_id", unique = true, nullable = false)
    private String patientId;

    @Column(name = "intern_email", nullable = false)
    private String internEmail;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String task;

    @Column(name = "medical_reason")
    private String medicalReason = "";

    private String remarks = "";

    private LocalDate visitDate;

    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}