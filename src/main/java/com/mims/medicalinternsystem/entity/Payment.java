package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private Double amount;

    private String status; // PENDING, SUCCESS, FAILED

    private String transactionId;

    private LocalDateTime createdAt;
}