package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 BUSINESS ID (visible to users)
    @Column(unique = true)
    private String patientId;

    private String internEmail;

    private String patientName;

    private String task;

    // 🏥 NEW MEDICAL FIELDS
    private String medicalReason;

    private String remarks;

    private LocalDate visitDate;

    private LocalDateTime timestamp;

    // 🔐 WORKFLOW
    private String status; // PENDING, APPROVED, REJECTED

    private String reviewedBy; // doctor email
}