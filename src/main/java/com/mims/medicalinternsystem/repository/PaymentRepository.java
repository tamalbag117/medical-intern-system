package com.mims.medicalinternsystem.repository;

import com.mims.medicalinternsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
