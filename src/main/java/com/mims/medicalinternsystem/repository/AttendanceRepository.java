package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.Attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByInternEmailAndDate(
            String internEmail,
            LocalDate date
    );

    List<Attendance> findByInternEmailOrderByDateDesc(
            String internEmail
    );
}