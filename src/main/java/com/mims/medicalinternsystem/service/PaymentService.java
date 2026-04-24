package com.mims.medicalinternsystem.service;

import com.mims.medicalinternsystem.entity.Payment;
import com.mims.medicalinternsystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repo;

    @Autowired
    private AuditService auditService;

    // 💳 CREATE PAYMENT (PENDING)
    public Payment createPayment(String email, Double amount) {

        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        Payment payment = new Payment();
        payment.setEmail(email);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());

        return repo.save(payment);
    }

    // 💳 PROCESS PAYMENT (simulate success/failure)
    public Payment processPayment(Long id, boolean success) {

        Payment payment = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("SUCCESS".equals(payment.getStatus())) {
            throw new RuntimeException("Already processed");
        }

        if (success) {
            payment.setStatus("SUCCESS");
        } else {
            payment.setStatus("FAILED");
        }

        payment.setTransactionId(UUID.randomUUID().toString());

        auditService.log("PAYMENT_" + payment.getStatus(), payment.getEmail());

        return repo.save(payment);
    }
}