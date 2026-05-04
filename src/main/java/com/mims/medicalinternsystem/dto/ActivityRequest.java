package com.mims.medicalinternsystem.dto;

public class ActivityRequest {

    private String patientName;
    private String task;
    private String medicalReason;
    private String remarks;

    // getters
    public String getPatientName() { return patientName; }
    public String getTask() { return task; }
    public String getMedicalReason() { return medicalReason; }
    public String getRemarks() { return remarks; }

    // setters
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setTask(String task) { this.task = task; }
    public void setMedicalReason(String medicalReason) { this.medicalReason = medicalReason; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}