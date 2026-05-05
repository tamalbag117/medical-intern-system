package com.mims.medicalinternsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class ActivityRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Task is required")
    private String task;

    private String medicalReason;
    private String remarks;

    public String getPatientName() { return patientName; }
    public String getTask() { return task; }
    public String getMedicalReason() { return medicalReason; }
    public String getRemarks() { return remarks; }

    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setTask(String task) { this.task = task; }
    public void setMedicalReason(String medicalReason) { this.medicalReason = medicalReason; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}