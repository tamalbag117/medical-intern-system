package com.mims.medicalinternsystem.service;


import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository repository;

    // ✅ CHECK IN
    public Attendance checkIn() {

        String email = currentUser();

        LocalDate today = LocalDate.now();

        if (
                repository.findByInternEmailAndDate(
                        email,
                        today
                ).isPresent()
        ) {

            throw new IllegalStateException(
                    "Already checked in today"
            );
        }

        Attendance attendance =
                Attendance.builder()
                        .internEmail(email)
                        .date(today)
                        .checkInTime(LocalDateTime.now())
                        .status("PRESENT")
                        .workedMinutes(0L)
                        .build();

        return repository.save(attendance);
    }

    // ✅ CHECK OUT
    public Attendance checkOut() {

        String email = currentUser();

        Attendance attendance =
                repository.findByInternEmailAndDate(
                        email,
                        LocalDate.now()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Please check in first"
                        )
                );

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException(
                    "Already checked out"
            );
        }

        LocalDateTime out = LocalDateTime.now();

        attendance.setCheckOutTime(out);

        long minutes =
                Duration.between(
                        attendance.getCheckInTime(),
                        out
                ).toMinutes();

        attendance.setWorkedMinutes(minutes);

        return repository.save(attendance);
    }

    // ✅ MY ATTENDANCE
    public List<Attendance> myAttendance() {

        return repository
                .findByInternEmailOrderByDateDesc(
                        currentUser()
                );
    }

    // ✅ ALL
    public List<Attendance> allAttendance() {
        return repository.findAll();
    }

    // ✅ USER
    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}
