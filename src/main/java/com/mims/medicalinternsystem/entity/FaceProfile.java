package com.mims.medicalinternsystem.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "face_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================================================
       ✅ USER EMAIL
    ========================================================= */

    @Column(unique = true)
    private String email;

    /* =========================================================
       ✅ FACE VECTOR
    ========================================================= */

    @Lob
    @Column(name = "face_descriptor")
    private String faceDescriptor;

    /* =========================================================
       ✅ TIMESTAMPS
    ========================================================= */

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}