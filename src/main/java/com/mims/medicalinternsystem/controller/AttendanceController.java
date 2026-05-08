package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.AttendanceAnalyticsDTO;
import com.mims.medicalinternsystem.dto.GeoAttendanceRequest;

import com.mims.medicalinternsystem.entity.Attendance;

import com.mims.medicalinternsystem.service.AttendanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;

    /* =========================================================
       ✅ NORMAL CHECK IN
    ========================================================= */

    @PostMapping("/checkin")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> checkIn() {

        return ResponseEntity.ok(
                service.checkIn()
        );
    }

    /* =========================================================
       ✅ GEO CHECK IN
    ========================================================= */

    @PostMapping("/geo-checkin")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> geoCheckIn(
            @RequestBody GeoAttendanceRequest request
    ) {

        return ResponseEntity.ok(
                service.geoCheckIn(request)
        );
    }

    /* =========================================================
       ✅ CHECK OUT
    ========================================================= */

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> checkOut() {

        return ResponseEntity.ok(
                service.checkOut()
        );
    }

    /* =========================================================
       ✅ MY ATTENDANCE
    ========================================================= */

    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<List<Attendance>> my() {

        return ResponseEntity.ok(
                service.myAttendance()
        );
    }

    /* =========================================================
       ✅ ALL ATTENDANCE
    ========================================================= */

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<Attendance>> all() {

        return ResponseEntity.ok(
                service.allAttendance()
        );
    }

    /* =========================================================
       ✅ ANALYTICS
    ========================================================= */

    @GetMapping("/analytics")
    @PreAuthorize(
            "hasAnyRole('INTERN','ADMIN','DOCTOR')"
    )
    public ResponseEntity<AttendanceAnalyticsDTO> analytics() {

        return ResponseEntity.ok(
                service.analytics()
        );
    }
}