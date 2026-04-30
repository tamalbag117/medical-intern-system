package com.mims.medicalinternsystem.dto;

public class ActivityRequest {

    private String patientName;
    private String task;

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
}