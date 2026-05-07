package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.service.AttendanceService;
import com.mims.medicalinternsystem.dto.AttendanceAnalyticsDTO;

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

    // ✅ CHECK IN
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> checkIn() {

        return ResponseEntity.ok(
                service.checkIn()
        );
    }

    // ✅ CHECK OUT
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> checkOut() {

        return ResponseEntity.ok(
                service.checkOut()
        );
    }

    // ✅ MY ATTENDANCE
    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<List<Attendance>> my() {

        return ResponseEntity.ok(
                service.myAttendance()
        );
    }

    // ✅ ADMIN/DOCTOR
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<Attendance>> all() {

        return ResponseEntity.ok(
                service.allAttendance()
        );
    }

    // ✅ ANALYTICS
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<AttendanceAnalyticsDTO> analytics() {

        return ResponseEntity.ok(
                service.analytics()
        );
    }
}