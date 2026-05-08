package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.FaceProfile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaceProfileRepository
        extends JpaRepository<FaceProfile, Long> {

    Optional<FaceProfile> findByEmail(
            String email
    );
}