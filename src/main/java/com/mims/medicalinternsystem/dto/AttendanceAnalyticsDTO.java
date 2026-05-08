package com.mims.medicalinternsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceAnalyticsDTO {

    // ✅ PRESENT DAYS
    private long totalPresent;

    // ✅ LATE DAYS
    private long totalLate;

    // ✅ ABSENT DAYS
    private long totalAbsent;

    // ✅ TOTAL WORK MINUTES
    private long totalWorkedMinutes;

    // ✅ AVG HOURS
    private double averageWorkedHours;

    // ✅ NEW
    private long overtimeMinutes;

    // ✅ NEW
    private double overtimeHours;
}