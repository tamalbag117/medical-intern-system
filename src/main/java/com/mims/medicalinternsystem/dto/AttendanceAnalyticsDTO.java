package com.mims.medicalinternsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceAnalyticsDTO {

    private long totalPresent;

    private long totalLate;

    private long totalAbsent;

    private long totalWorkedMinutes;

    private double averageWorkedHours;
}
