package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
