package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.QrAttendanceRequest;
import com.mims.medicalinternsystem.dto.QrTokenResponse;

import com.mims.medicalinternsystem.entity.Attendance;

import com.mims.medicalinternsystem.service.QrAttendanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrAttendanceController {

    private final QrAttendanceService service;

    /* =========================================================
       ✅ GENERATE QR
    ========================================================= */

    @PostMapping("/generate")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR')"
    )
    public ResponseEntity<QrTokenResponse> generate(

            @RequestParam String shift

    ) {

        return ResponseEntity.ok(
                service.generateQr(shift)
        );
    }

    /* =========================================================
       ✅ QR CHECK IN
    ========================================================= */

    @PostMapping("/checkin")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<Attendance> checkIn(

            @RequestBody
            QrAttendanceRequest request

    ) {

        return ResponseEntity.ok(
                service.qrCheckIn(request)
        );
    }
}