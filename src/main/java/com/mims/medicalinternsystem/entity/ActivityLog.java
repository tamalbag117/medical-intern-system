package com.mims.medicalinternsystem.entity;



import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String internEmail;

    private String patientName;

    private String task; // e.g. "Checked BP", "Administered medicine"

    private LocalDateTime timestamp;

    private String status; // PENDING, APPROVED, REJECTED

    private String reviewedBy; // doctor email

    private String remarks; // optional comments
}
