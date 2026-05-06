package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.Attendance;
import com.mims.medicalinternsystem.service.AttendanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    @PostMapping("/checkin")
    public Attendance checkIn() {
        return service.checkIn();
    }

    @PostMapping("/checkout")
    public Attendance checkOut() {
        return service.checkOut();
    }

    @GetMapping("/me")
    public List<Attendance> myAttendance() {
        return service.myAttendance();
    }

    @GetMapping("/today")
    public List<Attendance> todayAttendance() {
        return service.todayAttendance();
    }
}
