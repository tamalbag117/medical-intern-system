package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.dto.AdminAnalytics;
import com.mims.medicalinternsystem.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAnalyticsService {

    @Autowired
    private ActivityLogRepository activityRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private LeaveRepository leaveRepo;

    public AdminAnalytics getAnalytics() {

        long total = activityRepo.count();

        long completed =
                activityRepo.findAll()
                        .stream()
                        .filter(a ->
                                "COMPLETED".equals(a.getStatus()))
                        .count();

        long pending =
                activityRepo.findAll()
                        .stream()
                        .filter(a ->
                                "PENDING".equals(a.getStatus()))
                        .count();

        long rejected =
                activityRepo.findAll()
                        .stream()
                        .filter(a ->
                                "REJECTED".equals(a.getStatus()))
                        .count();

        long attendance =
                attendanceRepo.findAll()
                        .stream()
                        .filter(a ->
                                "PRESENT".equals(a.getStatus()))
                        .count();

        long late =
                attendanceRepo.findAll()
                        .stream()
                        .filter(a ->
                                "LATE".equals(a.getStatus()))
                        .count();

        long activeLeaves =
                leaveRepo.findAll()
                        .stream()
                        .filter(l ->
                                "APPROVED".equals(l.getStatus()))
                        .count();

        long pendingLeaves =
                leaveRepo.findAll()
                        .stream()
                        .filter(l ->
                                "PENDING".equals(l.getStatus()))
                        .count();

        return new AdminAnalytics(
                total,
                completed,
                pending,
                rejected,
                attendance,
                late,
                activeLeaves,
                pendingLeaves
        );
    }
}