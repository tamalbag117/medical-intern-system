package com.mims.medicalinternsystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    private String reason;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String remarks;
}