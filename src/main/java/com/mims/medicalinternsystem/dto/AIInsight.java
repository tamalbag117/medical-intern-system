package com.mims.medicalinternsystem.dto;



public class AIInsight {

    private String type;     // INFO / WARN / CRITICAL
    private String message;

    public AIInsight(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() { return type; }
    public String getMessage() { return message; }

    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
}
