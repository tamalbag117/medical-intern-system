package com.mims.medicalinternsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminAnalytics {

    private long totalActivities;

    private long completedActivities;

    private long pendingActivities;

    private long rejectedActivities;

    private long todayAttendance;

    private long lateAttendance;

    private long activeLeaves;

    private long pendingLeaves;
}