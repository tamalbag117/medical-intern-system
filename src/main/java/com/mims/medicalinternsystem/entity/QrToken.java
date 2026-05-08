package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(name = "shift_name")
    private String shiftName;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}