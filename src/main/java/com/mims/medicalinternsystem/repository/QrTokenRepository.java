package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.QrToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrTokenRepository
        extends JpaRepository<QrToken, Long> {

    Optional<QrToken> findByToken(String token);
}