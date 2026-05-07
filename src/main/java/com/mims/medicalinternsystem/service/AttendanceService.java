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
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository repository;

    // ✅ CHECK IN
    public Attendance checkIn() {

        String email = getCurrentUser();

        LocalDate today = LocalDate.now();

        repository.findByInternEmailAndDate(email, today)
                .ifPresent(a -> {
                    throw new RuntimeException(
                            "Already checked in today"
                    );
                });

        LocalDateTime now = LocalDateTime.now();

        String status =
                now.toLocalTime().isAfter(LocalTime.of(9, 30))
                        ? "LATE"
                        : "PRESENT";

        Attendance attendance =
                Attendance.builder()
                        .internEmail(email)
                        .date(today)
                        .checkInTime(now)
                        .status(status)
                        .workedMinutes(0L)
                        .build();

        return repository.save(attendance);
    }

    // ✅ CHECK OUT
    public Attendance checkOut() {

        String email = getCurrentUser();

        Attendance attendance =
                repository.findByInternEmailAndDate(
                        email,
                        LocalDate.now()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Check in first"
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

    // ✅ MY HISTORY
    public List<Attendance> myAttendance() {

        return repository.findByInternEmailOrderByDateDesc(
                getCurrentUser()
        );
    }

    // ✅ ALL
    public List<Attendance> allAttendance() {
        return repository.findAll();
    }

    // ✅ CURRENT USER
    private String getCurrentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}