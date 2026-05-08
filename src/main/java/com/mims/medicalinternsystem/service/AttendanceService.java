package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.AttendanceAnalyticsDTO;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.entity.User;

import com.mims.medicalinternsystem.enums.ShiftType;

import com.mims.medicalinternsystem.repository.AttendanceRepository;
import com.mims.medicalinternsystem.repository.UserRepository;

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

    private final UserRepository userRepository;

    private final ShiftService shiftService;

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

        // ✅ USER
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->

                                new IllegalStateException(
                                        "User not found"
                                )
                        );

        // ✅ DEFAULT SHIFT
        ShiftType shift =
                user.getShiftType() == null
                        ? ShiftType.MORNING
                        : user.getShiftType();

        LocalDateTime now =
                LocalDateTime.now();

        // ✅ SHIFT START
        LocalDateTime shiftStart =
                LocalDate.now()
                        .atTime(
                                shiftService
                                        .shiftStart(shift)
                        );

        // ✅ LATE CHECK
        boolean late =
                now.isAfter(shiftStart);

        long lateMinutes = 0;

        if (late) {

            lateMinutes =
                    Duration.between(
                            shiftStart,
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

                        .shiftName(
                                shift.name()
                        )

                        .overtimeMinutes(0L)

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

        // ✅ WORKED MINUTES
        long workedMinutes =
                Duration.between(
                        attendance.getCheckInTime(),
                        out
                ).toMinutes();

        attendance.setWorkedMinutes(
                workedMinutes
        );

        // ✅ SHIFT
        ShiftType shift =
                attendance.getShiftName() == null
                        ? ShiftType.MORNING
                        : ShiftType.valueOf(
                        attendance.getShiftName()
                );

        // ✅ SHIFT END
        LocalDateTime shiftEnd =
                LocalDate.now()
                        .atTime(
                                shiftService
                                        .shiftEnd(shift)
                        );

        // ✅ OVERTIME
        long overtime = 0;

        if (out.isAfter(shiftEnd)) {

            overtime =
                    Duration.between(
                            shiftEnd,
                            out
                    ).toMinutes();
        }

        attendance.setOvertimeMinutes(
                overtime
        );

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

        long overtimeMinutes =
                list.stream()
                        .mapToLong(a ->

                                a.getOvertimeMinutes() == null
                                        ? 0
                                        : a.getOvertimeMinutes()
                        )
                        .sum();

        double avgHours =
                list.isEmpty()
                        ? 0
                        : (totalMinutes / 60.0) / list.size();

        double overtimeHours =
                overtimeMinutes / 60.0;

        return new AttendanceAnalyticsDTO(
                present,
                late,
                absent,
                totalMinutes,
                avgHours,
                overtimeMinutes,
                overtimeHours
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