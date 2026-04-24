package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
