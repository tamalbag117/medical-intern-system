package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.AttendanceAnalyticsDTO;
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

        // ✅ ALREADY CHECKED IN
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

        LocalDateTime now = LocalDateTime.now();

        // ✅ OFFICE START TIME
        LocalDateTime officeTime =
                LocalDate.now()
                        .atTime(9, 0);

        boolean late =
                now.isAfter(officeTime);

        long lateMinutes = 0;

        // ✅ CALCULATE LATE MINUTES
        if (late) {

            lateMinutes =
                    Duration.between(
                            officeTime,
                            now
                    ).toMinutes();
        }

        Attendance attendance =
                Attendance.builder()
                        .internEmail(email)
                        .date(today)
                        .checkInTime(now)
                        .status(
                                late
                                        ? "LATE"
                                        : "PRESENT"
                        )
                        .lateMarked(late)
                        .lateMinutes(lateMinutes)
                        .workedMinutes(0L)
                        .autoMarked(false)
                        .build();

        Attendance saved =
                repository.save(attendance);

        // ✅ SEVERE LATE WARNING
        if (lateMinutes >= 30) {

            System.out.println(
                    "WARNING: "
                            + email
                            + " checked in very late"
            );
        }

        return saved;
    }

    // ✅ CHECK OUT
    public Attendance checkOut() {

        String email = currentUser();

        Attendance attendance =
                repository.findByInternEmailAndDate(
                        email,
                        LocalDate.now()
                ).orElseThrow(() ->

                        new IllegalStateException(
                                "Please check in first"
                        )
                );

        // ✅ ALREADY CHECKED OUT
        if (attendance.getCheckOutTime() != null) {

            throw new IllegalStateException(
                    "Already checked out"
            );
        }

        LocalDateTime out =
                LocalDateTime.now();

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

    // ✅ ALL ATTENDANCE
    public List<Attendance> allAttendance() {

        return repository.findAll();
    }

    // ✅ ANALYTICS
    public AttendanceAnalyticsDTO analytics() {

        String email = currentUser();

        List<Attendance> list =
                repository.findByInternEmailOrderByDateDesc(
                        email
                );

        long present =
                repository.countByInternEmailAndStatus(
                        email,
                        "PRESENT"
                );

        long late =
                repository.countByInternEmailAndStatus(
                        email,
                        "LATE"
                );

        long absent =
                repository.countByInternEmailAndStatus(
                        email,
                        "ABSENT"
                );

        long totalMinutes =
                list.stream()
                        .mapToLong(a ->

                                a.getWorkedMinutes() == null
                                        ? 0
                                        : a.getWorkedMinutes()
                        )
                        .sum();

        double avgHours =
                list.isEmpty()
                        ? 0
                        : (totalMinutes / 60.0) / list.size();

        return new AttendanceAnalyticsDTO(
                present,
                late,
                absent,
                totalMinutes,
                avgHours
        );
    }

    // ✅ CURRENT USER
    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }
}