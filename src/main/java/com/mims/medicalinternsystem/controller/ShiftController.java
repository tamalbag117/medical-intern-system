package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.User;
import com.mims.medicalinternsystem.enums.ShiftType;
import com.mims.medicalinternsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final UserRepository repository;

    // ✅ ASSIGN SHIFT
    @PutMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> assignShift(

            @RequestParam String email,

            @RequestParam ShiftType shift
    ) {

        User user =
                repository.findByEmail(email)
                        .orElseThrow();

        user.setShiftType(shift);

        repository.save(user);

        return ResponseEntity.ok(
                "Shift assigned successfully"
        );
    }
}
