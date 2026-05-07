package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.dto.LeaveRequestDTO;
import com.mims.medicalinternsystem.entity.LeaveRequest;
import com.mims.medicalinternsystem.service.LeaveService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService service;

    // ✅ APPLY LEAVE
    @PostMapping
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<LeaveRequest> apply(
            @Valid
            @RequestBody
            LeaveRequestDTO dto
    ) {

        return ResponseEntity.ok(
                service.apply(dto)
        );
    }

    // ✅ MY LEAVES
    @GetMapping("/my")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<List<LeaveRequest>> my() {

        return ResponseEntity.ok(
                service.myLeaves()
        );
    }

    // ✅ ALL LEAVES
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<LeaveRequest>> all() {

        return ResponseEntity.ok(
                service.allLeaves()
        );
    }

    // ✅ APPROVE
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<LeaveRequest> approve(
            @PathVariable Long id,
            @RequestParam(required = false)
            String remarks
    ) {

        return ResponseEntity.ok(
                service.approve(
                        id,
                        remarks
                )
        );
    }

    // ✅ REJECT
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<LeaveRequest> reject(
            @PathVariable Long id,
            @RequestParam(required = false)
            String remarks
    ) {

        return ResponseEntity.ok(
                service.reject(
                        id,
                        remarks
                )
        );
    }
}