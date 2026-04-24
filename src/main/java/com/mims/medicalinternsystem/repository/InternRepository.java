package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.Intern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternRepository extends JpaRepository<Intern, Long> {
    Optional<Intern> findByEmail(String email);
}