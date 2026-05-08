package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.FaceEnrollmentRequest;
import com.mims.medicalinternsystem.dto.FaceVerificationRequest;
import com.mims.medicalinternsystem.dto.FaceVerificationResponse;

import com.mims.medicalinternsystem.service.FaceRecognitionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class FaceRecognitionController {

    private final FaceRecognitionService service;

    /* =========================================================
       ✅ ENROLL FACE
    ========================================================= */

    @PostMapping("/enroll")
    @PreAuthorize(
            "hasRole('INTERN')"
    )
    public ResponseEntity<String> enroll(

            @RequestBody
            FaceEnrollmentRequest request

    ) {

        return ResponseEntity.ok(
                service.enrollFace(request)
        );
    }

    /* =========================================================
       ✅ VERIFY FACE
    ========================================================= */

    @PostMapping("/verify")
    @PreAuthorize(
            "hasRole('INTERN')"
    )
    public ResponseEntity<FaceVerificationResponse> verify(

            @RequestBody
            FaceVerificationRequest request

    ) {

        return ResponseEntity.ok(
                service.verifyFace(request)
        );
    }
}