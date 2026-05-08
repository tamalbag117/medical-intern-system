package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.AttendanceAnalyticsDTO;
import com.mims.medicalinternsystem.dto.GeoAttendanceRequest;

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

    /* =========================================================
       ✅ HOSPITAL GPS LOCATION
    ========================================================= */

    private static final double HOSPITAL_LAT = 6.9271;

    private static final double HOSPITAL_LNG = 79.8612;

    private static final double MAX_DISTANCE_METERS = 500000;

    /* =========================================================
       ✅ NORMAL CHECK IN
    ========================================================= */

    public Attendance checkIn() {

        return createAttendance(
                null,
                null,
                false,
                null
        );
    }

    /* =========================================================
       ✅ GEO CHECK IN
    ========================================================= */

    public Attendance geoCheckIn(
            GeoAttendanceRequest request
    ) {

        double distance =
                calculateDistanceMeters(
                        HOSPITAL_LAT,
                        HOSPITAL_LNG,
                        request.getLatitude(),
                        request.getLongitude()
                );

        if (distance > MAX_DISTANCE_METERS) {

            throw new IllegalStateException(
                    "You are outside hospital premises"
            );
        }

        return createAttendance(
                request.getLatitude(),
                request.getLongitude(),
                true,
                distance
        );
    }

    /* =========================================================
       ✅ COMMON ATTENDANCE CREATION
    ========================================================= */

    private Attendance createAttendance(

            Double latitude,

            Double longitude,

            Boolean geoVerified,

            Double distance

    ) {

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

        // ✅ SHIFT
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

        // ✅ LATE DETECTION
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

                        /* =========================
                           ✅ GEOLOCATION
                        ========================= */

                        .latitude(latitude)

                        .longitude(longitude)

                        .geoVerified(geoVerified)

                        .distanceFromHospital(distance)

                        .build();

        Attendance saved =
                repository.save(attendance);

        // ✅ HR WARNING
        if (lateMinutes >= 30) {

            System.out.println(
                    "WARNING: "
                            + email
                            + " checked in very late"
            );
        }

        return saved;
    }

    /* =========================================================
       ✅ CHECK OUT
    ========================================================= */

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

    /* =========================================================
       ✅ MY ATTENDANCE
    ========================================================= */

    public List<Attendance> myAttendance() {

        return repository
                .findByInternEmailOrderByDateDesc(
                        currentUser()
                );
    }

    /* =========================================================
       ✅ ALL ATTENDANCE
    ========================================================= */

    public List<Attendance> allAttendance() {

        return repository.findAll();
    }

    /* =========================================================
       ✅ ANALYTICS
    ========================================================= */

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

    /* =========================================================
       ✅ CURRENT USER
    ========================================================= */

    private String currentUser() {

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return auth.getName();
    }

    /* =========================================================
       ✅ DISTANCE CALCULATOR
    ========================================================= */

    private double calculateDistanceMeters(

            double lat1,

            double lon1,

            double lat2,

            double lon2

    ) {

        double earthRadius = 6371000;

        double dLat =
                Math.toRadians(lat2 - lat1);

        double dLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2)
                        * Math.sin(dLat / 2)
                        +
                        Math.cos(Math.toRadians(lat1))
                                *
                                Math.cos(Math.toRadians(lat2))
                                *
                                Math.sin(dLon / 2)
                                *
                                Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return earthRadius * c;
    }
}