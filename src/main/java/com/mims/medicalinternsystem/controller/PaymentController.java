package com.mims.medicalinternsystem.controller;

import com.mims.medicalinternsystem.entity.Payment;
import com.mims.medicalinternsystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService service;

    // 💳 Create payment
    @PostMapping("/create")
    public Payment create(@RequestParam String email,
                          @RequestParam Double amount) {
        return service.createPayment(email, amount);
    }

    // 💳 Process payment
    @PostMapping("/process")
    public Payment process(@RequestParam Long id,
                           @RequestParam boolean success) {
        return service.processPayment(id, success);
    }
}
