package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.AuditLog;
import com.mims.medicalinternsystem.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository repo;

    public void log(String action, String email) {

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEmail(email);
        log.setTimestamp(LocalDateTime.now());

        repo.save(log);
    }
}