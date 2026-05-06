package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.LeaveRequest;
import com.mims.medicalinternsystem.service.LeaveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService service;

    // ✅ APPLY
    @PostMapping
    public LeaveRequest apply(
            @RequestParam String reason,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {

        return service.apply(
                reason,
                fromDate,
                toDate
        );
    }

    // ✅ MY
    @GetMapping("/my")
    public List<LeaveRequest> myLeaves() {
        return service.myLeaves();
    }

    // ✅ PENDING
    @GetMapping("/pending")
    public List<LeaveRequest> pending() {
        return service.pendingLeaves();
    }

    // ✅ REVIEW
    @PostMapping("/review")
    public LeaveRequest review(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks
    ) {

        return service.review(
                id,
                status,
                remarks
        );
    }
}
